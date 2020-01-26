package com.example.rentacar.Model;

public class Service {

    String id, name, mobile, telephone, profile_photo, location, address, city, profile_level,town, user_type;

    public Service() {
    }

    public Service(String id, String name, String mobile, String telephone, String profile_photo, String location, String address, String city, String profile_level, String town, String user_type) {
        this.name = name;
        this.mobile = mobile;
        this.telephone = telephone;
        this.profile_photo = profile_photo;
        this.location = location;
        this.address = address;
        this.city = city;
        this.profile_level = profile_level;
        this.town = town;
        this.id = id;
        this.user_type = user_type;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfile_level() {
        return profile_level;
    }

    public void setProfile_level(String profile_level) {
        this.profile_level = profile_level;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
