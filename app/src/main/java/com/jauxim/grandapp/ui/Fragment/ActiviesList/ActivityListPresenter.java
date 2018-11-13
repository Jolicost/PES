package com.jauxim.grandapp.ui.Fragment.ActiviesList;

import android.content.Context;

import com.jauxim.grandapp.Utils.DataUtils;
import com.jauxim.grandapp.models.ActivityListItemModel;
import com.jauxim.grandapp.networking.NetworkError;
import com.jauxim.grandapp.networking.Service;

import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ActivityListPresenter {
    private final Service service;
    private final ActivitiesListView view;
    private CompositeSubscription subscriptions;

    public ActivityListPresenter(Service service, ActivitiesListView view) {
        this.service = service;
        this.view = view;
        this.subscriptions = new CompositeSubscription();
    }

    public void getActivityList() {
        view.showWait();
        String auth = DataUtils.getAuthToken(view.getContext());
        Subscription subscription = service.getActivityList(new Service.ActivityListCallback() {
            @Override
            public void onSuccess(List<ActivityListItemModel> activities) {
                view.removeWait();
                view.getActivityListSuccess(activities);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.removeWait();
                view.onFailure(networkError.getMessage());
            }

        },auth);

        subscriptions.add(subscription);
    }
    public void onStop() {
        subscriptions.unsubscribe();
    }
}
