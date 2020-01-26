package com.example.rentacar.Model;

public class MessageUserList {
    String partner_id, user_image, user_name, last_message, last_message_time, message_count, user_image_string;

    public MessageUserList() {
    }

    public MessageUserList(String partner_id, String user_image, String user_name, String last_message, String last_message_time, String message_count, String user_image_string) {
        this.user_image = user_image;
        this.user_name = user_name;
        this.last_message = last_message;
        this.last_message_time = last_message_time;
        this.message_count = message_count;
        this.partner_id = partner_id;
        this.user_image_string = user_image_string;
    }

    public String getUser_image_string() {
        return user_image_string;
    }

    public void setUser_image_string(String user_image_string) {
        this.user_image_string = user_image_string;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(String last_message_time) {
        this.last_message_time = last_message_time;
    }

    public String getMessage_count() {
        return message_count;
    }

    public void setMessage_count(String message_count) {
        this.message_count = message_count;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }
}
