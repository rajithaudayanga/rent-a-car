package com.example.rentacar.Model;

public class Rent  {

    String id, price, from_date, to_date, vehicle_id, service_id, cus_id, status;

    public Rent() {
    }

    public Rent(String id, String price, String from_date, String to_date, String vehicle_id, String service_id, String cus_id, String status) {
        this.id = id;
        this.price = price;
        this.from_date = from_date;
        this.to_date = to_date;
        this.vehicle_id = vehicle_id;
        this.service_id = service_id;
        this.cus_id = cus_id;
        this.status = status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getCus_id() {
        return cus_id;
    }

    public void setCus_id(String cus_id) {
        this.cus_id = cus_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
