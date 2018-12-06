package com.jauxim.grandapp.ui.Activity.Main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jauxim.grandapp.R;
import com.jauxim.grandapp.Utils.DataUtils;
import com.jauxim.grandapp.Utils.SingleShotLocationProvider;
import com.jauxim.grandapp.models.UserModel;
import com.jauxim.grandapp.networking.Service;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by ennur on 6/25/16.
 */
public class MainPresenter {
    private final Service service;
    private final MainView view;
    private CompositeSubscription subscriptions;

    public MainPresenter(Service service, MainView view) {
        this.service = service;
        this.view = view;
        this.subscriptions = new CompositeSubscription();
    }

    public void onStop() {
        subscriptions.unsubscribe();
    }

    public void updateLocation() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            SingleShotLocationProvider.requestSingleUpdate(view.getContext(),
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            DataUtils.saveLocation(view.getContext(), location);
                        }
                    });

        } else if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(view.getContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1234);
        }
    }

    public void logout() {
        view.showWait();
        DataUtils.deleteAuthToken((Context) view);
        view.removeWait();
        view.showLogoutSuccess(R.string.logout_success);
        view.redirectTologin();
    }

    public void showProfile() {
        String userId = DataUtils.getUserInfo((Context)view).getId();
        view.viewProfile(userId);
    }
}
