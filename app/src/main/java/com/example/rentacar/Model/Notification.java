package com.example.rentacar.Model;

import java.util.Date;

public class Notification {

    String notification_id, notification_title, notification_from, notification_type, notification_text, status;
    Date date;
    BookingDetails bookingDetails;

    public Notification() {
    }

    public Notification(String notification_from, String notification_title,  String notification_text, String notification_type, Date date, String status, BookingDetails bookingDetails) {
        this.notification_from = notification_from;
        this.notification_type = notification_type;
        this.notification_text = notification_text;
        this.date = date;
        this.status = status;
        this.bookingDetails = bookingDetails;
        this.notification_title = notification_title;
    }

    public String getNotification_from() {
        return notification_from;
    }

    public void setNotification_from(String notification_from) {
        this.notification_from = notification_from;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification_text() {
        return notification_text;
    }

    public void setNotification_text(String notification_text) {
        this.notification_text = notification_text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public BookingDetails getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(BookingDetails bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }
}
