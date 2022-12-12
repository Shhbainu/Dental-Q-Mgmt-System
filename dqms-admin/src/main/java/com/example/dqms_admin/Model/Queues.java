package com.example.dqms_admin.Model;

public class Queues {
    String patientName, time, service, doctor, date;
    long timeStamp, slot;

    public Queues(){}

    public Queues(String patientName, String date, long slot, long timeStamp, String time, String service, String doctor) {
        this.patientName = patientName;
        this.slot = slot;
        this.time = time;
        this.service = service;
        this.doctor = doctor;
        this.timeStamp = timeStamp;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public long getSlot() {
        return slot;
    }

    public void setSlot(long slot) {
        this.slot = slot;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}
