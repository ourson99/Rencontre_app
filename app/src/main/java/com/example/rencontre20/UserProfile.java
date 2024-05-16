package com.example.rencontre20;

public class UserProfile
{
    public String name;
    public String description;
    public String profileImageUrl;
    public String userID;
    public String invitation;

    public UserProfile(String name, String description, String profileImageUrl, String userID) {
        this.name = name;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
        this.userID = userID;
    }

    // Getters
    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return profileImageUrl;
    }

    public String getUserID() {
        return userID;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
}
