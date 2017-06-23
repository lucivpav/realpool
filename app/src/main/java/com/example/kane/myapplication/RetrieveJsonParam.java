package com.example.kane.myapplication;

/**
 * Created by kane on 22.06.2017.
 */

public class RetrieveJsonParam
{
    String url;
    JsonReceivedCommand command;
    RetrieveJsonParam(String url, JsonReceivedCommand command)
    {
        this.url = url;
        this.command = command;
    }
}
