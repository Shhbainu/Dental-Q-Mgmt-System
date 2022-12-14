package com.example.dqms_admin.Interface;

import com.example.dqms_admin.Model.Queues;
import com.example.dqms_admin.Model.Services;

import java.util.List;

public interface IQueuesLoadListener {
    void onQueuesLoadSuccess(List<Queues> queuesList);
    void onQueuesLoadFailed(String message);
}
