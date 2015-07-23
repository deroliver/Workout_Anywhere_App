package blog;


import android.app.ListActivity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.derikpc.workoutanywhere.R;

import java.util.ArrayList;

import stream.StreamObject;
import workouts.FreestyleWallObject;

public class BlogActivity extends ListActivity {

    private ArrayList<FreestyleWallObject> freestyleWallObjects;
    private MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        freestyleWallObjects = new ArrayList<FreestyleWallObject>();
        FreestyleWallObject FWO = new FreestyleWallObject();
        FreestyleWallObject FWO2 = new FreestyleWallObject();
        FreestyleWallObject FWO3 = new FreestyleWallObject();

        FWO.setWorkoutName("Full Body HIIT Workout");
        FWO.setWorkoutPicture(getResources().getDrawable(R.drawable.workout_video_example));
        FWO.setWorkoutType("Full Body");

        FWO2.setWorkoutName("Full Body HIIT Workout");
        FWO2.setWorkoutPicture(getResources().getDrawable(R.drawable.workout_video_example));
        FWO2.setWorkoutType("Full Body");

        FWO3.setWorkoutName("Full Body HIIT Workout");
        FWO3.setWorkoutPicture(getResources().getDrawable(R.drawable.workout_video_example));
        FWO3.setWorkoutType("Full Body");

        freestyleWallObjects.add(FWO);
        freestyleWallObjects.add(FWO2);
        freestyleWallObjects.add(FWO3);

        myListAdapter = new MyListAdapter(getApplication(), freestyleWallObjects);
        setListAdapter(myListAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do loading data here

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI here

                        setContentView(R.layout.blog_activity);
                    }
                });
            }
        }).start();
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

            if(convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.freestyle_wall_object, parent, false);

                holder.workoutName = (TextView) convertView.findViewById(R.id.freestyle_workout_video_name);
                holder.workoutPicture = ((LinearLayout) convertView.findViewById(R.id.freestyle_workout_video_image));
                holder.likeButton = (ImageButton) convertView.findViewById(R.id.freestyle_workout_video_like_button);
                holder.workoutType = (TextView) convertView.findViewById(R.id.freestyle_workout_video_type);
                // holder.likeButton.setOnClickListener(new CustomOnClickListener(position));
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.workoutName.setText(allFreestyleWorkoutObjects.get(position).getWorkoutName());
            holder.workoutPicture.setBackground(allFreestyleWorkoutObjects.get(position).getWorkoutPicture());
            holder.workoutType.setText(allFreestyleWorkoutObjects.get(position).getWorkoutType());
            holder.pos = position;
            return convertView;
        }
    }


    static class ViewHolder {
        TextView workoutName;
        TextView workoutType;
        LinearLayout workoutPicture;
        ImageButton likeButton;
        int pos;
    }
}
