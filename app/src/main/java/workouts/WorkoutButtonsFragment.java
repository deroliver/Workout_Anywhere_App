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
        Beginner = (ImageButton) view.findViewById(R.id.beginnerButton);
        Intermediate = (ImageButton) view.findViewById(R.id.intermediateButton);
        Beast = (ImageButton) view.findViewById(R.id.beastButton);
        FirstStep = (ImageButton) view.findViewById(R.id.firstStepButton);
        Core4 = (ImageButton) view.findViewById(R.id.core4Button);
        Freestyle = (ImageButton) view.findViewById(R.id.freestyleButton);


        Beginner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpecificWorkout.class);
                startActivity(intent);
            }
        });

        Intermediate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        Beast.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        FirstStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        Core4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        Freestyle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
