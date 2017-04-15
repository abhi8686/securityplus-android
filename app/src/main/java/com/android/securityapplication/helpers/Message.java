package com.android.securityapplication.helpers;

import android.support.annotation.Nullable;

/**
 * Created by rashmi on 30/11/16.
 */

public class Message {
//    public boolean position;
//    public String message, date, type;
//    public String left_image, right_image;
//
//    public Message(){
//
//    }
//    public Message(boolean position, String type, String message, String date, String left_image, String right_image) {
//        super();
//        this.position = position;
//        this.type = type;
//        this.message = message;
//        this.date = date;
//        this.left_image = left_image;
//        this.right_image = right_image;
//    }
private int usersId;
    private String message;
    private String sentAt;
//    private String name;
    private String from, type, image, app;

    public Message(int usersId, String message, String sentAt, String from, String type, @Nullable String image, String app) {
        this.usersId = usersId;
        this.message = message;
        this.sentAt = sentAt;
        this.from = from;
//        this.name = name;
        this.type = type;
        this.image = image;
        this.app = app;
    }

    public int getUsersId() {
        return usersId;
    }

    public String getMessage() {
        return message;
    }

    public String getSentAt() {
        return sentAt;
    }

//    public String getName() {
//        return name;
//    }

    public String getFrom() {
        return from;
    }

    public String getType() {
        return type;
    }
    public String getImage() {
        return image;
    }

    public String getApp(){return app;}
}
