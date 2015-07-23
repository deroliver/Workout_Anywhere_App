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
import android.widget.TextView;

public class FirstWelcomeFragment extends Fragment {

    Button loginButton;
    TextView registrationButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_welcome_sign_in_fragment, container, false);

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
            fT.replace(R.id.sign_in_activity, fragment);
            fT.addToBackStack(null);
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
}
