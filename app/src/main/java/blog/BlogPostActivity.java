package blog;


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
import android.view.ViewGroup;
import android.widget.*;

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
import java.util.Calendar;
import java.util.HashMap;

import databasetools.CompletedDatabaseTools;
import databasetools.FavoritesDatabaseTools;
import databasetools.UserInfoDatabaseTools;
import user.User;

public class BlogPostActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private String youtubeVideoID = "";
    private String imageURL = "";
    private String title = "";
    private String url = "";
    private String content = "";
    private String API_KEY = "AIzaSyDxojhEz5usnLGpcYFL9468Kn5_NW_8E9E";

    private Integer position = 0;

    private int RECOVERY_DIALOG_REQUEST = 10;
    private int DIALOG_ID = 0;
    private int year_x, month_x, day_x;

    YouTubePlayerView youTubePlayerView;
    ImageView postImageView;

    TextView titleTextView;
    TextView contentTextView;

    private SecondThread secondThread;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private ImageView like;
    private ImageView completed;

    private boolean likedWorkout = false;
    private boolean completedWorkout = false;

    private UserInfoDatabaseTools uDBTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_post_activity);
        imageLoader = ImageLoader.getInstance();

        uDBTools = new UserInfoDatabaseTools(this);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        like = (ImageView) findViewById(R.id.blog_post_activity_like_button);
        completed = (ImageView) findViewById(R.id.blog_post_activity_completed_button);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_video_view);
        titleTextView = (TextView) findViewById(R.id.blog_post_title_text_view);
        contentTextView = (TextView) findViewById(R.id.blog_post_text_view);
        postImageView = (ImageView) findViewById(R.id.post_image_view);

        secondThread = new SecondThread();
        secondThread.run();
    }

    private class SecondThread extends Thread {
        private boolean stopNow = false;

        public void close() {
            stopNow = false;
        }

        public void run() {
            stopNow = true;
            while(stopNow) {

                Intent intent = getIntent();
                url = intent.getStringExtra("URL");
                position = intent.getIntExtra("Position", 0);
                likedWorkout = intent.getBooleanExtra("Liked", false);
                completedWorkout = intent.getBooleanExtra("Completed", false);

                System.out.println(url);
                options = getDisplayOptions();

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
                        if(completedWorkout) {
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completed);
                            completedWorkout = false;
                        }
                        else {
                            showDialog(DIALOG_ID);
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completed);
                            completedWorkout = true;
                        }
                    }
                });

                new MyAsyncTask().execute(url);

                stopNow = false;
            }
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

                    content = post.getString("content");

                    Document document  = Jsoup.parse(content);
                    StringBuilder sb = new StringBuilder();

                    for(Element element : document.select("p")) {
                        sb.append(element.text()).append('\n');
                        sb.append('\n');
                    }

                    boolean dataGet = false;

                    content = sb.toString().trim();

                    Element youTube = document.select("div.esg-media-video").first();

                    if(!(youTube == null)) {
                        youtubeVideoID = youTube.attr("data-youtube");
                        dataGet =  true;
                    }

                    else {
                        youTube = document.select("iframe").first();

                        if(!(youTube == null)) {
                            String youtubeLinkString = youTube.attr("src");

                            String[] separated = youtubeLinkString.split("embed/");
                            String second = separated[1];
                            String[] secondSeparation = second.split("wmode");
                            youtubeLinkString = secondSeparation[0];

                            youtubeVideoID = youtubeLinkString.substring(0, youtubeLinkString.length() - 1);
                            dataGet = true;
                        }
                    }

                    if(!dataGet) {
                        JSONObject thumbnail_images = post.getJSONObject("thumbnail_images");
                        System.out.println(thumbnail_images.toString());
                        JSONObject image = thumbnail_images.getJSONObject("medium");
                        System.out.println(image.getString("url"));
                        imageURL = image.getString("url");
                    }

                    return dataGet;
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
            if(result) {
                titleTextView.setText(title);
                contentTextView.setText(content);
                InitializeYouTube();
            }
            else {
                titleTextView.setText(title);
                contentTextView.setText(content);
                setYoutubeImage();
            }
        }
    }

    public void setYoutubeImage() {
        ViewGroup.LayoutParams params = new TableLayout.LayoutParams(0, 0, 0.0f);
        youTubePlayerView.setVisibility(View.INVISIBLE);
        youTubePlayerView.setLayoutParams(params);

        postImageView.setVisibility(View.VISIBLE);
        params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 6.0f);
        postImageView.setLayoutParams(params);

        imageLoader.displayImage(imageURL, postImageView, options);
    }

    @Override
    protected void onDestroy() {
        secondThread.close();
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

