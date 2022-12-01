package com.example.dentalqmgmtsys.Models;

public class ReferralModel {
    //private String name;
    private int credits;

    public ReferralModel() {
    }

    public ReferralModel(String name, int credits) {
        //this.name = name;
        this.credits = credits;
    }

//    public String getName() {
//        return name;
//    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
