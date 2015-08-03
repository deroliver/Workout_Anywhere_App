package com.practice.derikpc.workoutanywhere;

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

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;


public class LogInScreenFragment extends Fragment {

    private Button logIn;
    private EditText userName;
    private EditText userPass;
    private ImageView Logo;

    private String username;
    private String password;
    private View view;

    File img = new File("///android_asset/workoutanywherebyrundlefit.png");


    private String workoutAnywhereURL = "https://workoutanywhere.net/";
    private String generateCookieFirst = "api/user/generate_auth_cookie/?username=";
    private String generateCookieSecond = "&password=";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sign_in_fragment, container, false);
        Logo = (ImageView) view.findViewById(R.id.workout_anywhere_picture_second);

        Picasso.with(getActivity()).load(img).into(Logo);


        logIn = (Button) view.findViewById(R.id.logInAccountButton);
        logIn.setOnClickListener(logInListener);

        userName = (EditText) view.findViewById(R.id.user_name_edit_text);
        userPass = (EditText) view.findViewById(R.id.user_password_edit_text);

        return view;
    }

    public View.OnClickListener logInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            username = userName.getText().toString();
            password = userPass.getText().toString();
            System.out.println(username + password);

            MyAsyncTask task = new MyAsyncTask();

            task.setDataDownloadListener(new MyAsyncTask.CookieDownloadListener() {
                @Override
                public void dataDownloadedSuccessfully(String data) {
                    Bundle cookie = new Bundle();
                    cookie.putString("Cookie", data);

                    Fragment fragment = new HomeScreenButtonsFragment();

                    fragment.setArguments(cookie);
                    FragmentManager fM = getFragmentManager();
                    FragmentTransaction fT = fM.beginTransaction();
                    fT.replace(R.id.home_screen_activity, fragment);
                    fT.commit();
                }

                @Override
                public void dataDownloadFailed() {
                    userName.setText("");
                    userPass.setText("");
                    Toast.makeText(getActivity(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                }
            });
            task.execute(workoutAnywhereURL + generateCookieFirst + username + generateCookieSecond + password);
        }
    };



    private static final class MyAsyncTask extends AsyncTask<String, Void, String> {
        CookieDownloadListener listener;

        MyAsyncTask() {}

        public void setDataDownloadListener(CookieDownloadListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpGet httpPost = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);

                int status = response.getStatusLine().getStatusCode();

                if(status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsonObject = new JSONObject(data);

                    jsonObject = new JSONObject(data);


                    System.out.println(jsonObject.getString("cookie"));
                    return jsonObject.getString("cookie");
                }
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if(result != null)
                listener.dataDownloadedSuccessfully(result);
            else
                listener.dataDownloadFailed();
        }

        public static interface CookieDownloadListener {
            void dataDownloadedSuccessfully(String data);
            void dataDownloadFailed();
        }

    }

    @Override
    public void onDestroyView() {
        System.out.println("On DestroyView LogInScreenFragment Called");
        super.onDestroyView();

        if(img != null) {
            Picasso.with(getActivity()).invalidate(img);
            img = null;
        }

        Logo.setImageBitmap(null);
        logIn = null;
        userName = null;
        userPass = null;
    }



}
