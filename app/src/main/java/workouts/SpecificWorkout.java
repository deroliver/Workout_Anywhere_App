package workouts;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.practice.derikpc.workoutanywhere.R;




public class SpecificWorkout extends FragmentActivity implements TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;

    private String workoutType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_info_pager);

        Intent intent = getIntent();
        workoutType = intent.getStringExtra("Workout Type");

        viewPager = (ViewPager) findViewById(R.id.workout_view_pager);
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        myAdapter.setWorkout(workoutType);
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab formatTab = actionBar.newTab();
        formatTab.setText("Format");
        formatTab.setTabListener(this);

        ActionBar.Tab wallTab = actionBar.newTab();
        wallTab.setText("Wall");
        wallTab.setTabListener(this);

        ActionBar.Tab postTab = actionBar.newTab();
        postTab.setText("Post");
        postTab.setTabListener(this);

        actionBar.addTab(formatTab);
        actionBar.addTab(wallTab);
        actionBar.addTab(postTab);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}

class MyAdapter extends FragmentPagerAdapter {

    String workoutType = "";

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setWorkout(String workoutType) {
        this.workoutType = workoutType;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        Bundle workout = new Bundle();
        workout.putString("Type", workoutType);


        if(position == 0) {
            fragment = new WorkoutFormatFragmentTab();
            fragment.setArguments(workout);
        }

        else if(position == 1) {
            fragment = new FreestyleWallWorkoutsTab();
        }

        else if(position == 2) {
            fragment = new PostWorkoutFragmentTab();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
