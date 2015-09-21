package com.practice.derikpc.workoutanywhere;



import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.ImageLoader;

import blog.BlogActivity;
import calendar.CalendarView;
import databasetools.UserInfoDatabaseTools;
import profile.UserProfile;
import stream.Stream;
import stream.StreamActivity;
import user.User;
import workouts.Workouts;

public class HomeScreenButtonsFragment extends Fragment {

    private ScrollView scrollView;

    private ImageButton myProfile;
    private ImageButton workouts;
    private ImageButton feed;
    private ImageButton calendar;
    private ImageButton blog;

    private View view;

    private SecondThread secondThread;

    private ImageLoader imageLoader;

    private String USER[] = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_buttons, container, false);

        getActivity().getActionBar().show();

        secondThread = new SecondThread();
        secondThread.run();


        return view;
    }

    private class SecondThread extends Thread {
        private boolean stopNow = true;

        public void close() {
            stopNow = false;
        }

        public void run() {
            while (stopNow) {
                imageLoader = ImageLoader.getInstance();

                myProfile = (ImageButton) view.findViewById(R.id.my_profile_home_button);
                workouts = (ImageButton) view.findViewById(R.id.workouts_home_button);
                feed = (ImageButton) view.findViewById(R.id.my_feed_home_button);
                calendar = (ImageButton) view.findViewById(R.id.calendar_home_button);
                blog = (ImageButton) view.findViewById(R.id.blog_home_button);
                scrollView = (ScrollView) view.findViewById(R.id.home_scroll_view);

                imageLoader.displayImage("drawable://" + R.drawable.user_profile_button, myProfile);
                imageLoader.displayImage("drawable://" + R.drawable.workouts_button, workouts);
                imageLoader.displayImage("drawable://" + R.drawable.my_feed_button, feed);
                imageLoader.displayImage("drawable://" + R.drawable.calendar_button, calendar);
                imageLoader.displayImage("drawable://" + R.drawable.blog_button, blog);

                myProfile.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfile.class);
                        startActivity(intent);
                    }
                });

                workouts.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), Workouts.class);
                        startActivity(intent);
                    }
                });

                feed.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), StreamActivity.class);
                        startActivity(intent);
                    }
                });

                calendar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CalendarView.class);
                        intent.putExtra("userName", User.getUserName());
                        startActivity(intent);
                    }
                });

                blog.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), BlogActivity.class);
                        startActivity(intent);
                    }
                });

                stopNow = false;
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
    }
}
