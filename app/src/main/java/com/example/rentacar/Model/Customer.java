package com.example.rentacar.Model;

public class Customer {

    String profile_level, user_type, name, mobile, profile_photo;

    public Customer() {
    }

    public Customer(String profile_level, String user_type, String name, String mobile, String profile_photo) {
        this.profile_level = profile_level;
        this.user_type = user_type;
        this.name = name;
        this.mobile = mobile;
        this.profile_photo = profile_photo;
    }

    public String getProfile_level() {
        return profile_level;
    }

    public void setProfile_level(String profile_level) {
        this.profile_level = profile_level;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getprofile_photo() {
        return profile_photo;
    }

    public void setprofile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }
}
