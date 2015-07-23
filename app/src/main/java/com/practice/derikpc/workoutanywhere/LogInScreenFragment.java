package com.practice.derikpc.workoutanywhere;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LogInScreenFragment extends Fragment {

    private Button logIn;
    private EditText userName;
    private EditText userPass;

    private String username;
    private String password;
    private String cookie;
    private String accountID;
    private String firstName;
    private String lastName;
    private ImageView avatar;
    private String loginStatus;

    private boolean successfulLogin = false;

    private String workoutAnywhereURL = "https://workoutanywhere.net/";
    private String generateNonce = "api/get_nonce/?controller=user&method=generate_auth_cookie";
    private String generateCookieFirst = "api/user/generate_auth_cookie/?username=";
    private String generateCookieSecond = "&password=";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);

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

            new MyAsyncTask().execute();
        }
    };

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());

            HttpPost httpPostCookie = new HttpPost(workoutAnywhereURL + generateCookieFirst + username + generateCookieSecond + password);


            httpPostCookie.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String result = null;

            try {
                HttpResponse cookieResponse = httpClient.execute(httpPostCookie);
                HttpEntity entity = cookieResponse.getEntity();

                inputStream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                StringBuilder theStringBuilder = new StringBuilder();

                String line = null;

                while ((line = reader.readLine()) != null) {
                    theStringBuilder.append(line + "\n");
                }

                result = theStringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {if(inputStream != null) inputStream.close();}
                catch (Exception e) {}
            }

            JSONObject jsonObject;
            try {
                successfulLogin = false;
                jsonObject = new JSONObject(result);

                loginStatus = jsonObject.getString("status");
                if(loginStatus.equals("ok")) {
                    successfulLogin = true;
                    cookie = jsonObject.getString("cookie");

                    JSONObject userJSONObject = jsonObject.getJSONObject("user");
                    firstName = userJSONObject.getString("firstname");
                    lastName = userJSONObject.getString("lastname");
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            if(successfulLogin) {
                Intent intent = new Intent(getActivity(), HomeScreen.class);

                intent.putExtra("FirstName", firstName);
                intent.putExtra("LastName", lastName);
                intent.putExtra("Cookie", cookie);

                startActivity(intent);
            } else {
                userName.setText("");
                userPass.setText("");

                Toast.makeText(getActivity(), "Incorrect Username or Password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
