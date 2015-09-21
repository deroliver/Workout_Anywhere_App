package workouts;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.practice.derikpc.workoutanywhere.HomeScreen;
import com.practice.derikpc.workoutanywhere.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import databasetools.UserInfoDatabaseTools;
import user.User;

public class WorkoutPostActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private String youtubeVideoID = null;
    private String youtubeLinkString = null;
    private String title = "";
    private String url = "";
    private String content = "";
    private String API_KEY = "AIzaSyDxojhEz5usnLGpcYFL9468Kn5_NW_8E9E";

    private int RECOVERY_DIALOG_REQUEST = 10;
    private int DIALOG_ID = 0;
    private int year_x, month_x, day_x;
    private Integer position = 0;

    YouTubePlayerView youTubePlayerView;

    TextView titleTextView;
    TextView contentTextView;

    private ImageView like;
    private ImageView completed;

    private Boolean likedWorkout;
    private Boolean completedWorkout;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private UserInfoDatabaseTools uDBTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_post_activity);

        uDBTools = new UserInfoDatabaseTools(this);

        like = (ImageView) findViewById(R.id.wall_post_activity_like_button);
        completed = (ImageView) findViewById(R.id.wall_post_activity_completed_button);
        titleTextView = (TextView) findViewById(R.id.workout_post_title_text_view);
        contentTextView = (TextView) findViewById(R.id.workout_post_text_view);

        new Async().execute();

    }

    private class Async extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = getIntent();
            url = intent.getStringExtra("URL");
            position = intent.getIntExtra("Position", 0);
            likedWorkout = intent.getBooleanExtra("Liked", false);
            completedWorkout = intent.getBooleanExtra("Completed", false);

            System.out.println(url);
            options = getDisplayOptions();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new check().execute();

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (likedWorkout) {
                        imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, like);
                        likedWorkout = false;
                    }
                    else {
                        imageLoader.displayImage("drawable://" + R.drawable.like_button, like);
                        likedWorkout = true;
                    }
                }
            });

            completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (completedWorkout) {
                        imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completed);
                        completedWorkout = false;
                    } else {
                        showDialog(DIALOG_ID);
                        imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completed);
                        completedWorkout = true;
                    }
                }
            });


            new MyAsyncTask().execute(url);
        }
    }


    private class check extends AsyncTask<Void, Void, Boolean[]> {

        @Override
        protected Boolean[] doInBackground(Void... params) {
            Boolean results[] = new Boolean[2];
            if (completedWorkout)
                results[0] = true;
            else
                results[0] = false;


            if (likedWorkout)
                results[1] = true;
            else
                results[1] = false;

            return results;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            if (result[0]) {
                imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completed);
                completedWorkout = true;
            } else {
                imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completed);
                completedWorkout = false;
            }
            if (result[1]) {
                imageLoader.displayImage("drawable://" + R.drawable.like_button, like);
                likedWorkout = true;
            } else {
                imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, like);
                likedWorkout = false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Liked", likedWorkout);
        intent.putExtra("Completed", completedWorkout);
        intent.putExtra("Position", position.toString());
        intent.putExtra("Date", month_x + "/" + day_x + "/" + year_x);
        setResult(RESULT_OK, intent);
        finish();
    }


    public DisplayImageOptions getDisplayOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.loading_error)
                .showImageOnFail(R.drawable.loading_error)
                .delayBeforeLoading(100)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        return  options;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID)
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            Toast.makeText(getApplicationContext(), day_x + "/" + month_x + "/" + year, Toast.LENGTH_LONG).show();
        }
    };

    public void InitializeYouTube() {
        System.out.println(youtubeVideoID);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_video_view);

        if(youtubeVideoID != null)
        {
            try{
                youTubePlayerView.initialize(API_KEY, this);
            }
            catch(IllegalStateException e){
                e.printStackTrace();
            }
        }
        else{
            youTubePlayerView.setVisibility(View.INVISIBLE);//Gone
        }
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("YouTube Error (%1$s)", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            player.cueVideo(youtubeVideoID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_video_view);
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                HttpGet httpPost = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);

                int status = response.getStatusLine().getStatusCode();

                if(status == 200){
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject post = jsonObject.getJSONObject("post");
                    title = post.getString("title");
                    System.out.println(title);

                    content = post.getString("content");

                    Document document  = Jsoup.parse(content);
                    StringBuilder sb = new StringBuilder();

                    for(Element element : document.select("p")) {
                        sb.append(element.text()).append('\n');
                        sb.append('\n');
                    }

                    content = sb.toString().trim();

                    Element youTube = document.select("div.esg-media-video").first();

                    if(!(youTube == null))
                        youtubeVideoID = youTube.attr("data-youtube");

                    else {
                        youTube = document.select("iframe").first();
                        System.out.println(youTube);

                        if(!(youTube == null)) {
                            youtubeLinkString = youTube.attr("src");

                            String[] separated = youtubeLinkString.split("embed/");
                            String second = separated[1];
                            String[] secondSeparation = second.split("wmode");
                            youtubeLinkString = secondSeparation[0];

                            youtubeVideoID = youtubeLinkString.substring(0, youtubeLinkString.length() - 1);
                        }
                    }

                    if(youtubeVideoID == null | youtubeVideoID == "")
                        Toast.makeText(getApplicationContext(), "Could Not Load Video", Toast.LENGTH_LONG).show();

                    return true;
                }

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            titleTextView.setText(title);
            contentTextView.setText(content);
            InitializeYouTube();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.sign_out: {
                signOut();
                return true;
            }

            case R.id.exit_the_app: {
                System.exit(0);
                return true;
            }

            case R.id.home_screen: {
                homeScreen();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        String username = User.getUserName();
        uDBTools.updateSignedInByUsername(username, "false");

        Intent intent = new Intent(this, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void homeScreen() {
        finish();
    }
}
