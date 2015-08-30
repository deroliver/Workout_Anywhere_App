package workouts;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.practice.derikpc.workoutanywhere.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class WorkoutFormatFragmentTab extends Fragment {

    private ImageView beginnerFormatImg;
    private ImageView beginnerButtonImg;

    private ImageView intermediateFormatImg;
    private ImageView intermediateButtonImg;

    private ImageView beastFormatImg;
    private ImageView beastButtonImg;

    private SecondThread secondThread;

    File beG;
    File inT;
    File beA;
    File beGB;
    File inTB;
    File beAB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_format_tab_fragment, container, false);

        beginnerButtonImg = (ImageView) view.findViewById(R.id.beginner_button_pic);
        beginnerFormatImg = (ImageView) view.findViewById(R.id.beginner_format_pic);

        intermediateButtonImg = (ImageView) view.findViewById(R.id.intermediate_button_pic);
        intermediateFormatImg = (ImageView) view.findViewById(R.id.intermediate_format_pic);

        beastButtonImg = (ImageView) view.findViewById(R.id.beast_button_pic);
        beastFormatImg = (ImageView) view.findViewById(R.id.beast_format_pic);

        secondThread = new SecondThread();
        secondThread.run();

        return view;
    }

    private class SecondThread extends Thread {
        private boolean stopNow = false;

        public void close(){
            stopNow = false;
        }

        public void run() {
            stopNow = true;
            while(stopNow) {
                beG = new File("///android_asset/beginner_sample_workout.png");
                inT = new File("///android_asset/intermediate_sample_workout.png");
                beA = new File("///android_asset/beast_sample_workout.png");

                beGB = new File("///android_asset/beginner_workouts_button.png");
                inTB = new File("///android_asset/intermediate_workout_button.png");
                beAB = new File("///android_asset/beast_workout_button.png");


                Picasso.with(getActivity()).load(beG).into(beginnerFormatImg);
                Picasso.with(getActivity()).load(beGB).into(beginnerButtonImg);

                Picasso.with(getActivity()).load(inT).into(intermediateFormatImg);
                Picasso.with(getActivity()).load(inTB).into(intermediateButtonImg);

                Picasso.with(getActivity()).load(beA).into(beastFormatImg);
                Picasso.with(getActivity()).load(beAB).into(beastButtonImg);

                stopNow = false;
            }
        }
    }


    @Override
    public void onDestroyView() {
        System.out.println("Workouts onDestroyView");
        secondThread.close();
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

        if(beGB != null) {
            Picasso.with(getActivity()).invalidate(beGB);
            beGB = null;
        }

        if(inTB != null) {
            Picasso.with(getActivity()).invalidate(inTB);
            inTB = null;
        }

        if(beAB != null) {
            Picasso.with(getActivity()).invalidate(beAB);
            beAB = null;
        }
    }

}
