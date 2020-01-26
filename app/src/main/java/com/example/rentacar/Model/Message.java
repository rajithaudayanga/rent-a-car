package com.example.rentacar.Model;

import java.util.Date;

public class Message {

    private String id, sender_id, message, status, receiver_id;
    private Date time;

    public Message() {
    }

    public Message(String id, String sender_id, String receiver_id, String message, Date time, String status) {
        this.id = id;
        this.sender_id = sender_id;
        this.message = message;
        this.time = time;
        this.status = status;
        this.receiver_id = receiver_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }
}
