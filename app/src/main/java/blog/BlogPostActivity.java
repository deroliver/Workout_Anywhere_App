package blog;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.practice.derikpc.workoutanywhere.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;

public class BlogPostActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private String youtubeVideoID = "";
    private String title = "";
    private String url = "";
    private String content = "";
    private String API_KEY = "AIzaSyDxojhEz5usnLGpcYFL9468Kn5_NW_8E9E";

    private int RECOVERY_DIALOG_REQUEST = 10;

    YouTubePlayerView youTubePlayerView;

    TextView titleTextView;
    TextView contentTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_post_activity);

        titleTextView = (TextView) findViewById(R.id.blog_post_title_text_view);
        contentTextView = (TextView) findViewById(R.id.blog_post_text_view);

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        System.out.println(url);

        new MyAsyncTask(this).execute(url);
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
        private YouTubePlayer.OnInitializedListener context;

        public MyAsyncTask(YouTubePlayer.OnInitializedListener context) {
            this.context = context;
        }

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

                    /*
                    document.select("br").append("\\n");
                    document.select("p").prepend("\\n\\n");
                    content = document.html().replaceAll("\\\\n", "\n");
                    content = Jsoup.clean(content, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));

                    */

                    Element youTube = document.select("div.esg-media-video").first();
                    youtubeVideoID = youTube.attr("data-youtube");

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
}

