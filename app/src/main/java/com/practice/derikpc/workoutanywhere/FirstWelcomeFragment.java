package com.practice.derikpc.workoutanywhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.io.File;

public class FirstWelcomeFragment extends Fragment {

    private Button loginButton;
    private TextView registrationButton;
    private ImageView Logo;
    private View view;

    File img = new File("///android_asset/workoutanywherebyrundlefit.png");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.first_welcome_sign_in_fragment, container, false);

        Logo = (ImageView) view.findViewById(R.id.workout_anywhere_picture_first);

        Picasso.with(getActivity()).load(img).into(Logo);


        loginButton = (Button) view.findViewById(R.id.first_sign_in_button);
        loginButton.setOnClickListener(bringToLogin);

        registrationButton = (TextView) view.findViewById(R.id.register_button);
        registrationButton.setOnClickListener(bringtoRegistration);

        return view;
    }

    public OnClickListener bringToLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment fragment = new LogInScreenFragment();
            FragmentTransaction fT = getFragmentManager().beginTransaction();
            fT.replace(R.id.home_screen_activity, fragment);
            fT.addToBackStack("SignIn");
            fT.commit();
        }
    };

    public OnClickListener bringtoRegistration = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String registerURL = "http://workoutanywhere.net/become-a-member/";

            Intent goToRegister = new Intent(Intent.ACTION_VIEW, Uri.parse(registerURL));

            startActivity(goToRegister);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        img = new File("///android_asset/workoutanywherebyrundlefit.png");
        Picasso.with(getActivity()).load(img).into(Logo);
    }


    @Override
    public void onDestroyView() {
        System.out.println("On DestroyView FirstWelcomeScreenFragment Called");
        super.onDestroyView();
        if(img != null) {
            Picasso.with(getActivity()).invalidate(img);
            img = null;
        }

        Logo.setImageBitmap(null);
    }
}
