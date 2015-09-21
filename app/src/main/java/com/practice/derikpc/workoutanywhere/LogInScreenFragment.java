package com.practice.derikpc.workoutanywhere;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import databasetools.UserInfoDatabaseTools;
import user.User;


public class LogInScreenFragment extends Fragment {

    private Button logIn;
    private EditText userName;
    private EditText userPass;
    private ImageView Logo;

    private String username;
    private String password;
    private View view;

    private String FNAME = null;
    private String LNAME = null;
    private String PASSWORD = null;
    private String ACCOUNT_STATUS = null;

    SecondThread secondThread;

    ImageLoader imageLoader;

    private UserInfoDatabaseTools userDBTools;

    private String workoutAnywhereURL = "http://workoutanywhere.net/";
    private String generateCookieFirst = "api/user/generate_auth_cookie/?username=";
    private String generateCookieSecond = "&password=";

    private String signedInUsername = "";
    private String signedInPassword = "";

    private UserInfoDatabaseTools userDBtools;

    private Boolean signedInUser = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        userDBTools = new UserInfoDatabaseTools(getActivity());

        Bundle args = getArguments();
        if(args != null) {
            signedInUsername = args.getString("userName");
            signedInPassword = args.getString("password");
            signedInUser = true;
        }

        imageLoader = ImageLoader.getInstance();

        Logo = (ImageView) view.findViewById(R.id.workout_anywhere_picture_second);
        imageLoader.displayImage("drawable://" + R.drawable.workoutanywherebyrundlefit, Logo);


        logIn = (Button) view.findViewById(R.id.logInAccountButton);

        userName = (EditText) view.findViewById(R.id.user_name_edit_text);
        userPass = (EditText) view.findViewById(R.id.user_password_edit_text);

        HashMap<String, String> user = userDBTools.getUserInfoByUserName(signedInUsername);

        if(args != null && signedInUsername != "" && signedInPassword != "" && user != null) {
            userName.setText(signedInUsername);
            userPass.setText(signedInPassword);
            username = signedInUsername;
            password = signedInPassword;

            secondThread = new SecondThread();
            secondThread.run();


        } else {
            secondThread = new SecondThread();
            secondThread.run();
        }

        return view;
    }

    private class SecondThread extends Thread {
        private boolean stopNow = true;

        public void close() {
            stopNow = false;
        }

        public void run() {
            while(stopNow) {
                logIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        username = userName.getText().toString();
                        password = userPass.getText().toString();
                        System.out.println(username + password);

                        MyAsyncTask task = new MyAsyncTask();

                        task.setContext(getActivity());

                        task.setDataDownloadListener(new MyAsyncTask.CookieDownloadListener() {
                            @Override
                            public void dataDownloadedSuccessfully(String data[]) {
                                userDBTools = new UserInfoDatabaseTools(getActivity());
                                Boolean found = false;

                                ArrayList<HashMap<String, String>> users = userDBTools.getAllUsers();
                                if(users.size() > 0) {
                                    for(int i = 0; i < users.size(); i++) {
                                        if (users.get(i).get("userName").equals(username)) {
                                            found = true;
                                        }
                                    }
                                }

                                if(found == false) {
                                    System.out.println("No User");
                                    userDBTools.insertUser(data[1], username, password, "NA", "true", data[3], data[4], "NA");
                                }

                                userDBTools.updateSignedInByUsername(username, "true");
                                User user = User.getInstance();
                                user.initUser(data[1], data[5], username, data[2], data[3], data[4]);


                                Fragment fragment = new HomeScreenButtonsFragment();

                                FragmentManager fM = getFragmentManager();
                                FragmentTransaction fT = fM.beginTransaction();
                                fT.replace(R.id.home_screen_activity, fragment);
                                fT.addToBackStack("HomeScreen");
                                fT.commit();
                            }

                            @Override
                            public void dataDownloadFailed() {
                                userName.setText("");
                                userPass.setText("");
                                Toast.makeText(getActivity(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        });
                        System.out.println(workoutAnywhereURL + generateCookieFirst + username + generateCookieSecond + password);
                        task.execute(workoutAnywhereURL + generateCookieFirst + username + generateCookieSecond + password);
                    }
                });
                if(signedInUser) {
                    logIn.performClick();
                }
                stopNow = false;
            }
        }
    }


    private static final class MyAsyncTask extends AsyncTask<String, Void, String[]> {
        CookieDownloadListener listener;
        HttpClientClass httpClientClass;
        String result = null;
        String USER[];
        Context myContext;
        private ProgressDialog progress;

        MyAsyncTask() {}

        public void setContext(Context context) {
            myContext = context;
        }

        public void setDataDownloadListener(CookieDownloadListener listener) {
            httpClientClass = new HttpClientClass();
            this.listener = listener;
            USER = new String[6];
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(myContext, R.style.MyTheme);
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progress.show();
            super.onPreExecute();
            Toast.makeText(myContext, "Loggin you in...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String[] doInBackground(String... urls) {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(urls[0]);

            httpPost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject user = jsonObject.getJSONObject("user");
                JSONObject status = user.getJSONObject("capabilities");

                USER[0] = jsonObject.getString("cookie");
                USER[1] = user.getString("firstname");
                USER[2] = user.getString("id");
                USER[3] = user.getString("avatar");
                USER[5] = user.getString("lastname");

                Iterator<String> keys = status.keys();
                String userStatus = "";

                while(keys.hasNext()) {
                    userStatus = (String)keys.next();
                }

                if(userStatus.equals("administrator") || userStatus.equals("optimizemember_level1")) {
                    System.out.println("Paid");
                    USER[4] = "Paid";
                }
                else {
                    System.out.println("Free");
                    USER[4] = "Free";
                }

            } catch (Exception e) {

            }

            return USER;
        }

        protected void onPostExecute(String result[]) {
            if(result[0] != null) {
                listener.dataDownloadedSuccessfully(result);

            }
            else
                listener.dataDownloadFailed();

            progress.dismiss();
        }

        private static interface CookieDownloadListener {
            void dataDownloadedSuccessfully(String data[]);
            void dataDownloadFailed();
        }

    }

    @Override
    public void onDestroyView() {
        System.out.println("On DestroyView LogInScreenFragment Called");
        super.onDestroyView();

        Fragment fragment = getFragmentManager().findFragmentByTag("SignIn");
        if(fragment != null)
            getFragmentManager().beginTransaction().remove(fragment).commit();

        Logo.setImageBitmap(null);
        logIn = null;
        userName = null;
        userPass = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }
}
