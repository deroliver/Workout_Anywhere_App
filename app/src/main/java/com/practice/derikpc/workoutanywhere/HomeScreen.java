
package com.practice.derikpc.workoutanywhere;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;

import java.util.ArrayList;
import java.util.HashMap;

import databasetools.CompletedDatabaseTools;
import databasetools.FavoritesDatabaseTools;
import databasetools.UserInfoDatabaseTools;


public class HomeScreen extends FragmentActivity {
    private SecondThread secondThread;

    private FavoritesDatabaseTools fdbTools;
    private CompletedDatabaseTools cDBTools;


    private UserInfoDatabaseTools uDBTools;

    private ArrayList<HashMap<String, String>> users;

    private String signedInUsername = "";
    private String signedInPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        getActionBar().hide();

        uDBTools = new UserInfoDatabaseTools(getApplicationContext());

        users = uDBTools.getAllUsers();

        if(users.size() > 0) {
            for(int i = 0; i < users.size(); i++) {
                if(users.get(i).get("signedIn").equals("true")) {
                    signedInUsername = users.get(i).get("userName");
                    signedInPassword = users.get(i).get("password");
                }
            }
        }


        fdbTools = new FavoritesDatabaseTools(getApplicationContext());
        cDBTools = new CompletedDatabaseTools(getApplicationContext());
        uDBTools = new UserInfoDatabaseTools(getApplicationContext());

        secondThread = new SecondThread();
        secondThread.run();

        Fragment fragment;

        if(signedInUsername != "" && signedInPassword != "") {
            Bundle args = new Bundle();
            args.putString("userName", signedInUsername);
            args.putString("password", signedInPassword);

            fragment = new LogInScreenFragment();
            fragment.setArguments(args);
        } else {
            fragment = new FirstWelcomeFragment();
        }

        FragmentManager fM = getSupportFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        fT.add(R.id.home_screen_activity, fragment);
        fT.addToBackStack("WelcomeScreen");
        fT.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Home Screen", Toast.LENGTH_SHORT).show();
    }

    private class SecondThread extends Thread {
        private boolean stopNow = true;

        public void close() {
            stopNow = false;
        }

        public void run() {
            while(stopNow) {


                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                        .threadPoolSize(20)
                        .threadPriority(Thread.NORM_PRIORITY)
                        .tasksProcessingOrder(QueueProcessingType.FIFO)
                        .memoryCacheSize(25 * 512 * 512)
                        .memoryCacheSizePercentage(15)
                        .discCacheSize(25 * 512 * 512)
                        .discCacheFileCount(100)
                        .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                        .imageDecoder(new BaseImageDecoder(true))
                        .build();

                ImageLoader.getInstance().init(config);

                stopNow = false;
            }
        }
    }

    public void SignInFirst(View view) {
        Fragment fragment = new LogInScreenFragment();
        FragmentTransaction fT = getSupportFragmentManager().beginTransaction();
        fT.replace(R.id.home_screen_activity, fragment);
        fT.addToBackStack("SignIn");
        fT.commit();
    }

    public void BringToLogin(View view) {
        String registerURL = "http://workoutanywhere.net/become-a-member/";

        Intent goToRegister = new Intent(Intent.ACTION_VIEW, Uri.parse(registerURL));

        startActivity(goToRegister);
    }

    @Override
    protected void onDestroy() {
        System.out.println("Home Screen onDestroy Called");
        super.onDestroy();
        secondThread.close();
    }


}


