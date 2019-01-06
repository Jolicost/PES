package com.jauxim.grandapp.ui.Activity.Chat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jauxim.grandapp.Utils.DataUtils;
import com.jauxim.grandapp.models.MessageModel;
import com.jauxim.grandapp.models.UserModel;
import com.jauxim.grandapp.networking.NetworkError;
import com.jauxim.grandapp.networking.Service;

import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ChatPresenter {

    private final Service service;
    private CompositeSubscription subscriptions;

    private List<MessageModel> messageHistorial;
    private UserModel userInfo;

    public ChatPresenter(Service service) {
        this.service = service;
        this.subscriptions = new CompositeSubscription();
    }


    public List<MessageModel> getHistorial(String activityId, String messageCount, String auth) {

        Log.d("Log", " Inside Historial 1");

        Subscription subscription = service.getHistorial(activityId, messageCount, new Service.MessageCallback() {
            @Override
            public void onSuccess(List<MessageModel> messageList) {
                messageHistorial = messageList;
                Log.d("Log", " ERROR  1    Inside Historial 1");
            }

            @Override
            public void onError(NetworkError networkError) {
                Log.d("Log", " ERROR  2    Inside Historial 1");
            }
        }, auth);

        subscriptions.add(subscription);

        return messageHistorial;
    }

    public UserModel getProfileInfo(String id, String auth) {
        Subscription subscription = service.getProfileInfo(id, new Service.ProfileInfoCallback() {
            @Override
            public void onSuccess(UserModel userModel) {
                userInfo = userModel;
            }

            @Override
            public void onError(NetworkError networkError) {
                //todo
            }

        }, auth);

        subscriptions.add(subscription);

        return userInfo;
    }
}
