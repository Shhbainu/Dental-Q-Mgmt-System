package com.example.dentalqmgmtsys.Interface;

import java.util.List;

public interface IAllDoctorsLoadListener {
    void onAllDoctorsLoadSuccess(List<String> doctorList);
    void onAllDoctorsLoadFailed(String message);
}
