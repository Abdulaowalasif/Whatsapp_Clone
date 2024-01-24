package com.whatsappclone.models;

public class Users {
    String profileImage,userName,email,password,lastMsg,userId;

    public Users(){}
    public Users(String profileImage, String userName, String email, String password, String lastMsg, String userId) {
        this.profileImage = profileImage;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.lastMsg = lastMsg;
        this.userId = userId;
    }

    public Users(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void getUserId(String key) {
    }
}
