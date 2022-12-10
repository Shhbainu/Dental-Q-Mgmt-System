package com.example.dentalqmgmtsys.Common;


import com.example.dentalqmgmtsys.Models.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SERVICE = "SERVICE_SAVE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final int TIME_SLOT_TOTAL = 10;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_CONFIRM_APPOINTMENT = "CONFIRM_APPOINTMENT";
    public static Service currentService;
    public static String currentUser;
    public static String currentPhone;

    public static int step = 0; //Initialized the first step

    public static String currentDoctor;
    public static int currentTimeSlot = -1;
    public static Calendar appointmentDate = Calendar.getInstance();
    public static SimpleDateFormat simpleFormatDate = new SimpleDateFormat("MM_dd_yyyy");
    public static Date timeNow = Calendar.getInstance().getTime();
    public static String appointmentID;

    public static String convertTimeSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "08:00";
            case 1:
                return "09:00";
            case 2:
                return "10:00";
            case 3:
                return "11:00";
            case 4:
                return "12:00";
            case 5:
                return "13:00";
            case 6:
                return "14:00";
            case 7:
                return "15:00";
            case 8:
                return "16:00";
            case 9:
                return "17:00";
            default:
                return "Closed";
        }
    }

}
