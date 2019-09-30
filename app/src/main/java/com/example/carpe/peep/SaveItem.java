package com.example.carpe.peep;

public class SaveItem {

    private String UserID, UserName, UserPassword;
    boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        this.UserPassword = userPassword;
    }

    public SaveItem(String userID, String userName, String userPassword) {
        this.UserID = userID;
        UserName = userName;
        UserPassword = userPassword;
    }
}
