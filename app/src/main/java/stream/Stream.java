package stream;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.SimpleFormatter;

import databasetools.StreamDatabaseHelper;
import user.User;
import workouts.FreestyleWallObject;





public class Stream extends ListFragment {

    private ArrayList<StreamObject> streamObjects;
    private MyListAdapter myListAdapter;

    private Boolean isLoading = false;

    private int numNewPosts = 0;
    private int numPosts = 0;

    private boolean pauseOnScroll = false;
    private boolean pauseOnFling = true;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private ArrayList<UserComment> comments;

    private ListView listView;

    private String submitActivityUrl1 = "http://workoutanywhere.net/api/buddypressread/activity_submit_activity/?action=";
    private String submitActivityUrl2 = "&content=";
    private String submitActivityUrl3 = "&userid=";

    private String firstUrl = "http://workoutanywhere.net/api/buddypressread/activity_get_activities/?pages=2&comments=threaded&page=";
    private String page = "1";
    private String curUrl = firstUrl+ page;

    private Boolean first = true;

    private MultiScrollListener scrolls;

    private StreamDatabaseHelper streamDB;

    private CommentListener listener;

    private ImageButton submitActivity;

    private TextView whatsNewText;

    private EditText activityContent;

    View context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (CommentListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_stream, container, false);

        streamObjects = new ArrayList<StreamObject>();
        comments = new ArrayList<UserComment>();

        listView = (ListView) view.findViewById(android.R.id.list);

        submitActivity = (ImageButton) view.findViewById(R.id.submit_activity_button);
        whatsNewText = (TextView) view.findViewById(R.id.whats_new_text_view);
        activityContent = (EditText) view.findViewById(R.id.activity_content_text);

        whatsNewText.setText("What's new " + User.firstName);

        myListAdapter = new MyListAdapter(getActivity(), streamObjects);

        new MyAsyncTask().execute(firstUrl);

        return view;
    }


    private class Second extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            streamDB = new StreamDatabaseHelper(getActivity());
            streamDB.printAllLikes();

            imageLoader = ImageLoader.getInstance();
            options = getDisplayOptions();

            scrolls = new MultiScrollListener();

            EndlessScrollListener endlessListener = new EndlessScrollListener();

            PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);

            scrolls.addScrollListener(listener);
            scrolls.addScrollListener(endlessListener);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            submitActivity.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!activityContent.getText().equals("")) {
                        String url = submitActivityUrl1 + User.getFirstName() + "%20" + User.getLastName() + "%20posted%20an%20update" +
                                submitActivityUrl2 + activityContent.getText() + submitActivityUrl3 + User.getUserID();

                        activityContent.setText("");

                        System.out.println(url);

                        new SubmitActivity().execute(url);
                    }
                }
            });

            listView.setAdapter(myListAdapter);

            listView.setOnScrollListener(scrolls);

            new CheckLikes().execute();
        }
    }

    private class SubmitActivity extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                isLoading = true;
                HttpGet httpPost = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);


            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
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

    private class CheckLikes extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for(int i = 0; i < streamObjects.size(); i++) {
                if(streamDB.checkIfLikeExists(streamObjects.get(i).getActivityID())){
                    System.out.println("Liked");
                    streamObjects.get(i).setLiked(true);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myListAdapter.notifyDataSetChanged();
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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

            boolean liked = allStreamObjects.get(position).getLiked();

            if(liked) {
                holder.likeButton.setTextColor(getResources().getColor(R.color.light_rundle_blue_hex));
                holder.nLikes.setTextColor(getResources().getColor(R.color.light_rundle_blue_hex));
            } else {
                holder.likeButton.setTextColor(getResources().getColor(R.color.secondary_grey));
                holder.nLikes.setTextColor(getResources().getColor(R.color.secondary_grey));
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

            if(allStreamObjects.get(position).getnComments() == 1) {
                holder.seeCommentsButton.setText("comment");
            }

            if(allStreamObjects.get(position).getnLikes() == 1) {
                holder.likeButton.setText("like");
            }

            holder.likeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean liked = allStreamObjects.get(position).getLiked();
                    Integer likes = 0;
                    String postId = allStreamObjects.get(position).getActivityID();

                    if(liked) {
                        holder.likeButton.setTextColor(getResources().getColor(R.color.secondary_grey));
                        holder.nLikes.setTextColor(getResources().getColor(R.color.secondary_grey));
                        likes = allStreamObjects.get(position).getnLikes();
                        likes--;
                        holder.nLikes.setText(likes.toString());
                        allStreamObjects.get(position).setLiked(false);
                        streamDB.deleteLike(postId);
                    } else {
                        holder.likeButton.setTextColor(getResources().getColor(R.color.light_rundle_blue_hex));
                        holder.nLikes.setTextColor(getResources().getColor(R.color.light_rundle_blue_hex));
                        likes = allStreamObjects.get(position).getnLikes();
                        likes++;
                        holder.nLikes.setText(likes.toString());
                        allStreamObjects.get(position).setLiked(true);
                        streamDB.addLike(postId);
                    }

                    allStreamObjects.get(position).setnLikes(likes);
                }
            });

            holder.seeCommentsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int comments = streamObjects.get(position).getnComments();

                    if(comments > 0) {
                        new startCommentFragment().execute(position);
                    } else {
                        Toast.makeText(getActivity(), "No comments to see", Toast.LENGTH_SHORT).show();
                    }

                }
            });

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

    private class startCommentFragment extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            listener.setCommentArray(streamObjects.get(params[0]).getComments());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Fragment fragment = new CommentsFragment();

            FragmentManager fM = getFragmentManager();
            FragmentTransaction fT = fM.beginTransaction();
            fT.add(R.id.stream_activity, fragment);
            fT.addToBackStack(null);
            fT.commit();
        }
    }

    private String calculateTimeDifference(String dateTime) {
        Calendar now = Calendar.getInstance();
        Calendar postDate = Calendar.getInstance();

        DateTime post = new DateTime();


        try {
            post = new DateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
        } catch (java.text.ParseException e) {
            post = null;
            e.printStackTrace();
        }


        postDate.setTime(post.toDate());

        int elapsedDays = 0;
        int elapsedHours = 0;
        int elapsedMinutes = 0;
        int elapsedWeeks = 0;

        int diffYear = 0;
        int diffMonth = 0;


        if(postDate != null) {

            long millis = now.getTimeInMillis() - postDate.getTimeInMillis();
            long difference = Math.abs(millis);

            elapsedDays = (int) (difference/(1000 * 60 * 60 * 24));
            elapsedHours = (int) ((difference -  (1000 * 60 * 60 * 24*elapsedDays))/ (1000 * 60 * 60));
            elapsedMinutes = (int) ((difference -  (1000 * 60 * 60 * 24*elapsedDays))/ (1000 * 60));
            elapsedWeeks = elapsedDays / 7;
        }

        if(diffYear > 0) {
            return (diffYear + " years " + diffMonth + " months ago");
        }

        if(diffMonth > 0) {
            if(elapsedWeeks > 0) {
                return (diffMonth + " months " + elapsedWeeks + " weeks ago");
            }
            return (diffMonth + " months ago");
        }

        if(elapsedWeeks > 0) {
            return (elapsedWeeks + " weeks ago");
        }

        if(elapsedDays > 0) {
            return (elapsedDays + " days " + elapsedHours + " hours ago");
        }

        if(elapsedHours > 0) {

            if(elapsedMinutes >= 30) {
                return ((elapsedHours + 1) + " hours ago");
            }

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

                        ArrayList<UserComment> postComments = new ArrayList<UserComment>();

                        if(children != null) {
                            Iterator y = children.keys();

                            JSONArray childrenArray = new JSONArray();

                            while(y.hasNext()) {
                                String key = (String)y.next();
                                childrenArray.put(children.get(key));
                            }

                            UserComment comment = new UserComment();
                            JSONObject jsonComment = new JSONObject();
                            JSONObject userComment = new JSONObject();

                            for(int j = 0; j < childrenArray.length(); j++) {
                                jsonComment = childrenArray.getJSONObject(j);
                                comment.setCommentContent(jsonComment.getString("content"));
                                comment.setCommentTime(jsonComment.getString("time"));

                                userComment = jsonComment.getJSONObject("user");
                                comment.setFirstLastName(userComment.getString("display_name"));
                                comment.setAvatarUrl(userComment.getString("avatar_thumb"));

                                postComments.add(comment);
                            }

                            System.out.println("Children: " + childrenArray.length());
                            numComments = childrenArray.length();
                        }

                        String id = jsonPost.getString("id");


                        StreamObject activityUpdate = new StreamObject();

                        activityUpdate.setDateTime(postTime);
                        activityUpdate.setName(userName);
                        activityUpdate.setImageURL(avatarURL);
                        activityUpdate.setActivityUserID(activityUserID);
                        activityUpdate.setActivityAction(actionText);
                        activityUpdate.setActivityContent(contentText);
                        activityUpdate.setnComments(numComments);
                        activityUpdate.setActivityID(id);
                        activityUpdate.setComments(postComments);

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
            listener.setCommentArray(comments);

            if(first) {
                first = false;
                new Second().execute();
            }
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
                System.out.println(curUrl);
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








































