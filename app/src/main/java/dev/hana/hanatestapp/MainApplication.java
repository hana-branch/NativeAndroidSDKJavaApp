package dev.hana.hanatestapp;

import android.app.Application;
import io.branch.referral.Branch;


public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Branch.expectDelayedSessionInitialization(true);
        branchSDKInit();
    }
    private void branchSDKInit() {
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }
}
