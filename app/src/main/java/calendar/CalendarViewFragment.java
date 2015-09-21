package calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import databasetools.CompletedDatabaseTools;
import databasetools.UserInfoDatabaseTools;


public class CalendarViewFragment extends Fragment {
    private CaldroidFragment caldroidFragment;
    private FragmentActivity myContext;
    final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
    private static DateTime lastDate;
    private HashMap<Boolean, Boolean> Dates;
    private CompletedDatabaseTools cDBTools;

    private SeekChangeListener listener;
    //private Date currentDate;
    private DateTime firstTrainerDay;
    private DateTime currentDate;

    private int weekNumber = 1;
    private int dayNumber = 0;

    private String userName = "";

    private UserInfoDatabaseTools userDBTools;
    private HashMap<String, String> user;

    private ImageView addTrainerButton;
    private String trainerType = "BeginnerOriginal";

    private int[] weekDayNumber;
    private CompletedDatabaseTools completedDatabaseTools;
    private ArrayList<HashMap<String, String>> completedList;

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);

        listener = (SeekChangeListener)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);
        caldroidFragment = new CaldroidFragment();

        completedDatabaseTools = new CompletedDatabaseTools(getActivity());

        weekDayNumber = new int[2];

        addTrainerButton = (ImageView) view.findViewById(R.id.add_trainer_button);

        addTrainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseATrainerFragment.class);
                intent.putExtra("CurrentTrainer", trainerType);
                startActivityForResult(intent, 1);
            }
        });

        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
        }
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);

            caldroidFragment.setArguments(args);
        }

        new Async().execute();


        return view;
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        private Boolean firstTrainerPresent = false;

        @Override
        protected Void doInBackground(Void... params) {
            completedList = completedDatabaseTools.getAllCompleted();
            userDBTools = new UserInfoDatabaseTools(getActivity());
            Bundle args = getArguments();
            userName = args.getString("userName");

            user = userDBTools.getUserInfoByUserName(userName);

            String firstDay = user.get("firstTrainerDay");
            trainerType = user.get("trainerType");

            if(lastDate != null) {
                currentDate = lastDate;
            } else {
                Calendar cal = Calendar.getInstance();
                currentDate = new DateTime(cal.getTime());
            }

            if(firstDay.equals("NA")) {
                int[] data = new int[] {weekNumber, dayNumber};
                listener.setDayWeekNumber(data);
            } else {
                firstTrainerPresent = true;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date first = Calendar.getInstance().getTime();

                try {
                    first = dateFormat.parse(firstDay);
                    firstTrainerDay = new DateTime(dateFormat.parse(firstDay));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(first.getDate() == Calendar.getInstance().getTime().getDate()) {
                    int[] data = new int[] {weekNumber, dayNumber};
                    listener.setDayWeekNumber(data);
                } else {
                    calculateWeekDayNumber();
                    int[] data = new int[] {weekNumber, dayNumber};
                    listener.setDayWeekNumber(data);
                }
            }

            listener.setDateSelected(currentDate.toDate());

            Dates = new HashMap<Boolean, Boolean>();

            if(lastDate != null) {
                listener.setDateSelected(lastDate.toDate());
                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_date_selected, lastDate.toDate());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(firstTrainerPresent) {
                if(trainerType.equals("OriginalBeginner") || trainerType.equals("BodyweightBeginner") || trainerType.equals("FatBurnerBeginner")) {
                    new setCalendarBackgrounds().execute("Beginner");
                    listener.setTrainerType(trainerType);
                } else if(trainerType.equals("OriginalInt") || trainerType.equals("BodyweightInt") || trainerType.equals("FatBurnerInt")) {
                    new setCalendarBackgrounds().execute("Int");
                    listener.setTrainerType(trainerType);
                }
            }

            caldroidFragment.setCaldroidListener(new CaldroidListener() {
                @Override
                public void onSelectDate(Date date, View view) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    completedWorkoutChecker dateChecker = new completedWorkoutChecker();
                    Boolean found = false;
                    dateChecker.execute(new DateTime(date));


                    if (lastDate != null) {

                        String day = sdf.format(lastDate.toDate());
                        int daysSinceFirst = dateDifference(firstTrainerDay, lastDate);

                        Boolean completed = dateChecker.isCompleted();

                        while(!completed) {
                            completed = dateChecker.isCompleted();
                            found = dateChecker.getResult();
                            System.out.println("Progress: " + dateChecker.isCompleted());
                            System.out.println("Result: " + dateChecker.getResult());
                        }

                        if(daysSinceFirst >= 0 && daysSinceFirst < 84 && !day.equals("Sunday") && !day.equals("Saturday")) {
                            if(found) {
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_date_selected_both, lastDate.toDate());
                            } else {
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_trainer_day, lastDate.toDate());
                            }
                        } else {
                            caldroidFragment.setBackgroundResourceForDate(R.color.white, lastDate.toDate());
                        }

                        lastDate = new DateTime(date);

                        listener.setDateSelected(lastDate.toDate());
                        currentDate = new DateTime(lastDate);

                        calculateWeekDayNumber();
                        weekDayNumber[0] = weekNumber;
                        weekDayNumber[1] = dayNumber;
                        listener.setDayWeekNumber(weekDayNumber);

                        daysSinceFirst = dateDifference(firstTrainerDay, new DateTime(date));
                        day = sdf.format(date);

                        if(daysSinceFirst >= 0 && daysSinceFirst < 84 && !day.equals("Sunday") && !day.equals("Saturday")) {
                            if(found) {
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_date_selected_both, date);
                            } else {
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_trainer_day, date);
                            }
                        } else {
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_date_selected, date);
                        }

                    } else {

                        int daysSinceFirst = dateDifference(firstTrainerDay, new DateTime(date));
                        String day = sdf.format(date);

                        if(daysSinceFirst >= 0 && daysSinceFirst < 84 && !day.equals("Sunday") && !day.equals("Saturday")) {
                            if(found) {
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_date_selected_both, date);
                            } else {
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_trainer_day, date);
                            }
                        } else {
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_date_selected, date);
                        }

                        listener.setDateSelected(date);
                        currentDate = new DateTime(date);
                        lastDate = new DateTime(date);
                    }

                    calculateWeekDayNumber();
                    int[] data = new int[]{weekNumber, dayNumber};
                    listener.setDayWeekNumber(data);
                    caldroidFragment.refreshView();
                }

                @Override
                public void onChangeMonth(int month, int year) {
                    String text = "month: " + month + " year: " + year;
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClickDate(Date date, View view) {
                    Toast.makeText(getActivity(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCaldroidViewCreated() {
                    Toast.makeText(getActivity(), "Caldroid view is created", Toast.LENGTH_SHORT).show();
                }

            });

            FragmentTransaction t = myContext.getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendar1, caldroidFragment);
            t.commit();
        }
    }

    private class completedWorkoutChecker extends AsyncTask<DateTime, Void, Boolean> {
        private Boolean completed = false;
        private Boolean foundCompleted = false;

        @Override
        protected Boolean doInBackground(DateTime... date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String dateString = dateFormat.format(date[0].toDate()).substring(1);

            System.out.println("Selected Date: " + dateString);

            for (HashMap<String, String> fav : completedList) {
                System.out.println("Database Date: " + fav.get("type"));
                if(fav.get("type").equals(dateString)) {
                    completed = true;
                    return true;
                }
            }

            completed = true;
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            foundCompleted = aBoolean;
        }

        private Boolean getResult() {
            return foundCompleted;
        }

        private Boolean isCompleted() {
            return completed;
        }
    }


    public class setCalendarBackgrounds extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... type) {

            ArrayList<Date> completedDates = new ArrayList<Date>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date completedDate = new Date();

            for (HashMap<String, String> fav : completedList) {
                try {
                    completedDate = dateFormat.parse(fav.get("type"));
                    System.out.println("Date: " + fav.get("type"));
                    completedDates.add(completedDate);
                } catch (Exception e){
                }
            }


            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Calendar cal = Calendar.getInstance();
            DateTime today = DateTime.now();

            Date nextDate = firstTrainerDay.toDate();
            String day = "";
            int i = 0;
            int j = 0;

            boolean completed = false;
            ArrayList<Integer> positions = new ArrayList<Integer>();

            while(i < 84) {
                i++;

                for(int l = 0; l < completedDates.size(); l++) {
                    System.out.println("Current: " + nextDate.toString() + "   Completed" + completedDates.get(l).toString());
                    if(nextDate.compareTo(completedDates.get(l)) == 0) {
                        System.out.println("equal");
                        completed = true;
                        completedDates.remove(l);
                        continue;
                    }
                }

                day = sdf.format(nextDate);

                if(type[0].equals("Beginner")) {
                    if (!day.equals("Saturday") && !day.equals("Sunday")) {
                        if(completed) {
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_workout_and_trainer_day, nextDate);
                            completed = false;
                        } else {
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_trainer_day, nextDate);
                        }
                    }
                } else if(type[0].equals("Int")) {
                    if (!day.equals("Sunday")) {
                        if(completed) {
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_workout_and_trainer_day, nextDate);
                            completed = false;
                        } else {
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_trainer_day, nextDate);
                        }
                    }
                }

                if(completed) {
                    caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_completed_workout_day, nextDate);
                    completed = false;
                }


                cal.setTime(nextDate);
                cal.add(Calendar.DATE, 1);
                nextDate = cal.getTime();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            caldroidFragment.refreshView();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String typeChosen;

        if(data != null) {
            typeChosen = data.getStringExtra("trainerType");
            System.out.println(typeChosen);
        } else {
            typeChosen = "";
        }


        if(!trainerType.equals(typeChosen) && !typeChosen.equals("")) {
            trainerType = typeChosen;
            listener.setTrainerType(trainerType);
            System.out.println(trainerType);
            userDBTools.updateTrainerTypeByUserName(userName, trainerType);

            if(firstTrainerDay != null) {
                resetCalendarBackground();
            }

            setFirstTrainerDay();
            calculateWeekDayNumber();
            int[] weekDay = new int[]{weekNumber, dayNumber};

            listener.setDayWeekNumber(weekDay);

            if(typeChosen.equals("OriginalBeginner") || typeChosen.equals("BodyweightBeginner") || typeChosen.equals("FatBurnerBeginner")) {

                new setCalendarBackgrounds().execute("Beginner");

            } else if(typeChosen.equals("OriginalInt") || typeChosen.equals("BodyweightInt") || typeChosen.equals("FatBurnerInt")) {

                new setCalendarBackgrounds().execute("Int");

            }
        }
    }

    public void resetCalendarBackground() {
        Calendar cal = Calendar.getInstance();
        Date nextDate = firstTrainerDay.toDate();
        int i = 0;

        while(i < 84) {
            i++;
            caldroidFragment.setBackgroundResourceForDate(R.color.white, nextDate);

            cal.setTime(nextDate);
            cal.add(Calendar.DATE, 1);
            nextDate = cal.getTime();
        }

        caldroidFragment.refreshView();
    }


    public void calculateWeekDayNumber() {
        Date first = firstTrainerDay.toDate();
        Date current = currentDate.toDate();

        if(current.before(first) && !(current == first)) {
            weekNumber = -1;
            dayNumber = -1;
        } else {
            int daysSinceFirst = dateDifference(firstTrainerDay, currentDate) + 1;
            weekNumber = (daysSinceFirst / 7) + 1;
            dayNumber = currentDate.getDayOfWeek() - 1;
        }
    }


    public int dateDifference(DateTime date1, DateTime date2) {
        return Days.daysBetween(date1, date2).getDays();
    }

    public void setFirstTrainerDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Calendar cal = Calendar.getInstance();
        Date nextDate = cal.getTime();
        String day = sdf.format(nextDate);

        if(day.equals("Monday")){
            firstTrainerDay = new DateTime(cal.getTime());
        } else {
            while(!day.equals("Monday")) {
                cal.setTime(nextDate);
                cal.add(Calendar.DATE, 1);
                nextDate = cal.getTime();
                day = sdf.format(nextDate);
            }
            firstTrainerDay = new DateTime(nextDate);
        }

        userDBTools.updateFirstTrainerDayByUserByUserName(userName, firstTrainerDay.getMonthOfYear() + "/" + firstTrainerDay.getDayOfMonth() + "/" + firstTrainerDay.getYear());
        caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_trainer_day, firstTrainerDay.toDate());
    }


}



