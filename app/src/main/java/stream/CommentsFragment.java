package stream;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.practice.derikpc.workoutanywhere.R;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CommentsFragment extends Fragment {

    private CommentListener listener;
    private ArrayList<UserComment> comments;
    private MyListAdapter myListAdapter;
    private ListView listView;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (CommentListener)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_comments, container, false);

        listView = (ListView) view.findViewById(android.R.id.list);

        new Background().execute();

        return view;
    }

    private class Background extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            imageLoader = ImageLoader.getInstance();
            options = getDisplayOptions();

            comments = listener.getCommentArray();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(comments != null) {
                System.out.println("Comments present");
                System.out.println(comments.get(0).getCommentContent());
                myListAdapter = new MyListAdapter(getActivity(), comments);

                listView.setAdapter(myListAdapter);
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



    private class MyListAdapter extends ArrayAdapter<UserComment> {

        private Context context;
        private ArrayList<UserComment> allComments;

        private LayoutInflater mInflater;
        private boolean mNotifyOnChange = true;


        public MyListAdapter(Context context, ArrayList<UserComment> comments) {
            super(context, R.layout.stream_object);
            this.context = context;
            this.allComments = comments;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return allComments.size();
        }

        @Override
        public UserComment getItem(int position) {
            return allComments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getPosition(UserComment item) {
            return allComments.indexOf(item);
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
                convertView = mInflater.inflate(R.layout.stream_comment_object, parent, false);
                holder = new ViewHolder();
                holder.personName = (TextView) convertView.findViewById(R.id.stream_comment_name);
                holder.personPicture = ((ImageView) convertView.findViewById(R.id.stream_comment_image));
                holder.content = (TextView) convertView.findViewById(R.id.stream_comment_text);
                holder.bar = (ProgressBar) convertView.findViewById(R.id.stream_comment_progress_bar);
                holder.time = (TextView) convertView.findViewById(R.id.stream_comment_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.personName.setText(allComments.get(position).getFirstLastName());
            holder.time.setText(calculateTimeDifference(allComments.get(position).getCommentTime()));
            holder.content.setText(allComments.get(position).getCommentContent());
            display(holder.personPicture, allComments.get(position).getAvatarUrl(), holder.bar);

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
        TextView content;
        ProgressBar bar;
        TextView time;
        int pos;
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
}
