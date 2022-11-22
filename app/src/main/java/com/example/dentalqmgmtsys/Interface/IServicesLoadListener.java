package com.example.dentalqmgmtsys.Interface;

import com.example.dentalqmgmtsys.Models.Service;

import java.util.List;

public interface IServicesLoadListener {
    void onServicesLoadSuccess(List<Service> serviceList);
    void onServicesLoadFailed(String message);
}
