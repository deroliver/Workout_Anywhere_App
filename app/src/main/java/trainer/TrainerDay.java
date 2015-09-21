package trainer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.practice.derikpc.workoutanywhere.HomeScreen;
import com.practice.derikpc.workoutanywhere.R;

import java.util.List;

import databasetools.UserInfoDatabaseTools;
import user.User;
import workouts.FreestyleWallWorkoutsTab;
import workouts.PostWorkoutFragmentTab;
import workouts.WorkoutFormatFragmentTab;


public class TrainerDay extends FragmentActivity implements ActionBar.TabListener, TrainerTypeListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private MyAdapter myAdapter;

    private View view;

    private String workoutType = "";

    private ActionBar.Tab formatTab;
    private ActionBar.Tab wallTab;
    private ActionBar.Tab postTab;

    private TrainerTypeListener listener;

    private String trainerType;
    private int[] dayWeek;
    private ProgressDialog progress;
    private UserInfoDatabaseTools uDBTools;

    @Override
    public void setProgressBar(ProgressDialog progress) {
        this.progress = progress;
    }

    @Override
    public ProgressDialog getProgressBar() {
        return progress;
    }

    @Override
    public int[] getDayWeek() {
        return dayWeek;
    }

    @Override
    public void setDayWeek(int[] dayWeek) {
        this.dayWeek = dayWeek;
    }

    @Override
    public void setTrainerType(String trainerType) {
        this.trainerType = trainerType;
    }

    @Override
    public String getTrainerType() {
        return trainerType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_info_pager);
        dayWeek = new int[2];

        uDBTools = new UserInfoDatabaseTools(this);

        progress = new ProgressDialog(TrainerDay.this, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.show();

        Intent intent = getIntent();

        trainerType = intent.getStringExtra("trainerType");
        dayWeek = intent.getIntArrayExtra("dayWeek");

        viewPager = (ViewPager) findViewById(R.id.workout_view_pager);
        viewPager.setOffscreenPageLimit(2);

        myAdapter = new MyAdapter(getSupportFragmentManager());

        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.workout_view_pager + ":" + viewPager.getCurrentItem());

                if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((TrainerWorkoutTab) page).updateFragment();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        actionBar = this.getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        formatTab = actionBar.newTab();
        formatTab.setText("WarmUp");
        formatTab.setTabListener(this);

        wallTab = actionBar.newTab();
        wallTab.setText("Workout");
        wallTab.setTabListener(this);

        postTab = actionBar.newTab();
        postTab.setText("Stretch/Core");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.sign_out: {
                signOut();
                return true;
            }

            case R.id.exit_the_app: {
                System.exit(0);
                return true;
            }

            case R.id.home_screen: {
                homeScreen();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        String username = User.getUserName();
        uDBTools.updateSignedInByUsername(username, "false");

        Intent intent = new Intent(this, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void homeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}


class MyAdapter extends FragmentPagerAdapter {

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println(position);
        Fragment fragment = null;

        if(position == 0) {
            fragment = new TrainerWarmUpTab();
        }

        else if(position == 1) {
            fragment = new TrainerWorkoutTab();
        }

        else if(position == 2) {
            fragment = new CoreStretchTab();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
