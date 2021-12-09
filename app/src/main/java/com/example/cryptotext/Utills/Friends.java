package com.example.cryptotext.Utills;

public class Friends {
    private String ImageUrl, name;

    public Friends() {
    }

    public Friends(String imageUrl, String name) {
        ImageUrl = imageUrl;
        this.name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
