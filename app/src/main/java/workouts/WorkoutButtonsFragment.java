package workouts;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.practice.derikpc.workoutanywhere.R;
import com.squareup.picasso.Picasso;

import java.io.File;


public class WorkoutButtonsFragment extends Fragment {

    private ImageButton Beginner;
    private ImageButton Intermediate;
    private ImageButton Beast;
    private ImageButton FirstStep;
    private ImageButton Core4;
    private ImageButton Freestyle;

    private View view;

    File beG = new File("///android_asset/beginner_workouts_button.png");
    File inT = new File("///android_asset/intermediate_workout_button.png");
    File beA = new File("///android_asset/beast_workout_button.png");
    File coR = new File("///android_asset/core_workout_button.png");
    File fiR = new File("///android_asset/first_step_workout_button.png");
    File frE = new File("///android_asset/freestyle_workout_button.png");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.workout_buttons, container, false);


        Beginner = (ImageButton) view.findViewById(R.id.beginner_workout_button);
        Intermediate = (ImageButton) view.findViewById(R.id.intermediate_workout_button);
        Beast = (ImageButton) view.findViewById(R.id.beast_workout_button);
        FirstStep = (ImageButton) view.findViewById(R.id.first_step_workout_button);
        Core4 = (ImageButton) view.findViewById(R.id.core_workout_button);
        Freestyle = (ImageButton) view.findViewById(R.id.freestyle_workout_button);


        Picasso.with(getActivity()).load(beG).into(Beginner);
        Picasso.with(getActivity()).load(inT).into(Intermediate);
        Picasso.with(getActivity()).load(beA).into(Beast);
        Picasso.with(getActivity()).load(fiR).into(FirstStep);
        Picasso.with(getActivity()).load(coR).into(Core4);
        Picasso.with(getActivity()).load(frE).into(Freestyle);


        Beginner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle type = new Bundle();
                type.putString("Workout Type", "Beginner");

                Fragment fragment = new SpecificWorkout();
                fragment.setArguments(type);

                FragmentManager fM = getFragmentManager();
                FragmentTransaction fT = fM.beginTransaction();
                fT.replace(R.id.workouts_activity, fragment);
                fT.addToBackStack("Specific");
                fT.commit();
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

    @Override
    public void onResume() {
        super.onResume();
        beG = new File("///android_asset/beginner_workouts_button.png");
        inT = new File("///android_asset/intermediate_workout_button.png");
        beA = new File("///android_asset/beast_workout_button.png");
        coR = new File("///android_asset/core_workout_button.png");
        fiR = new File("///android_asset/first_step_workout_button.png");
        frE = new File("///android_asset/freestyle_workout_button.png");

        Picasso.with(getActivity()).load(beG).into(Beginner);
        Picasso.with(getActivity()).load(inT).into(Intermediate);
        Picasso.with(getActivity()).load(beA).into(Beast);
        Picasso.with(getActivity()).load(fiR).into(FirstStep);
        Picasso.with(getActivity()).load(coR).into(Core4);
        Picasso.with(getActivity()).load(frE).into(Freestyle);

    }

    @Override
    public void onDestroyView() {
        System.out.println("Workouts onDestroyView");
        super.onDestroyView();

        if(beG != null) {
            Picasso.with(getActivity()).invalidate(beG);
            beG = null;
        }

        if(inT != null) {
            Picasso.with(getActivity()).invalidate(inT);
            inT = null;
        }

        if(beA != null) {
            Picasso.with(getActivity()).invalidate(beA);
            beA = null;
        }

        if(coR != null) {
            Picasso.with(getActivity()).invalidate(coR);
            coR = null;
        }

        if(fiR != null) {
            Picasso.with(getActivity()).invalidate(fiR);
            fiR = null;
        }

        if(frE != null) {
            Picasso.with(getActivity()).invalidate(frE);
            frE = null;
        }
    }
}

