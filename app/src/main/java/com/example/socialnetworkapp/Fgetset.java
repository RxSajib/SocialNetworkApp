package com.example.socialnetworkapp;

public class Fgetset {

    String downloadurl;
    String fullname;
    String statas;

    public Fgetset(){

    }

    public Fgetset(String downloadurl, String fullname, String statas) {
        this.downloadurl = downloadurl;
        this.fullname = fullname;
        this.statas = statas;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatas() {
        return statas;
    }

    public void setStatas(String statas) {
        this.statas = statas;
    }
}
