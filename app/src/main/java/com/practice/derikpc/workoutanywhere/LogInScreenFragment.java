package com.practice.derikpc.workoutanywhere;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class LogInScreenFragment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_fragment);
    }

    public void logInAccount(View view) {

        Intent intent = new Intent(getApplication(), HomeScreen.class);

        startActivity(intent);
    }

    public static class PlaceHolderFragmnet extends Fragment

    {

        public PlaceHolderFragmnet() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.sign_in_fragment, container, false);


            return rootView;
        }
    }
}
