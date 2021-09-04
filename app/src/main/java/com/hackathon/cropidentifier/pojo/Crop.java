package com.hackathon.cropidentifier.pojo;

public class Crop {

    String id;
    String image;
    String desc;
    float latitude;
    float longitude;
    String address;

    public Crop() {
    }

    public Crop(String image, String desc, float latitude, float longitude, String address) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.image = image;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address="Not Available";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
