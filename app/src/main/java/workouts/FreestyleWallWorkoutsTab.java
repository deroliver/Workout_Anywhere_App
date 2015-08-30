package workouts;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;

import blog.BlogPostActivity;
import databasetools.CompletedDatabaseTools;
import databasetools.FavoritesDatabaseTools;
import stream.StreamObject;

public class FreestyleWallWorkoutsTab extends ListFragment {

    private ArrayList<FreestyleWallObject> freestyleWallObjects;
    private MyListAdapter myListAdapter;
    ListView listView;

    private Spinner spinner;

    private HashMap<String, Integer> videoCategories;

    private boolean isLoading = false;
    private boolean pauseOnScroll = false;
    private boolean pauseOnFling = true;

    private int numPosts = 0;
    private int numNewPosts = 5;
    private int pageNumber = 1;
    private int DIALOG_ID = 0;
    private int year_x, month_x, day_x;

    private String workoutCategory = "All";
    private String urlOne = "http://workoutanywhere.net/?json=get_category_posts&id=";
    private String urlTwo = "&count=5&page=";
    private String curUrl = urlOne + "886" + urlTwo + pageNumber;

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
    private ProgressDialog progress;
    private ProgressDialogListener listener;
    private boolean first = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (ProgressDialogListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.freestlye_wall, container, false);

        progress = listener.getProgressBar();

        freestyleWallObjects = new ArrayList<FreestyleWallObject>();

        dbTools = new FavoritesDatabaseTools(getActivity());
        fDBtools = new CompletedDatabaseTools(getActivity());

        listView = (ListView) view.findViewById(android.R.id.list);

        spinner = (Spinner) view.findViewById(R.id.freestyle_wall_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.freestyle_wall_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        myListAdapter = new MyListAdapter(getActivity(), R.layout.freestyle_wall_object, freestyleWallObjects);

        new MyAsyncTask().execute(curUrl);

        return view;
    }


    private class Second extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
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

            favoriteList = dbTools.getAllFavorites();
            numFavs = favoriteList.size();
            System.out.println("Num Favs:" + numFavs);

            completedList = fDBtools.getAllCompleted();
            numCompleted = completedList.size();
            System.out.println("Num Completed:" + numCompleted);


            videoCategories = new HashMap<String, Integer>();
            videoCategories.put("All", 886);
            videoCategories.put("Lower Body", 885);
            videoCategories.put("Full Body", 891);
            videoCategories.put("Beginner Home", 892);
            videoCategories.put("Bodyweight", 889);
            videoCategories.put("8 Minute Meltdown", 573);
            videoCategories.put("Cardio", 561);
            videoCategories.put("4 Minute Core", 233);

            scrolls = new MultiScrollListener();

            EndlessScrollListener endlessListener = new EndlessScrollListener();

            PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);

            scrolls.addScrollListener(listener);
            scrolls.addScrollListener(endlessListener);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            d = new DatePickerDialog(getActivity(), R.style.AppTheme, dPickerListener, year_x, month_x, day_x);

            listView.setAdapter(myListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), BlogPostActivity.class);
                    intent.putExtra("URL", freestyleWallObjects.get(position).getUrl());
                    intent.putExtra("Position", position);
                    intent.putExtra("Liked", freestyleWallObjects.get(position).isLiked());
                    intent.putExtra("Completed", freestyleWallObjects.get(position).isCompleted());
                    getParentFragment().startActivityForResult(intent, 1);
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    workoutCategory = (String) spinner.getItemAtPosition(position);
                    numPosts = 0;
                    numNewPosts = 5;
                    pageNumber = 1;
                    curUrl = urlOne + videoCategories.get(workoutCategory) + urlTwo + pageNumber;
                    freestyleWallObjects.clear();

                    new MyAsyncTask().execute(curUrl);

                    listView.setAdapter(myListAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), BlogPostActivity.class);
                            intent.putExtra("URL", freestyleWallObjects.get(position).getUrl());
                            intent.putExtra("Position", position);
                            intent.putExtra("Liked", freestyleWallObjects.get(position).isLiked());
                            intent.putExtra("Completed", freestyleWallObjects.get(position).isCompleted());
                            getParentFragment().startActivityForResult(intent, 1);
                        }
                    });

                    myListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            listView.setOnScrollListener(scrolls);

            if (favoriteList != null)
                new checkFavorites().execute();

            if (completedList != null)
                new checkCompleted().execute();
        }
    }



    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            Toast.makeText(getActivity(), day_x + "/" + month_x + "/" + year, Toast.LENGTH_LONG).show();
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

        if(completed && !freestyleWallObjects.get(pos).isCompleted()) {
            freestyleWallObjects.get(pos).setCompleted(true);
            myListAdapter.notifyDataSetChanged();
            fDBtools.insertCompletion(freestyleWallObjects.get(pos).getUrl(), date);
            numCompleted++;
            freestyleWallObjects.get(pos).setSqliteID(numCompleted);
        } else if(!completed && freestyleWallObjects.get(pos).isCompleted()) {
            freestyleWallObjects.get(pos).setCompleted(false);
            myListAdapter.notifyDataSetChanged();
            fDBtools.deleteCompletion((freestyleWallObjects.get(pos).getSqliteID()).toString());
            numCompleted--;
        }

        if(liked && !freestyleWallObjects.get(pos).isLiked()) {
            freestyleWallObjects.get(pos).setLiked(true);
            myListAdapter.notifyDataSetChanged();
            dbTools.insertFavorite(freestyleWallObjects.get(pos).getUrl(), "Blog");
            numFavs++;
            freestyleWallObjects.get(pos).setSqliteID(numFavs);
        } else if(!liked && freestyleWallObjects.get(pos).isLiked()) {
            freestyleWallObjects.get(pos).setLiked(false);
            myListAdapter.notifyDataSetChanged();
            dbTools.deleteFavorite((freestyleWallObjects.get(pos).getSqliteID()).toString());
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


    private class checkFavorites extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            isLoading = true;
            for (HashMap<String, String> fav : favoriteList) {
                if(numPosts + numNewPosts - 1 < freestyleWallObjects.size()) {
                    for (int i = numPosts; i < numPosts + numNewPosts; i++) {
                        if (fav.get("url").equals(freestyleWallObjects.get(i).getUrl())) {
                            System.out.println(fav.get("url"));
                            freestyleWallObjects.get(i).setLiked(true);
                            freestyleWallObjects.get(i).setSqliteID(Integer.parseInt(fav.get("ID")));
                            break;
                        }
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            myListAdapter.notifyDataSetChanged();
            numPosts += numNewPosts;
            isLoading = false;
        }
    }

    private class checkCompleted extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            isLoading = true;
            for (HashMap<String, String> fav : completedList) {
                if(numPosts + numNewPosts - 1 < freestyleWallObjects.size()) {
                    for (int i = numPosts; i < numPosts + numNewPosts; i++) {
                        if (fav.get("url").equals(freestyleWallObjects.get(i).getUrl())) {
                            System.out.println(fav.get("url"));
                            freestyleWallObjects.get(i).setCompleted(true);
                            freestyleWallObjects.get(i).setSqliteID(Integer.parseInt(fav.get("ID")));
                            break;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            myListAdapter.notifyDataSetChanged();
            isLoading = false;
            progress.dismiss();
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


    private class MyListAdapter extends ArrayAdapter<FreestyleWallObject> {
        private ArrayList<FreestyleWallObject> allFreestyleWorkoutObjects;
        private LayoutInflater mInflater;
        private int Resource;
        private Context context;


        public MyListAdapter(Context context, int resource, ArrayList<FreestyleWallObject> objects) {
            super(context, resource, objects);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Resource = resource;
            allFreestyleWorkoutObjects = objects;
            this.context = context;
        }

        @Override
        public int getCount() {
            return allFreestyleWorkoutObjects.size();
        }

        @Override
        public FreestyleWallObject getItem(int position) {
            return allFreestyleWorkoutObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public int getPosition(StreamObject item) {
            return allFreestyleWorkoutObjects.indexOf(item);
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

            if(freestyleWallObjects.get(position).isLiked()) {
                Picasso.with(getActivity()).load(R.drawable.like_button).into(vH.likeButton);
            } else
                Picasso.with(getActivity()).load(R.drawable.like_before_pressed_button).into(vH.likeButton);

            if(freestyleWallObjects.get(position).isCompleted()) {
                Picasso.with(getActivity()).load(R.drawable.completed_check_mark_button_full).into(vH.completed);
            } else
                Picasso.with(getActivity()).load(R.drawable.completed_check_mark_button_empty).into(vH.completed);

            vH.completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (freestyleWallObjects.get(position).isCompleted()) {
                        Picasso.with(getActivity()).load(R.drawable.completed_check_mark_button_empty).into(vH.completed);
                        numCompleted--;
                        fDBtools.deleteCompletion((freestyleWallObjects.get(position).getSqliteID().toString()));
                        freestyleWallObjects.get(position).setCompleted(false);
                    }

                    else if (!freestyleWallObjects.get(position).isCompleted()) {
                        d.show();
                        Picasso.with(getActivity()).load(R.drawable.completed_check_mark_button_full).into(vH.completed);
                        fDBtools.insertCompletion(freestyleWallObjects.get(position).getUrl(), "Blog");
                        numCompleted++;
                        freestyleWallObjects.get(position).setSqliteID(numCompleted);
                        freestyleWallObjects.get(position).setCompleted(true);
                    }
                }
            });


            vH.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (freestyleWallObjects.get(position).isLiked()) {
                        Picasso.with(getActivity()).load(R.drawable.like_before_pressed_button).into(vH.likeButton);
                        numFavs--;
                        dbTools.deleteFavorite((freestyleWallObjects.get(position).getSqliteID().toString()));
                        freestyleWallObjects.get(position).setLiked(false);
                    } else if (!freestyleWallObjects.get(position).isLiked()) {
                        Picasso.with(getActivity()).load(R.drawable.like_button).into(vH.likeButton);
                        dbTools.insertFavorite(freestyleWallObjects.get(position).getUrl(), "Wall");
                        numFavs++;
                        freestyleWallObjects.get(position).setSqliteID(numFavs);
                        freestyleWallObjects.get(position).setLiked(true);
                    }
                }
            });

            vH.workoutName.setText(allFreestyleWorkoutObjects.get(position).getWorkoutName());
            display(vH.workoutPicture, allFreestyleWorkoutObjects.get(position).getImageURL(), vH.bar);
            vH.workoutType.setText(allFreestyleWorkoutObjects.get(position).getWorkoutType());
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

                    for (int i = 0; i < jsonArray.length(); i++) {
                        numNewPosts = jsonArray.length();

                        jsonPost = (JSONObject) jsonArray.get(i);

                        JSONObject thumbnail_images = jsonPost.getJSONObject("thumbnail_images");

                        JSONObject image = thumbnail_images.getJSONObject("medium");

                        FreestyleWallObject wallPost = new FreestyleWallObject();

                        wallPost.setWorkoutName(jsonPost.getString("title"));
                        wallPost.setImageURL(image.getString("url"));
                        wallPost.setUrl(jsonPost.getString("url") + "?json=1");

                        freestyleWallObjects.add(wallPost);
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
            if(first) {
                first = false;
                new Second().execute();
            }
            myListAdapter.notifyDataSetChanged();
            isLoading = false;
            new checkFavorites().execute();
            new checkCompleted().execute();
        }
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int startingPageIndex = 0;
        private int previousTotal = 0;
        private boolean loading = true;
        private int lastPageNumber = 1;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(totalItemCount < previousTotal) {
                previousTotal = totalItemCount;
                currentPage = startingPageIndex;
                if(totalItemCount == 0) {this.loading = true;}
            }

            if(loading && (totalItemCount > previousTotal)) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }


            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                currentPage++;
                curUrl = urlOne + videoCategories.get(workoutCategory) + urlTwo + currentPage;
                new MyAsyncTask().execute(curUrl);
                loading = true;
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

