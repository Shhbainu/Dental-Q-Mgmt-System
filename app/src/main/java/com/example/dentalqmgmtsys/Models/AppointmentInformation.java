package com.example.dentalqmgmtsys.Models;

import com.google.firebase.Timestamp;

public class AppointmentInformation {
    private String patientName, patientPhone, time, doctor, service, date;
    private Long slot, timeStamp, newTimeStamp;

    public AppointmentInformation()
    {

    }

    public AppointmentInformation(Long timeStamp, Long newTimeStamp, String patientName, String patientPhone, String time, String doctor, String service, String date, Long slot) {
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.time = time;
        this.doctor = doctor;
        this.service = service;
        this.slot = slot;
        this.date = date;
        this.timeStamp = timeStamp;
        this.newTimeStamp = newTimeStamp;
    }

    public Long getNewTimeStamp() {
        return newTimeStamp;
    }

    public void setNewTimeStamp(Long newTimeStamp) {
        this.newTimeStamp = newTimeStamp;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDone(boolean done) {
    }

    public void seTimestamp(long timestamp) {
    }

}
