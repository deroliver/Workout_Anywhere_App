package workouts;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    private int curPostOnPage = 0;
    private int numPosts = 0;

    private String curUrl = "";
    private String workoutCategory = "All";
    private String urlOne = "http://workoutanywhere.net/?json=get_category_posts&id=";
    private String urlTwo = "&count=10&page=";

    private ImageLoader imageLoader;
    private DisplayImageOptions options;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.freestlye_wall, container, false);

        imageLoader = ImageLoader.getInstance();
        options = getDisplayOptions();

        freestyleWallObjects = new ArrayList<FreestyleWallObject>();

        videoCategories = new HashMap<String, Integer>();
        videoCategories.put("All", 886);
        videoCategories.put("Lower Body", 885);
        videoCategories.put("Full Body", 891);
        videoCategories.put("Beginner Home", 892);
        videoCategories.put("Bodyweight", 889);

        listView = (ListView) view.findViewById(android.R.id.list);

        myListAdapter = new MyListAdapter(getActivity(), R.layout.freestyle_wall_object, freestyleWallObjects);
        listView.setAdapter(myListAdapter);

        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);
        listView.setOnScrollListener(listener);


        spinner = (Spinner)view.findViewById(R.id.freestyle_wall_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.freestyle_wall_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workoutCategory = (String) spinner.getItemAtPosition(position);
                freestyleWallObjects.clear();
                numPosts = 0;
                int pageNumber = (numPosts / 5) + 1;
                curUrl = urlOne + videoCategories.get(workoutCategory) + urlTwo + pageNumber;
                System.out.println(curUrl);
                new MyAsyncTask().execute(curUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vH =new ViewHolder();
            if(convertView == null) {
                convertView = mInflater.inflate(Resource, null);
                vH.bar = (ProgressBar) convertView.findViewById(R.id.freestyle_object_progress_bar);
                vH.workoutName = (TextView) convertView.findViewById(R.id.freestyle_workout_video_name);
                vH.workoutPicture = ((ImageView) convertView.findViewById(R.id.freestyle_workout_video_image));
                vH.likeButton = (ImageButton) convertView.findViewById(R.id.freestyle_workout_video_like_button);
                vH.workoutType = (TextView) convertView.findViewById(R.id.freestyle_workout_video_type);
                convertView.setTag(vH);

            } else {
                vH = (ViewHolder) convertView.getTag();
            }

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
        ImageButton likeButton;
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

                    for (int i = 0; i < 5; i++) {
                        jsonPost = (JSONObject) jsonArray.get(i);

                        JSONObject thumbnail_images = jsonPost.getJSONObject("thumbnail_images");

                        JSONObject image = thumbnail_images.getJSONObject("medium");

                        FreestyleWallObject wallPost = new FreestyleWallObject();

                        wallPost.setWorkoutName(jsonPost.getString("title"));
                        wallPost.setImageURL(image.getString("url"));
                        wallPost.setUrl(jsonPost.getString("url") + "?json=1");

                        freestyleWallObjects.add(wallPost);
                    }
                    numPosts += 5;
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
            myListAdapter.notifyDataSetChanged();
            isLoading = false;
        }
    }
}

