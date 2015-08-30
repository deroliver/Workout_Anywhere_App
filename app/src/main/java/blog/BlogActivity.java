package blog;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.DatePicker;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import databasetools.CompletedDatabaseTools;
import databasetools.FavoritesDatabaseTools;


public class BlogActivity extends Activity {

    private ArrayList<BlogPost> blogActivityObjects;
    private ArrayList<HashMap<String, String>> completedList;
    private ArrayList<HashMap<String, String>> favoriteList;

    private int numPosts = 0;
    private int numNewPosts = 5;
    private int pageNumber = 1;
    private int DIALOG_ID = 0;
    private int numFavs = 0;
    private int numCompleted = 0;
    private int year_x, month_x, day_x;

    private String workoutAnywhereURL1 = "http://workoutanywhere.net/blog/?json=get_posts&page=";
    private String workoutAnywhereURL2 = "&count=5";
    private String currentURL = workoutAnywhereURL1 + pageNumber + workoutAnywhereURL2;

    private boolean isLoading = false;
    private boolean pauseOnScroll = false;
    private boolean pauseOnFling = false;

    private BlogPostAdapter adapter;

    private ListView listView;

    private ImageLoader imageLoader;

    private DisplayImageOptions options;

    private FavoritesDatabaseTools dbTools;

    private CompletedDatabaseTools fDBTools;

    private MultiScrollListener scrolls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_activity);
        imageLoader = ImageLoader.getInstance();

        dbTools = new FavoritesDatabaseTools(this);
        fDBTools = new CompletedDatabaseTools(this);

        listView = (ListView) findViewById(R.id.list);
        blogActivityObjects = new ArrayList<BlogPost>();
        adapter = new BlogPostAdapter(getApplicationContext(), R.layout.blog_object, blogActivityObjects);
        listView.setAdapter(adapter);

        new MyAsyncTask().execute(currentURL);

        if (favoriteList != null)
            new checkFavorites().execute();

        if (completedList != null)
            new checkCompleted().execute();

        new Async().execute();
    }

    private class Async extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            options = getDisplayOptions();

            final Calendar cal = Calendar.getInstance();
            year_x = cal.get(Calendar.YEAR);
            month_x = cal.get(Calendar.MONTH);
            day_x = cal.get(Calendar.DAY_OF_MONTH);

            favoriteList = dbTools.getAllFavorites();
            numFavs = favoriteList.size();
            System.out.println("Num Favs:" + numFavs);

            completedList = fDBTools.getAllCompleted();
            numCompleted = completedList.size();
            System.out.println("Num Completed:" + numCompleted);

            scrolls = new MultiScrollListener();

            EndlessScrollListener endlessListener = new EndlessScrollListener();

            PauseOnScrollListener listener2 = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);

            scrolls.addScrollListener(endlessListener);
            scrolls.addScrollListener(listener2);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listView.setOnScrollListener(scrolls);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), BlogPostActivity.class);
                    intent.putExtra("URL", blogActivityObjects.get(position).getUrl());
                    intent.putExtra("Position", position);
                    intent.putExtra("Liked", blogActivityObjects.get(position).isLiked());
                    intent.putExtra("Completed", blogActivityObjects.get(position).isCompleted());
                    startActivityForResult(intent, 1);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent intent = data;
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String position = intent.getStringExtra("Position");
                Boolean completed = intent.getBooleanExtra("Completed", false);
                Boolean liked = intent.getBooleanExtra("Liked", false);
                String date = intent.getStringExtra("Date");

                int pos = Integer.parseInt(position);

                System.out.println("Position: " + pos + " Completed: " + completed + " Liked: " + liked + " Date: " + date);

                if (completed && !blogActivityObjects.get(pos).isCompleted()) {
                    blogActivityObjects.get(pos).setCompleted(true);
                    fDBTools.insertCompletion(blogActivityObjects.get(pos).getUrl(), date);
                    numCompleted++;
                } else if (!completed && blogActivityObjects.get(pos).isCompleted()) {
                    blogActivityObjects.get(pos).setCompleted(false);
                    fDBTools.deleteCompletionByURL(blogActivityObjects.get(pos).getUrl());
                    numCompleted--;
                }
                adapter.notifyDataSetChanged();

                if (liked && !blogActivityObjects.get(pos).isLiked()) {
                    blogActivityObjects.get(pos).setLiked(true);
                    dbTools.insertFavorite(blogActivityObjects.get(pos).getUrl(), "Blog");
                    numFavs++;
                } else if (!liked && blogActivityObjects.get(pos).isLiked()) {
                    blogActivityObjects.get(pos).setLiked(false);
                    dbTools.deleteFavoriteByURL(blogActivityObjects.get(pos).getUrl());
                    numFavs--;
                }
                adapter.notifyDataSetChanged();
            }
        }
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
            for (AbsListView.OnScrollListener listener : mListeners) {
                listener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            for (AbsListView.OnScrollListener listener : mListeners) {
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

        return options;
    }


    private class checkFavorites extends AsyncTask<Void, Void, Boolean[]> {
        private int position[] = new int[5];
        private Boolean Found = false;

        @Override
        protected Boolean[] doInBackground(Void... params) {
            isLoading = true;
            Boolean found[] = new Boolean[] {false, false, false, false, false};
            for (HashMap<String, String> fav : favoriteList) {
                secondloop: for (int i = numPosts; i < numPosts + numNewPosts && i < blogActivityObjects.size(); i++) {
                    if (fav.get("url").equals(blogActivityObjects.get(i).getUrl())) {
                        Found = true;
                        position[i - numPosts] = i;
                        found[i - numPosts] = true;
                        break secondloop;
                    }
                }
            }
            return found;
        }


        @Override
        protected void onPostExecute(Boolean[] result) {
            if(Found) {
                for (int i = 0; i < result.length; i++) {
                    if (result[i]) {
                        blogActivityObjects.get(position[i]).setLiked(true);
                    }
                }
            }
            isLoading = false;
            adapter.notifyDataSetChanged();
            numPosts += 5;
        }
    }


    private class checkCompleted extends AsyncTask<Void, Void, Boolean[]> {
        private int position[] = new int[5];
        private Boolean Found = false;
        @Override
        protected Boolean[] doInBackground(Void... params) {
            Boolean found[] = new Boolean[] {false, false, false, false, false};
            isLoading = true;
            for (HashMap<String, String> fav : completedList) {
                secondloop: for (int i = numPosts; i < numPosts + numNewPosts && i < blogActivityObjects.size(); i++) {
                    if (fav.get("url").equals(blogActivityObjects.get(i).getUrl())) {
                        Found = true;
                        position[i - numPosts] = i;
                        found[i - numPosts] = true;
                        break secondloop;
                    }
                }

            }
            return found;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            if(Found) {
                for (int i = 0; i < result.length; i++) {
                    if (result[i]) {
                        blogActivityObjects.get(position[i]).setCompleted(true);
                    }
                }
            }

            isLoading = false;
            adapter.notifyDataSetChanged();
        }
    }


    private class MyAsyncTask extends AsyncTask<String, Void, BlogPost[]> {

        @Override
        protected BlogPost[] doInBackground(String... urls) {
            try {
                BlogPost posts[] = new BlogPost[5];
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

                    for (int i = 0; i < jsonArray.length(); i++) {
                        numNewPosts = jsonArray.length();
                        jsonPost = (JSONObject) jsonArray.get(i);

                        JSONObject thumbnail_images = jsonPost.getJSONObject("thumbnail_images");

                        JSONObject image = thumbnail_images.getJSONObject("medium");

                        BlogPost blogPost = new BlogPost();

                        blogPost.setTitle(jsonPost.getString("title"));
                        blogPost.setId(jsonPost.getString("id"));
                        blogPost.setImageURL(image.getString("url"));
                        blogPost.setSlug(jsonPost.getString("slug"));
                        blogPost.setUrl(jsonPost.getString("url") + "?json=1");

                        posts[i] = blogPost;
                    }
                    isLoading = false;
                    return posts;
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

        protected void onPostExecute(BlogPost[] results) {
            for(BlogPost bP : results) {
                blogActivityObjects.add(bP);
            }
            adapter.notifyDataSetChanged();
            pageNumber++;
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
                vH.completed = (ImageView) convertView.findViewById(R.id.freestyle_workout_video_completed_button);
                convertView.setTag(vH);
            } else {
                vH = (ViewHolder) convertView.getTag();
            }

            if(allBlogPosts.get(position).isLiked()) {
                Picasso.with(getApplicationContext()).load(R.drawable.like_button).into(vH.like);
            } else
                Picasso.with(getApplicationContext()).load(R.drawable.like_before_pressed_button).into(vH.like);


            if(allBlogPosts.get(position).isCompleted()) {
                Picasso.with(getApplicationContext()).load(R.drawable.completed_check_mark_button_full).into(vH.completed);
            } else
                Picasso.with(getApplicationContext()).load(R.drawable.completed_check_mark_button_empty).into(vH.completed);

            vH.title.setText(allBlogPosts.get(position).getTitle());
            vH.type.setText(allBlogPosts.get(position).getType());
            display(vH.image, allBlogPosts.get(position).getImageURL(), vH.bar);

            vH.completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allBlogPosts.get(position).isCompleted()) {
                        Picasso.with(getApplicationContext()).load(R.drawable.completed_check_mark_button_empty).into(vH.completed);
                        numCompleted--;
                        fDBTools.deleteCompletionByURL(allBlogPosts.get(position).getUrl());
                        allBlogPosts.get(position).setCompleted(false);
                    }

                    else if (!allBlogPosts.get(position).isCompleted()) {
                        showDialog(DIALOG_ID);
                        Picasso.with(getApplicationContext()).load(R.drawable.completed_check_mark_button_full).into(vH.completed);
                        fDBTools.insertCompletion(allBlogPosts.get(position).getUrl(), day_x + ":" + month_x + ":" + year_x);
                        numCompleted++;
                        allBlogPosts.get(position).setCompleted(true);
                    }
                }
            });

            vH.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(allBlogPosts.get(position).isLiked()) {
                        Picasso.with(getApplicationContext()).load(R.drawable.like_before_pressed_button).into(vH.like);
                        numFavs--;
                        dbTools.deleteFavoriteByURL(allBlogPosts.get(position).getUrl());
                        allBlogPosts.get(position).setLiked(false);
                    } else if(!allBlogPosts.get(position).isLiked()) {
                        Picasso.with(getApplicationContext()).load(R.drawable.like_button).into(vH.like);
                        dbTools.insertFavorite(allBlogPosts.get(position).getUrl(), "Blog");
                        numFavs++;
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
            ImageView completed;
            boolean liked = false;
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
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            Toast.makeText(getApplicationContext(), day_x + "/" + month_x + "/" + year, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                currentURL = workoutAnywhereURL1 + pageNumber + workoutAnywhereURL2;
                new MyAsyncTask().execute(currentURL);
                new checkCompleted().execute();
                new checkFavorites().execute();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}

