package com.example.cryptotext.Utills;

public class Users {
    private String name, city, age, ImageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public Users(String name, String city, String age, String imageUrl) {
        this.name = name;
        this.city = city;
        this.age = age;
        ImageUrl = imageUrl;
    }

    public Users() {
    }
}
