package com.practice.derikpc.workoutanywhere;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import workouts.Workouts;


public class HomeScreen extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do loading data here

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI here
                        setContentView(R.layout.home_screen);
                    }
                });
            }
        }).start();
    }
}


