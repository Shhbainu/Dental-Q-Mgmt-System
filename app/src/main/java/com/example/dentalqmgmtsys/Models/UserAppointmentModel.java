package com.example.dentalqmgmtsys.Models;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserAppointmentModel {
    private String doctor, date, time, service;

    public UserAppointmentModel() {
    }

    public UserAppointmentModel(String doctor, String date, String time, String service) {
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.service = service;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getService() {
        return service;
    }
}
