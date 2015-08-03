package com.practice.derikpc.workoutanywhere;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;



public class LogInScreenActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        Fragment fragment = new FirstWelcomeFragment();

        FragmentManager fM = getSupportFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();

        fT.add(R.id.sign_in_activity, fragment);
        fT.commit();
    }

    @Override
    protected void onDestroy() {
        setContentView(null);
        finish();
        super.onDestroy();
    }
}
