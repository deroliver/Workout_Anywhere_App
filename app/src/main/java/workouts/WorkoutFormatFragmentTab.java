package workouts;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.derikpc.workoutanywhere.R;

import org.w3c.dom.Text;

public class WorkoutFormatFragmentTab extends Fragment {

    private String type = "";
    private ImageView format;
    private ImageView workoutType;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_format_tab_fragment, container, false);

        type = getArguments().getString("Type");

        System.out.println("===========================" + type);

        format = (ImageView) view.findViewById(R.id.workout_format_image_view);
        workoutType = (ImageView) view.findViewById(R.id.workout_type_image_view);
        textView = (TextView) view.findViewById(R.id.workout_format_text_view);

        if(type.equals("Beginner")) {
            format.setImageDrawable(getResources().getDrawable(R.drawable.beginner_sample_workout));
            workoutType.setImageResource(R.drawable.beginner_workouts_button);
            textView.setTextColor(getResources().getColor(R.color.rundle_blue_hex));
        }

        else if(type.equals("Intermediate")) {
            format.setImageDrawable(getResources().getDrawable(R.drawable.intermediate_sample_workout));
            workoutType.setImageResource(R.drawable.intermediate_workout_button);
            textView.setTextColor(getResources().getColor(R.color.pink_workout_format));
        }

        else if(type.equals("Beast")) {
            format.setImageDrawable(getResources().getDrawable(R.drawable.beast_sample_workout));
            workoutType.setImageResource(R.drawable.beast_workout_button);
            textView.setTextColor(getResources().getColor(R.color.green_workout_format));
        }

        return view;
    }
}
