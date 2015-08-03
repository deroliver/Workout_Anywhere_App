package blog;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.practice.derikpc.workoutanywhere.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BlogActivity extends Activity {

    private ArrayList<BlogPost> blogActivityObjects;
    private BlogPostAdapter adapter;
    private ListView listView;

    private File img = new File("///android_asset/workoutanywherebyrundlefit.png");

    private String workoutAnywhereURL = "https://workoutanywhere.net/blog/?json=1";
    private String workoutAnywhereURLMorePages1 = "https://workoutanywhere.net/blog/page/";
    private String workoutAnywhereURLMorePages2 = "/?json=1";
    private String currentURL = workoutAnywhereURL;

    private int numPosts = 0;
    private int pageNumber = 0;
    private int curPostOnPage = 0;

    private boolean isLoading = false;
    private boolean pauseOnScroll = false;
    private boolean pauseOnFling = false;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_activity);
        imageLoader = ImageLoader.getInstance();
        options = getDisplayOptions();

        blogActivityObjects = new ArrayList<BlogPost>();
        new MyAsyncTask().execute(currentURL);

        listView = (ListView) findViewById(R.id.list);

        adapter = new BlogPostAdapter(getApplicationContext(), R.layout.blog_object, blogActivityObjects);

        listView.setAdapter(adapter);
        MultiScrollListener scrolls = new MultiScrollListener();

        AbsListView.OnScrollListener listener1 = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(isLoading)) {
                    numPosts = blogActivityObjects.size();
                    System.out.println(numPosts);
                    if (curPostOnPage == 20) {
                        curPostOnPage = 0;
                        pageNumber = (numPosts / 20) + 1;
                        currentURL = workoutAnywhereURLMorePages1 + pageNumber + workoutAnywhereURLMorePages2;
                        System.out.println(currentURL);
                        new MyAsyncTask().execute(currentURL);
                    } else {
                        curPostOnPage += 10;
                        new MyAsyncTask().execute(currentURL);
                    }
                }
            }
        };

        PauseOnScrollListener listener2 = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);

        scrolls.addScrollListener(listener1);
        scrolls.addScrollListener(listener2);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BlogPostActivity.class);
                intent.putExtra("URL", blogActivityObjects.get(position).getUrl());
                startActivity(intent);
            }
        });
    }


    private static class MultiScrollListener implements AbsListView.OnScrollListener {
        List<AbsListView.OnScrollListener> mListeners = new ArrayList<AbsListView.OnScrollListener>();

        public void addScrollListener(AbsListView.OnScrollListener listener) {
            mListeners.add(listener);
        }

        public void removeScrollListener(AbsListView.OnScrollListener listener) {
            mListeners.remove(listener);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            for(AbsListView.OnScrollListener listener : mListeners) {
                listener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            for(AbsListView.OnScrollListener listener : mListeners) {
                listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
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

    private class MyAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                isLoading = true;
                HttpGet httpPost = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);

                int status = response.getStatusLine().getStatusCode();

                if(status == 200){
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsonObject = new JSONObject(data);

                    JSONArray jsonArray = (JSONArray) jsonObject.get("posts");
                    JSONObject jsonPost = new JSONObject();

                    for (int i = curPostOnPage; i < curPostOnPage + 10; i++) {
                        jsonPost = (JSONObject) jsonArray.get(i);

                        JSONObject thumbnail_images = jsonPost.getJSONObject("thumbnail_images");

                        JSONObject image = thumbnail_images.getJSONObject("medium");

                        BlogPost blogPost = new BlogPost();

                        blogPost.setTitle(jsonPost.getString("title"));
                        blogPost.setId(jsonPost.getString("id"));
                        blogPost.setImageURL(image.getString("url"));
                        blogPost.setSlug(jsonPost.getString("slug"));
                        blogPost.setUrl(jsonPost.getString("url") + "?json=1");

                        blogActivityObjects.add(blogPost);
                    }
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
            adapter.notifyDataSetChanged();
            numPosts += 10;
            isLoading = false;
        }
    }

    private class BlogPostAdapter extends ArrayAdapter<BlogPost> {
        private ArrayList<BlogPost> allBlogPosts;
        private LayoutInflater mInflater;
        private int Resource;
        private Context context;


        public BlogPostAdapter(Context context, int resource, ArrayList<BlogPost> objects) {
            super(context, resource, objects);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Resource = resource;
            allBlogPosts = objects;
            this.context = context;
        }


        public int getCount() {
            return allBlogPosts.size();
        }

        public BlogPost getItem(int position) {
            return allBlogPosts.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder vH;
            if(convertView == null) {
                convertView = mInflater.inflate(Resource, null);
                vH = new ViewHolder();
                vH.title = (TextView) convertView.findViewById(R.id.blog_title);
                vH.image = (ImageView) convertView.findViewById(R.id.blog_image);
                vH.bar = (ProgressBar) convertView.findViewById(R.id.blog_progress_bar);
                vH.type = (TextView) convertView.findViewById(R.id.blog_type);
                vH.like = (ImageView) convertView.findViewById(R.id.freestyle_workout_video_like_button);
                convertView.setTag(vH);
            } else {
                vH = (ViewHolder) convertView.getTag();
            }

            if(allBlogPosts.get(position).isLiked()) {
                Picasso.with(getApplicationContext()).load(R.drawable.like_button).into(vH.like);
            } else
                Picasso.with(getApplicationContext()).load(R.drawable.like_before_pressed_button).into(vH.like);

            vH.title.setText(allBlogPosts.get(position).getTitle());
            vH.type.setText(allBlogPosts.get(position).getType());
            display(vH.image, allBlogPosts.get(position).getImageURL(), vH.bar);
            vH.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(allBlogPosts.get(position).isLiked()) {
                        Picasso.with(getApplicationContext()).load(R.drawable.like_before_pressed_button).into(vH.like);
                        allBlogPosts.get(position).setLiked(false);
                    } else if(!allBlogPosts.get(position).isLiked()) {
                        Picasso.with(getApplicationContext()).load(R.drawable.like_button).into(vH.like);
                        allBlogPosts.get(position).setLiked(true);
                    }
                }
            });
            return convertView;
        }

        public void display(ImageView img, String url, final ProgressBar spinner) {

            imageLoader.displayImage(url, img, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    spinner.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        class ViewHolder {
            TextView title;
            TextView type;
            ImageView image;
            ProgressBar bar;
            ImageView like;
            boolean liked = false;
        }
    }
}

