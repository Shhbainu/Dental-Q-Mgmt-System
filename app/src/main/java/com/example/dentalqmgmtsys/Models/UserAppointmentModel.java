package com.example.dentalqmgmtsys.Models;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserAppointmentModel {
    private String doctor, date, time, service, id;

    public UserAppointmentModel() {
    }

    public UserAppointmentModel(String doctor, String date, String time, String service, String id) {
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.service = service;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
