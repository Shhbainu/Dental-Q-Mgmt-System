package com.example.dqms_admin.Model;

public class Doctors {
    String name;
    boolean availability;

    public Doctors(){

    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }



    public Doctors(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
