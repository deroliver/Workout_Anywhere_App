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

import com.squareup.picasso.Picasso;

import java.io.File;

import blog.BlogActivity;
import calendar.Calendar;
import stream.Stream;
import workouts.Workouts;

public class HomeScreenButtonsFragment extends Fragment {

    private ScrollView scrollView;

    private ImageButton myProfile;
    private ImageButton workouts;
    private ImageButton feed;
    private ImageButton calendar;
    private ImageButton blog;

    private View view;

    File myP = new File("///android_asset/user_profile_button.png");
    File woR = new File("///android_asset/workouts_button.png");
    File myF = new File("///android_asset/my_feed_button.png");
    File caL = new File("///android_asset/calendar_button.png");
    File blO = new File("///android_asset/blog_button.png");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_buttons, container, false);

        Picasso p = Picasso.with(getActivity());

        myProfile = (ImageButton) view.findViewById(R.id.my_profile_home_button);
        workouts = (ImageButton) view.findViewById(R.id.workouts_home_button);
        feed = (ImageButton) view.findViewById(R.id.my_feed_home_button);
        calendar = (ImageButton) view.findViewById(R.id.calendar_home_button);
        blog = (ImageButton) view.findViewById(R.id.blog_home_button);

        scrollView = (ScrollView) view.findViewById(R.id.home_scroll_view);


        Picasso.with(getActivity()).load(myP).into(myProfile);
        Picasso.with(getActivity()).load(woR).into(workouts);
        Picasso.with(getActivity()).load(myF).into(feed);
        Picasso.with(getActivity()).load(caL).into(calendar);
        Picasso.with(getActivity()).load(blO).into(blog);


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

                /*
                Fragment fragment = new WorkoutButtonsFragment();

                FragmentManager fM = getFragmentManager();
                FragmentTransaction fT = fM.beginTransaction();
                fT.add(R.id.home_screen_activity, fragment);
                fT.addToBackStack(null);
                fT.commit();
                */
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Stream.class);
                startActivity(intent);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {

            @Override
          public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Calendar.class);
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

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Picasso.with(getActivity()).invalidate(myP);
        myP = null;

        Picasso.with(getActivity()).invalidate(woR);
        woR = null;

        Picasso.with(getActivity()).invalidate(myF);
        myF = null;

        Picasso.with(getActivity()).invalidate(caL);
        caL = null;

        Picasso.with(getActivity()).invalidate(blO);
        blO = null;
    }
}
