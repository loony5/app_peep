package com.example.carpe.peep;

public class User {

    private String userEmail;
    private String userName;
    private String userImage;
    private String userID;

    public User(){

    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User(String userEmail, String userName, String userImage, String userID) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userImage = userImage;
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
