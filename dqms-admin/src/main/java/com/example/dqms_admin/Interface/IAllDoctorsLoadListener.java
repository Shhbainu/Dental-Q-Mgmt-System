package com.example.dqms_admin.Interface;

import java.util.List;

public interface IAllDoctorsLoadListener {
    void onAllDoctorsLoadSuccess(List<String> doctorNameList);
    void onAllDoctorsLoadFailed(String message);
}
