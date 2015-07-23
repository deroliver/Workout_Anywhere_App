package workouts;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.practice.derikpc.workoutanywhere.R;


public class WorkoutButtonsFragment extends Fragment {

    private static ImageButton Beginner;
    private static ImageButton Intermediate;
    private static ImageButton Beast;
    private static ImageButton FirstStep;
    private static ImageButton Core4;
    private static ImageButton Freestyle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_buttons, container, false);
        Beginner = (ImageButton) view.findViewById(R.id.beginner_workout_button);
        Intermediate = (ImageButton) view.findViewById(R.id.intermediate_workout_button);
        Beast = (ImageButton) view.findViewById(R.id.beast_workout_button);
        FirstStep = (ImageButton) view.findViewById(R.id.first_step_button);
        Core4 = (ImageButton) view.findViewById(R.id.core_workout_button);
        Freestyle = (ImageButton) view.findViewById(R.id.freestyle_wall_button);


        Beginner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);

                intent.putExtra("Workout Type", "Beginner");

                startActivity(intent);
            }
        });

        Intermediate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);

                intent.putExtra("Workout Type", "Intermediate");

                startActivity(intent);


            }
        });

        Beast.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);

                intent.putExtra("Workout Type", "Beast");

                startActivity(intent);

            }
        });

        FirstStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);

                intent.putExtra("Workout Type", "FirstStep");

                startActivity(intent);

            }
        });

        Core4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);

                intent.putExtra("Workout Type", "Core");

                startActivity(intent);

            }
        });

        Freestyle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);

                intent.putExtra("Workout Type", "Freestyle");

                startActivity(intent);

            }
        });

        return view;
    }
}
