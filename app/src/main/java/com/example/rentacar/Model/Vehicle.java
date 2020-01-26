package com.example.rentacar.Model;

public class Vehicle {

    String id, name, brand, model, color, num_of_passengers, price_per_day, distance_per_day, price_extra_hour, price_extra_km, description, image_1_url, image_2_url, image_3_url, image_4_url, status, vehicle_id, service_id;

    public Vehicle() {
    }

    public Vehicle(String name, String brand, String model, String color, String num_of_passengers, String price_per_day, String distance_per_day, String price_extra_hour, String price_extra_km, String description, String image_1_url, String image_2_url, String image_3_url, String image_4_url, String status, String vehicle_id) {
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.num_of_passengers = num_of_passengers;
        this.price_per_day = price_per_day;
        this.distance_per_day = distance_per_day;
        this.price_extra_hour = price_extra_hour;
        this.price_extra_km = price_extra_km;
        this.description = description;
        this.image_1_url = image_1_url;
        this.image_2_url = image_2_url;
        this.image_3_url = image_3_url;
        this.image_4_url = image_4_url;
        this.status = status;
        this.vehicle_id = vehicle_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNum_of_passengers() {
        return num_of_passengers;
    }

    public void setNum_of_passengers(String num_of_passengers) {
        this.num_of_passengers = num_of_passengers;
    }

    public String getPrice_per_day() {
        return price_per_day;
    }

    public void setPrice_per_day(String price_per_day) {
        this.price_per_day = price_per_day;
    }

    public String getDistance_per_day() {
        return distance_per_day;
    }

    public void setDistance_per_day(String distance_per_day) {
        this.distance_per_day = distance_per_day;
    }

    public String getPrice_extra_hour() {
        return price_extra_hour;
    }

    public void setPrice_extra_hour(String price_extra_hour) {
        this.price_extra_hour = price_extra_hour;
    }

    public String getPrice_extra_km() {
        return price_extra_km;
    }

    public void setPrice_extra_km(String price_extra_km) {
        this.price_extra_km = price_extra_km;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_1_url() {
        return image_1_url;
    }

    public void setImage_1_url(String image_1_url) {
        this.image_1_url = image_1_url;
    }

    public String getImage_2_url() {
        return image_2_url;
    }

    public void setImage_2_url(String image_2_url) {
        this.image_2_url = image_2_url;
    }

    public String getImage_3_url() {
        return image_3_url;
    }

    public void setImage_3_url(String image_3_url) {
        this.image_3_url = image_3_url;
    }

    public String getImage_4_url() {
        return image_4_url;
    }

    public void setImage_4_url(String image_4_url) {
        this.image_4_url = image_4_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }
}
