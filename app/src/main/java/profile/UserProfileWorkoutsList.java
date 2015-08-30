package profile;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.practice.derikpc.workoutanywhere.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blog.BlogPost;
import blog.BlogPostActivity;
import databasetools.CompletedDatabaseTools;
import databasetools.FavoritesDatabaseTools;
import stream.StreamObject;

public class UserProfileWorkoutsList extends ListActivity {

    private ArrayList<BlogPost> blogPostArrayList;
    private MyListAdapter myListAdapter;
    ListView listView;

    private boolean isLoading = false;
    private boolean pauseOnScroll = false;
    private boolean pauseOnFling = true;

    private int numPosts = 0;
    private int numNewPosts = 0;
    private int DIALOG_ID = 0;
    private int year_x, month_x, day_x;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private FavoritesDatabaseTools dbTools;
    private ArrayList<HashMap<String, String>> favoriteList;

    private CompletedDatabaseTools fDBtools;
    private ArrayList<HashMap<String, String>> completedList;

    private int numFavs = 0;
    private int numCompleted = 0;
    private DatePickerDialog d;

    private MultiScrollListener scrolls;

    private String[] urlArray;

    private String type = "Favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        type = intent.getStringExtra("Type");

        setContentView(R.layout.profile_list);

        urlArray = new String[5];

        blogPostArrayList = new ArrayList<BlogPost>();

        dbTools = new FavoritesDatabaseTools(getApplicationContext());
        fDBtools = new CompletedDatabaseTools(getApplicationContext());

        listView = (ListView) findViewById(android.R.id.list);

        myListAdapter = new MyListAdapter(getApplicationContext(), R.layout.freestyle_wall_object, blogPostArrayList);

        new Second().execute();

        new MyAsyncTask().execute(urlArray);
    }


    private class Second extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
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

            imageLoader = ImageLoader.getInstance();
            options = getDisplayOptions();

            final java.util.Calendar cal = java.util.Calendar.getInstance();
            year_x = cal.get(java.util.Calendar.YEAR);
            month_x = cal.get(java.util.Calendar.MONTH);
            day_x = cal.get(java.util.Calendar.DAY_OF_MONTH);

            if(type.equals("Favorites")) {
                completedList = fDBtools.getAllCompleted();
                favoriteList = dbTools.getAllFavorites();
                numFavs = favoriteList.size();
                System.out.println("Num Favs:" + numFavs);

                if (favoriteList.size() < 5){
                    numNewPosts = favoriteList.size();
                } else
                    numNewPosts = 5;

                for(int i = numPosts; i < numNewPosts; i ++) {
                    if(favoriteList.get(i) != null)
                        urlArray[i] = favoriteList.get(i).get("url");
                }



            } else if(type.equals("Completed")) {
                completedList = fDBtools.getAllCompleted();
                favoriteList = fDBtools.getAllCompleted();
                numCompleted = completedList.size();
                System.out.println("Num Completed:" + numCompleted);

                if (favoriteList.size() < 5){
                    numNewPosts = favoriteList.size();
                } else
                    numNewPosts = 5;

                for(int i = numPosts; i < numNewPosts; i ++) {
                    if(completedList.get(i).get("url") != null)
                        urlArray[i] = completedList.get(i).get("url");
                }
            }


            scrolls = new MultiScrollListener();

            EndlessScrollListener endlessListener = new EndlessScrollListener();

            PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);

            scrolls.addScrollListener(listener);
            scrolls.addScrollListener(endlessListener);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            d = new DatePickerDialog(getApplicationContext(), R.style.AppTheme, dPickerListener, year_x, month_x, day_x);

            listView.setAdapter(myListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), BlogPostActivity.class);
                    intent.putExtra("URL", blogPostArrayList.get(position).getUrl());
                    intent.putExtra("Position", position);
                    intent.putExtra("Liked", blogPostArrayList.get(position).isLiked());
                    intent.putExtra("Completed", blogPostArrayList.get(position).isCompleted());
                    startActivityForResult(intent, 1);
                }
            });

            listView.setOnScrollListener(scrolls);

        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Request Code: " + requestCode + "::::::" + " Result Code: " + resultCode);

        String position = data.getStringExtra("Position");
        Boolean completed = data.getBooleanExtra("Completed", false);
        Boolean liked = data.getBooleanExtra("Liked", false);
        String date = data.getStringExtra("Date");

        int pos = Integer.parseInt(position);

        System.out.println("Position: " + pos + " Completed: " + completed + " Liked: " + liked + " Date: " + date);

        if(completed && !blogPostArrayList.get(pos).isCompleted()) {
            blogPostArrayList.get(pos).setCompleted(true);
            myListAdapter.notifyDataSetChanged();
            fDBtools.insertCompletion(blogPostArrayList.get(pos).getUrl(), date);
            numCompleted++;
            blogPostArrayList.get(pos).setSqliteID(numCompleted);
        } else if(!completed && blogPostArrayList.get(pos).isCompleted()) {
            blogPostArrayList.get(pos).setCompleted(false);
            myListAdapter.notifyDataSetChanged();
            fDBtools.deleteCompletion((blogPostArrayList.get(pos).getSqliteID()).toString());
            numCompleted--;
        }

        if(liked && !blogPostArrayList.get(pos).isLiked()) {
            blogPostArrayList.get(pos).setLiked(true);
            myListAdapter.notifyDataSetChanged();
            dbTools.insertFavorite(blogPostArrayList.get(pos).getUrl(), "Blog");
            numFavs++;
            blogPostArrayList.get(pos).setSqliteID(numFavs);
        } else if(!liked && blogPostArrayList.get(pos).isLiked()) {
            blogPostArrayList.get(pos).setLiked(false);
            myListAdapter.notifyDataSetChanged();
            dbTools.deleteFavorite((blogPostArrayList.get(pos).getSqliteID()).toString());
            numFavs--;
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

    private class checkFavorites extends AsyncTask<Void, Void, Boolean[]> {
        private int position[] = new int[5];
        private Boolean Found = false;
        @Override
        protected Boolean[] doInBackground(Void... type) {
            isLoading = true;
            Boolean found[] = new Boolean[]{false, false, false, false, false};
            System.out.println("NumPosts: " + numPosts);
            for (HashMap<String, String> comp : completedList) {
                secondloop: for (int i = numPosts; i < numPosts + numNewPosts && i < blogPostArrayList.size(); i++) {
                    System.out.println("SQL URL: " + comp.get("url") + " ArrayList URL: " + blogPostArrayList.get(i).getUrl());
                    if (comp.get("url").equals(blogPostArrayList.get(i).getUrl())) {
                        System.out.println("Found Favorite Match: " + comp.get("url") + blogPostArrayList.get(i).getUrl());
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
                        blogPostArrayList.get(position[i]).setCompleted(true);
                    }
                }
            }
            isLoading = false;
            numPosts += numNewPosts;
            myListAdapter.notifyDataSetChanged();
        }
    }


    private class checkCompleted extends AsyncTask<Void, Void, Boolean[]> {
        private int position[] = new int[5];
        private Boolean Found = false;

        @Override
        protected Boolean[] doInBackground(Void... params) {
            Boolean found[] = new Boolean[]{false, false, false, false, false};
            isLoading = true;

            for (HashMap<String, String> fav : favoriteList) {
                secondloop:
                for (int i = numPosts; i < numPosts + numNewPosts && i < blogPostArrayList.size(); i++) {
                    System.out.println("SQL URL: " + fav.get("url") + " ArrayList URL: " + blogPostArrayList.get(i).getUrl());
                    if (fav.get("url").equals(blogPostArrayList.get(i).getUrl())) {
                        System.out.println("Found Completed Match: " + fav.get("url") + blogPostArrayList.get(i).getUrl());
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
                        blogPostArrayList.get(position[i]).setLiked(true);
                    }
                }
            }
            isLoading = false;
            numPosts += numNewPosts;
            myListAdapter.notifyDataSetChanged();
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



        private class MyListAdapter extends ArrayAdapter<BlogPost> {
            private ArrayList<BlogPost> allUserProfilePosts;
            private LayoutInflater mInflater;
            private int Resource;
            private Context context;


            public MyListAdapter(Context context, int resource, ArrayList<BlogPost> objects) {
                super(context, resource, objects);
                mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Resource = resource;
                allUserProfilePosts = objects;
                this.context = context;
            }

            @Override
            public int getCount() {
                return allUserProfilePosts.size();
            }

            @Override
            public BlogPost getItem(int position) {
                return allUserProfilePosts.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }


            public int getPosition(StreamObject item) {
                return allUserProfilePosts.indexOf(item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int position) {
                return 1;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ViewHolder vH;
                if (convertView == null) {
                    convertView = mInflater.inflate(Resource, null);
                    vH = new ViewHolder();
                    vH.bar = (ProgressBar) convertView.findViewById(R.id.freestyle_object_progress_bar);
                    vH.workoutName = (TextView) convertView.findViewById(R.id.freestyle_workout_video_name);
                    vH.workoutPicture = ((ImageView) convertView.findViewById(R.id.freestyle_workout_video_image));
                    vH.likeButton = (ImageView) convertView.findViewById(R.id.freestyle_workout_video_like_button);
                    vH.completed = (ImageView) convertView.findViewById(R.id.freestyle_workout_video_completed_button);
                    vH.workoutType = (TextView) convertView.findViewById(R.id.freestyle_workout_video_type);
                    convertView.setTag(vH);

                } else {
                    vH = (ViewHolder) convertView.getTag();
                }


                if (blogPostArrayList.get(position).isLiked()) {
                    imageLoader.displayImage("drawable://" + R.drawable.like_button, vH.likeButton);
                } else
                    imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, vH.likeButton);

                if (blogPostArrayList.get(position).isCompleted()) {
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, vH.completed);
                } else
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, vH.completed);

                vH.completed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (blogPostArrayList.get(position).isCompleted()) {
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, vH.completed);
                            numCompleted--;
                            fDBtools.deleteCompletionByURL(blogPostArrayList.get(position).getUrl());
                            blogPostArrayList.get(position).setCompleted(false);
                        } else if (!blogPostArrayList.get(position).isCompleted()) {
                            showDialog(DIALOG_ID);
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, vH.completed);
                            fDBtools.insertCompletion(blogPostArrayList.get(position).getUrl(), "Blog");
                            numCompleted++;
                            blogPostArrayList.get(position).setSqliteID(numCompleted);
                            blogPostArrayList.get(position).setCompleted(true);
                        }
                    }
                });


                vH.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (blogPostArrayList.get(position).isLiked()) {
                            imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, vH.likeButton);
                            numFavs--;
                            dbTools.deleteFavoriteByURL(blogPostArrayList.get(position).getUrl());
                            blogPostArrayList.get(position).setLiked(false);
                        } else if (!blogPostArrayList.get(position).isLiked()) {
                            imageLoader.displayImage("drawable://" + R.drawable.like_button, vH.likeButton);
                            dbTools.insertFavorite(blogPostArrayList.get(position).getUrl(), "Wall");
                            numFavs++;
                            blogPostArrayList.get(position).setSqliteID(numFavs);
                            blogPostArrayList.get(position).setLiked(true);
                        }
                    }
                });

                vH.workoutName.setText(allUserProfilePosts.get(position).getTitle());
                display(vH.workoutPicture, allUserProfilePosts.get(position).getImageURL(), vH.bar);
                vH.workoutType.setText(allUserProfilePosts.get(position).getType());
                vH.pos = position;
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

        }

        static class ViewHolder {
            TextView workoutName;
            TextView workoutType;
            ImageView workoutPicture;
            ImageView likeButton;
            ImageView completed;
            ProgressBar bar;
            int pos;
        }


    private class MyAsyncTask extends AsyncTask<String, Void, BlogPost[]> {

        @Override
        protected BlogPost[] doInBackground(String... urls) {
            try {
                isLoading = true;
                System.out.println("numNewPosts: " + numNewPosts);
                BlogPost results[] = new BlogPost[numNewPosts];
                for(int i = 0; i < numNewPosts; i ++) {
                    if (urls[i] != null) {
                        HttpGet httpPost = new HttpGet(urls[i]);
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpResponse response = httpClient.execute(httpPost);

                        int status = response.getStatusLine().getStatusCode();

                        if (status == 200) {
                            HttpEntity entity = response.getEntity();
                            String data = EntityUtils.toString(entity);

                            JSONObject jsonObject = new JSONObject(data);

                            JSONObject post = jsonObject.getJSONObject("post");

                            JSONObject thumbnail_images = post.getJSONObject("thumbnail_images");

                            JSONObject image = thumbnail_images.getJSONObject("medium");

                            BlogPost wallPost = new BlogPost();

                            wallPost.setTitle(post.getString("title_plain"));
                            wallPost.setImageURL(image.getString("url"));
                            wallPost.setUrl(post.getString("url") + "?json=1");

                            if(type.equals("Favorites"))
                                wallPost.setLiked(true);
                            else if(type.equals("Completed"))
                                wallPost.setCompleted(true);

                            results[i] = wallPost;
                        }
                    }
                }
                return results;

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(BlogPost[] result) {
            if (result != null) {
                for (BlogPost bP : result)
                    blogPostArrayList.add(bP);
            }
            myListAdapter.notifyDataSetChanged();
            isLoading = false;

            if(type.equals("Favorites")){
                new checkFavorites().execute();
            } else if(type.equals("Completed")){
                new checkCompleted().execute();
            }
        }
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
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if(blogPostArrayList.size() > visibleThreshold) {
                    if (favoriteList.size() < 5){
                        numNewPosts = favoriteList.size();
                    } else
                        numNewPosts = 5;

                    for(int i = numPosts; i < numNewPosts; i ++) {
                        if(favoriteList.get(i) != null)
                            urlArray[i] = favoriteList.get(i).get("url");
                    }


                    new MyAsyncTask().execute(urlArray);

                    if(type.equals("Favorites")){
                        new checkFavorites().execute();
                    } else if(type.equals("Completed")){
                        new checkCompleted().execute();
                    }

                    loading = true;
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
    }
}


