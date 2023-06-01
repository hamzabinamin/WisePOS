package com.app.wisepos;

import android.app.Application;

import com.onesignal.OneSignal;


public class ApplicationClass extends Application {
    String ONESIGNAL_APP_ID = "c9529754-ce59-44d3-b4d2-3983d660cca9";

    @Override
    public void onCreate() {
        super.onCreate();
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();
    }
}
