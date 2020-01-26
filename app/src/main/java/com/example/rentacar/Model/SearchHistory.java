package com.example.rentacar.Model;

public class SearchHistory {

    String district, town, key_word;

    public SearchHistory() {
    }

    public SearchHistory(String district, String town, String key_word) {
        this.district = district;
        this.town = town;
        this.key_word = key_word;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getKey_word() {
        return key_word;
    }

    public void setKey_word(String key_word) {
        this.key_word = key_word;
    }
}
