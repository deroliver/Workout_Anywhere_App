package calendar;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;


public class CalendarView extends FragmentActivity implements ActionBar.TabListener, SeekChangeListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private CaldroidFragment caldroidFragment;
    private CaldroidListener caldroidListener;

    private int[] currentDate;
    private Date date;
    private String trainerType = "";

    private String userName = "";

    @Override
    public String getTrainerType() { return trainerType; }

    @Override
    public void setTrainerType(String trainerType) { this.trainerType = trainerType; }

    @Override
    public void onPageSelected() {}

    @Override
    public Date getDateSelected() {
        return date;
    }

    @Override
    public int[] getDayWeekNumber() {
        return currentDate;
    }

    @Override
    public void setDateSelected(Date date) {
        this.date = date;
    }

    @Override
    public void setDayWeekNumber(int[] date) {
        currentDate = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_info_pager);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");

        viewPager = (ViewPager) findViewById(R.id.calendar_view_pager);
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.calendar_view_pager + ":" + viewPager.getCurrentItem());

                if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((CalendarDateCompletionsFragment) page).updateFragment();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        myAdapter.setUserName(userName);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab formatTab = actionBar.newTab();
        formatTab.setText("Calendar");
        formatTab.setTabListener(this);

        ActionBar.Tab wallTab = actionBar.newTab();
        wallTab.setText("Activity");
        wallTab.setTabListener(this);

        actionBar.addTab(formatTab);
        actionBar.addTab(wallTab);
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
    private String userName;

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if(position == 0) {
            Bundle args = new Bundle();
            args.putString("userName", userName);
            fragment = new CalendarViewFragment();
            fragment.setArguments(args);
        }

        else if(position == 1) {
            fragment = new CalendarDateCompletionsFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {return POSITION_NONE; }
}
