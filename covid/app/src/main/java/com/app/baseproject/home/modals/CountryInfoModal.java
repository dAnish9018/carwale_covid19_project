package com.app.baseproject.home.modals;

public class CountryInfoModal {

    String country,countryCode;
    int totalConfirmed,totalDeath,totalRecovered;

    public CountryInfoModal(String country, String countryCode, int totalConfirmed, int totalDeath, int totalRecovered) {
        this.country = country;
        this.countryCode = countryCode;
        this.totalConfirmed = totalConfirmed;
        this.totalDeath = totalDeath;
        this.totalRecovered = totalRecovered;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public int getTotalConfirmed() {
        return totalConfirmed;
    }

    public int getTotalDeath() {
        return totalDeath;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }
}
