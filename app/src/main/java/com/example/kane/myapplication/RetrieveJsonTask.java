package com.example.kane.myapplication;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RetrieveJsonTask extends AsyncTask<RetrieveJsonParam, Void, Void> {

    JsonReceivedCommand command;
    JSONObject jsonObject;
    protected Void doInBackground(RetrieveJsonParam... param)
    {
        HttpURLConnection urlConnection = null;
        command = param[0].command;

        try {
            URL url = new URL(param[0].url);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            urlConnection.setDoOutput(true);

            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            char[] buffer = new char[1024];

            String jsonString = new String();

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            jsonString = sb.toString();

            System.out.println("JSON: " + jsonString);

            jsonObject = new JSONObject(jsonString);
        }
        catch (Exception e)
        {

        }
        return null;
    }


    protected void onPostExecute(Void v)
    {
        command.jsonReceived(jsonObject);
    }
}
