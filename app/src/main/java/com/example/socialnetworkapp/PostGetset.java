package com.example.socialnetworkapp;

public class PostGetset {

    String profileimage;
    String fullname;
    String date;
    String time;
    String descptrion;
    String postimage;


    public PostGetset(){

    }

    public PostGetset(String profileimage, String fullname, String date, String time, String descptrion, String postimage) {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.date = date;
        this.time = time;
        this.descptrion = descptrion;
        this.postimage = postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescptrion() {
        return descptrion;
    }

    public void setDescptrion(String descptrion) {
        this.descptrion = descptrion;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }
}
