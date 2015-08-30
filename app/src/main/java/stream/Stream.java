package stream;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v7.app.ActionBar;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.practice.derikpc.workoutanywhere.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.SimpleFormatter;

import workouts.FreestyleWallObject;


public class Stream extends ListActivity {

    private ArrayList<StreamObject> streamObjects;
    private MyListAdapter myListAdapter;

    private Boolean isLoading = false;

    private int numNewPosts = 0;
    private int numPosts = 0;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private ListView listView;

    private String firstUrl = "http://workoutanywhere.net/api/buddypressread/activity_get_activities/?pages=2&comments=threaded&page=";
    private String page = "1";
    private String curUrl = firstUrl+ page;

    View context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_stream);

        streamObjects = new ArrayList<StreamObject>();

        imageLoader = ImageLoader.getInstance();
        options = getDisplayOptions();
        listView = (ListView) findViewById(android.R.id.list);

        myListAdapter = new MyListAdapter(getApplicationContext(), streamObjects);

        listView.setAdapter(myListAdapter);

        new MyAsyncTask().execute(firstUrl);

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



    private class MyListAdapter extends ArrayAdapter<StreamObject> {

        private Context context;
        private ArrayList<StreamObject> allStreamObjects;

        private LayoutInflater mInflater;
        private boolean mNotifyOnChange = true;


        public MyListAdapter(Context context, ArrayList<StreamObject> mStreamObjects) {
            super(context, R.layout.stream_object);
            this.context = context;
            this.allStreamObjects = mStreamObjects;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return allStreamObjects.size();
        }

        @Override
        public StreamObject getItem(int position) {
            return allStreamObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getPosition(StreamObject item) {
            return allStreamObjects.indexOf(item);
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
            final ViewHolder holder;
            int type = getItemViewType(position);

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.stream_object, parent, false);
                holder = new ViewHolder();
                holder.personName = (TextView) convertView.findViewById(R.id.person_name);
                holder.personPicture = ((ImageView) convertView.findViewById(R.id.stream_object_picture));
                holder.action = (TextView) convertView.findViewById(R.id.stream_object_action);
                holder.content = (TextView) convertView.findViewById(R.id.stream_object_content);
                holder.nLikes = (TextView) convertView.findViewById(R.id.stream_object_nlikes);
                holder.nComments = (TextView) convertView.findViewById(R.id.stream_object_ncomments);
                holder.likeButton = (TextView) convertView.findViewById(R.id.like_post_button);
                holder.seeCommentsButton = (TextView) convertView.findViewById(R.id.see_comments_button);
                holder.bar = (ProgressBar) convertView.findViewById(R.id.activity_object_progress_bar);
                holder.time = (TextView) convertView.findViewById(R.id.stream_object_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.personName.setText(allStreamObjects.get(position).getName());
            if(allStreamObjects.get(position).getActivityContent() != "") {
                holder.content.setVisibility(View.VISIBLE);
            }
            holder.time.setText(calculateTimeDifference(allStreamObjects.get(position).getDateTime()));
            holder.content.setText(allStreamObjects.get(position).getActivityContent());
            holder.action.setText(allStreamObjects.get(position).getActivityAction());
            display(holder.personPicture, allStreamObjects.get(position).getImageURL(), holder.bar);
            holder.nLikes.setText((allStreamObjects.get(position).getnLikes()).toString());
            holder.nComments.setText((allStreamObjects.get(position).getnComments()).toString());
            holder.pos = position;
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
        TextView personName;
        ImageView personPicture;
        TextView action;
        TextView content;
        TextView nLikes;
        TextView nComments;
        TextView likeButton;
        TextView seeCommentsButton;
        ProgressBar bar;
        TextView time;
        int pos;
    }

    private String calculateTimeDifference(String dateTime) {
        DateTime postDate = new DateTime();
        DateTime now = DateTime.now();


        try {
            postDate = new DateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
        } catch (java.text.ParseException e) {
            postDate = null;
            e.printStackTrace();
        }


        long secondsInMill = 0;
        long minutesInMill = 0;
        long hoursInMill = 0;
        long daysInMill = 0;

        long elapsedDays = 0;
        long elapsedHours = 0;
        long elapsedMinutes = 0;
        long elapsedSeconds = 0;

        int diffYear = 0;
        int diffMonth = 0;


        if(postDate != null) {

            long millis = now.getMillis() - postDate.getMillis();
            long difference = Math.abs(millis);

            elapsedDays = (int) (difference/(1000 * 60 * 60 * 24));
            elapsedHours = (int) ((difference -  (1000 * 60 * 60 * 24*elapsedDays))/ (1000 * 60 * 60));
            elapsedMinutes = (int) ((difference -  (1000 * 60 * 60 * 24*elapsedDays))/ (1000 * 60));
        }

        if(diffYear > 0) {
            return (diffYear + " years " + diffMonth + " ago");
        }

        if(diffMonth > 0) {
            return (diffMonth + " months " + elapsedDays + " ago");
        }

        if(elapsedDays > 0) {
            return (elapsedDays + " days " + elapsedHours + " hours ago");
        }

        if(elapsedHours > 0) {
            return (elapsedHours + " hours ago");
        }

        if(elapsedMinutes > 0) {
            return (elapsedMinutes + " minutes ago");
        }

        return "recently";
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

                    JSONObject array = jsonObject.getJSONObject("activities");
                    Iterator x = array.keys();

                    JSONArray jsonArray = new JSONArray();

                    while(x.hasNext()) {
                        String key = (String)x.next();
                        jsonArray.put(array.get(key));
                    }

                    JSONObject jsonPost = new JSONObject();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        numNewPosts = 10;

                        jsonPost = (JSONObject) jsonArray.get(i);

                        JSONObject user = jsonPost.getJSONObject("user");
                        JSONObject userID = new JSONObject();

                        String action = "";
                        String content = "";


                        if(user != null) {
                            Iterator<String> keys = user.keys();
                            while(keys.hasNext()) {
                                String key = keys.next();
                                userID = user.getJSONObject(key);
                            }
                        }

                        action = jsonPost.getString("action");
                        content = jsonPost.getString("content");

                        Document doc;
                        String actionText = "";
                        String contentText = "";

                        if(action != "") {
                            doc = Jsoup.parse(action);
                            actionText = doc.text();
                        }

                        if(content != "") {
                            doc = Jsoup.parse(content);
                            contentText = doc.text();
                        }

                        String userName = "";
                        String avatarURL = "";
                        String activityUserID = "";

                        if(userID != null) {
                            userName = userID.getString("display_name");
                            avatarURL = userID.getString("avatar_thumb");
                            activityUserID = userID.getString("id");
                        } else {
                            userName = "Error";
                        }


                        String postTime = jsonPost.getString("time");

                        char first = avatarURL.charAt(0);
                        if(first == '/') {
                            avatarURL = "http://" + avatarURL.substring(2);
                        }

                        JSONObject children = jsonPost.optJSONObject("children");

                        int numComments = 0;

                        if(children != null) {

                            Iterator y = array.keys();

                            JSONArray childrenArray = new JSONArray();

                            while(x.hasNext()) {
                                String key = (String)y.next();
                                childrenArray.put(children.get(key));
                            }

                            System.out.println("Children: " + childrenArray.length());
                            numComments = childrenArray.length();
                        }


                        StreamObject activityUpdate = new StreamObject();

                        activityUpdate.setDateTime(postTime);
                        activityUpdate.setName(userName);
                        activityUpdate.setImageURL(avatarURL);
                        activityUpdate.setActivityUserID(activityUserID);
                        activityUpdate.setActivityAction(actionText);
                        activityUpdate.setActivityContent(contentText);
                        activityUpdate.setnComments(numComments);

                        System.out.println(userName + "   " + avatarURL);

                        streamObjects.add(activityUpdate);
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
            myListAdapter.notifyDataSetChanged();
            isLoading = false;

            System.out.println(streamObjects.size());
            //pageNumber++;
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
                curUrl = firstUrl + currentPage;
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
        //imageLoader.clearMemoryCache();
        //imageLoader.clearDiskCache();
    }
}








































