package com.example.dqms_admin.Interface;

import com.example.dqms_admin.Model.Services;

import java.util.List;

public interface IServicesLoadListener {
    void onServicesLoadSuccess(List<Services> servicesList);
    void onServicesLoadFailed(String message);
}
