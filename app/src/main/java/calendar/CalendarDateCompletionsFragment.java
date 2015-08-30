package calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.derikpc.workoutanywhere.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import stream.StreamObject;
import trainer.TrainerDay;
import trainertools.BWTrainerBeginner;
import trainertools.BWTrainerInt;
import trainertools.OriginalTrainerBeginner;
import trainertools.OriginalTrainerInt;
import workouts.FreestyleWallObject;

public class CalendarDateCompletionsFragment extends ListFragment {

    private ArrayList<FreestyleWallObject> freestyleWallObjects;
    private MyListAdapter myListAdapter;

    private TextView workoutTitle;
    private TextView workoutDate;

    private OriginalTrainerBeginner originalBeginnerTrainer;
    private OriginalTrainerInt originalIntTrainer;
    private BWTrainerBeginner BWBeginnerTrainer;
    private BWTrainerInt BWIntTrainer;

    private ImageView trainerButton;

    private SeekChangeListener listener;

    private Date currentDate;
    private int currentDay = 0;
    int dateWeek[];

    private String title = "";
    private String trainerType = "";
    private String day, month;
    private int date;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (SeekChangeListener)activity;
        Calendar cal = Calendar.getInstance();
        currentDate = cal.getTime();
    }

    public void updateFragment() {
        new Background().execute();
    }

    private class Background extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            currentDate = listener.getDateSelected();
            dateWeek = new int[2];
            dateWeek = listener.getDayWeekNumber();

            trainerType = listener.getTrainerType();

            if(!(dateWeek[0] == -1)) {
                System.out.println(trainerType);
                switch (trainerType) {
                    case "OriginalBeginner": title = originalBeginnerTrainer.getTitle(getActivity(), dateWeek[0], dateWeek[1]); break;
                    case "OriginalInt": title = originalIntTrainer.getTitle(getActivity(), dateWeek[0], dateWeek[1]); break;
                    case "BodyweightBeginner": title = BWBeginnerTrainer.getTitle(getActivity(), dateWeek[0], dateWeek[1]); break;
                    case "BodyweightInt": title = BWIntTrainer.getTitle(getActivity(), dateWeek[0], dateWeek[1]); break;

                    default: title = "Could Not Get Workout";
                }
            } else {
                title = "Not a Trainer Day";
            }

            System.out.println(trainerType);


            System.out.println("Title: " + title);
            System.out.println("Week: " + dateWeek[0] + "  Day: " + dateWeek[1]);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            day = sdf.format(currentDate);
            date = currentDate.getDate();
            month = month_date.format(currentDate.getTime());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            workoutTitle.setText(title);
            workoutDate.setText(day + " " + month + " " + date);
            if(title.equals("Not a Trainer Day")) {
                trainerButton.setEnabled(false);
            } else {
                trainerButton.setEnabled(true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_completions, container, false);

        trainerType = listener.getTrainerType();

        originalBeginnerTrainer = new OriginalTrainerBeginner();
        originalIntTrainer = new OriginalTrainerInt();
        BWBeginnerTrainer = new BWTrainerBeginner();
        BWIntTrainer = new BWTrainerInt();

        workoutDate = (TextView) view.findViewById(R.id.workout_day);
        workoutTitle = (TextView) view.findViewById(R.id.workout_title);
        trainerButton = (ImageView) view.findViewById(R.id.view_trainer_workout_button);
        trainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TrainerDay.class);
                intent.putExtra("trainerType", trainerType);
                intent.putExtra("dayWeek", dateWeek);
                System.out.println("Week: " + dateWeek[0] + "  Day: " + dateWeek[1]);
                startActivity(intent);
            }
        });

        if(trainerType.equals("")) {
            workoutTitle.setText("No Trainer Selected");
        }

        freestyleWallObjects = new ArrayList<FreestyleWallObject>();

        this.myListAdapter = new MyListAdapter(getActivity(), freestyleWallObjects);
        setListAdapter(myListAdapter);

        return view;
    }


    private class MyListAdapter extends ArrayAdapter<FreestyleWallObject> {

        private Context context;
        private ArrayList<FreestyleWallObject> allFreestyleWorkoutObjects;

        private LayoutInflater mInflater;
        private boolean mNotifyOnChange = true;


        public MyListAdapter(Context context, ArrayList<FreestyleWallObject> mStreamObjects) {
            super(context, R.layout.freestyle_wall_object);
            this.context = context;
            this.allFreestyleWorkoutObjects = new ArrayList<FreestyleWallObject>(mStreamObjects);
            this.mInflater = LayoutInflater.from(context);
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
            final ViewHolder holder;
            int type = getItemViewType(position);

            if(convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.freestyle_wall_object, parent, false);

                holder.workoutName = (TextView) convertView.findViewById(R.id.freestyle_workout_video_name);
                holder.workoutPicture = ((ImageView) convertView.findViewById(R.id.freestyle_workout_video_image));
                holder.likeButton = (ImageView) convertView.findViewById(R.id.freestyle_workout_video_like_button);
                holder.workoutType = (TextView) convertView.findViewById(R.id.freestyle_workout_video_type);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.workoutName.setText(allFreestyleWorkoutObjects.get(position).getWorkoutName());
            holder.workoutType.setText(allFreestyleWorkoutObjects.get(position).getWorkoutType());
            holder.pos = position;
            return convertView;
        }

    }

    static class ViewHolder {
        TextView workoutName;
        TextView workoutType;
        ImageView workoutPicture;
        ImageView likeButton;
        int pos;
    }
}
