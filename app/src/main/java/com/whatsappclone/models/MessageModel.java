package com.whatsappclone.models;

public class MessageModel {
    String message,uId;
    long time;

    public MessageModel(String message, String uId, long time) {
        this.message = message;
        this.uId = uId;
        this.time = time;
    }

    public MessageModel(String message, String uId) {
        this.message = message;
        this.uId = uId;
    }
    public MessageModel(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
