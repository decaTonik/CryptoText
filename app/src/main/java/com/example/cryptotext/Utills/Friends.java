package com.example.cryptotext.Utills;

public class Friends {
    private String profileImageUrl, username;

    public Friends(String profileImageUrl, String username){
        this.profileImageUrl=profileImageUrl;
        this.username=username;
    }

    public Friends() {
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
