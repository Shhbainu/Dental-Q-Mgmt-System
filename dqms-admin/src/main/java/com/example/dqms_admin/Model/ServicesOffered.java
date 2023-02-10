package com.example.dqms_admin.Model;

public class ServicesOffered {

    private String headline, description, priceRange, serviceImage;

    public ServicesOffered() {
    }

    public ServicesOffered(String headline, String description, String priceRange, String serviceImage) {
        this.headline = headline;
        this.description = description;
        this.priceRange = priceRange;
        this.serviceImage = serviceImage;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }
}
