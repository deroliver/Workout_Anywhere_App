package workouts;


import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;

import java.util.ArrayList;

import stream.StreamObject;

public class FreestyleWallWorkoutsTab extends ListFragment {

    private ArrayList<FreestyleWallObject> freestyleWallObjects;
    private MyListAdapter myListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.freestlye_wall, container, false);

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


    public View.OnClickListener likePostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            //Integer nLikes = Integer.parseInt(nLikesTextView.getText().toString());
            //nLikes++;
        }
    };

    private class CustomOnClickListener implements View.OnClickListener {
        private int pos;

        public CustomOnClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "You Clicked Like", Toast.LENGTH_LONG).show();

            // MyListAdapter mAdapter = (MyListAdapter)
            //myListAdapter.notifyDataSetChanged();

        }
    }
}

