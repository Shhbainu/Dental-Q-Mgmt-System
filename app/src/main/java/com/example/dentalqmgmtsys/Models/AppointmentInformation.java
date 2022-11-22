package com.example.dentalqmgmtsys.Models;

public class AppointmentInformation {
    private String patientName, patientPhone, time, doctor, service;
    private Long slot;

    public AppointmentInformation()
    {

    }

    public AppointmentInformation(String patientName, String patientPhone, String time, String doctor, String service, Long slot) {
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.time = time;
        this.doctor = doctor;
        this.service = service;
        this.slot = slot;
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
}
