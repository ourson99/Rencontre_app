package com.example.rencontre20;

public class User
{
    public String name;
    public String email;
    public String phone;
    public String description;
    public String imageUrl; // URI to the profile image stored in Firebase Storage

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String phone, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }
}
