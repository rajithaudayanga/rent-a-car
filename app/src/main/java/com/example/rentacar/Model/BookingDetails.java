package com.example.rentacar.Model;

public class BookingDetails {

    String id, booked_by, vehicle_id, from, to, service_id,price, status;

    public BookingDetails() {
    }

    public BookingDetails(String id, String booked_by, String vehicle_id, String from, String to, String service_id,String price, String status) {
        this.vehicle_id = vehicle_id;
        this.from = from;
        this.to = to;
        this.booked_by = booked_by;
        this.service_id = service_id;
        this.status = status;
        this.price = price;
        this.id = id;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBooked_by() {
        return booked_by;
    }

    public void setBooked_by(String booked_by) {
        this.booked_by = booked_by;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
