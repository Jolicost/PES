package com.jauxim.grandapp.ui.Activity.ActivityEmergency;

import com.jauxim.grandapp.models.EmergencyContactsModel;
import com.jauxim.grandapp.models.UserModel;

/**
 * Created by ennur on 6/25/16.
 */
public interface ActivityEmergencyView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void getEmergencyContacts(EmergencyContactsModel emergencyContactsModel);
}
