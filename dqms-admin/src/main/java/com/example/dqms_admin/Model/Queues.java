package com.example.dqms_admin.Model;

public class Queues {
    String patientName, time, service, doctor;
    int slot;

    public Queues(){}

    public Queues(String patientName, int slot, String time, String service, String doctor) {
        this.patientName = patientName;
        this.slot = slot;
        this.time = time;
        this.service = service;
        this.doctor = doctor;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
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
