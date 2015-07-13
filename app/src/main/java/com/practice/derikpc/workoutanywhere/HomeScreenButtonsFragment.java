package com.practice.derikpc.workoutanywhere;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import workouts.Workouts;

public class HomeScreenButtonsFragment extends Fragment {

    private static ImageButton myProfile;
    private static ImageButton workouts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_buttons, container, false);

        myProfile = (ImageButton) view.findViewById(R.id.my_profile_home_button);
        workouts = (ImageButton) view.findViewById(R.id.workouts_home_button);

        myProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfile.class);
                startActivity(intent);
            }
        });

        workouts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Workouts.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void showWorkoutTracker(View view) {

        Intent intent = new Intent(getActivity(), WorkoutTracker.class);

        startActivity(intent);

    }

    public void showMyProfile(View view) {

        Intent intent = new Intent(getActivity(), UserProfile.class);

        startActivity(intent);
    }

    public void showChooseAWorkout(View view) {

        Intent intent = new Intent(getActivity(), Workouts.class);

        startActivity(intent);
    }


    public static class WelcomeSignInFragment extends Fragment {

        public WelcomeSignInFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {

            View rootView = inflater.inflate(R.layout.welcome_sign_in, container, false);

            return rootView;
        }
    }

    public static class LogInScreenFragment extends Fragment {

        public LogInScreenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {

            View rootView = inflater.inflate(R.layout.sign_in_fragment, container, false);

            return rootView;
        }
    }
}
