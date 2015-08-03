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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;

public class WorkoutFormatFragmentTab extends Fragment {

    private String type = "";
    private ImageView format;
    private ImageView workoutType;
    TextView textView;

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
        System.out.println("OnCreate Format Called");

        beG = new File("///android_asset/beginner_sample_workout.png");
        inT = new File("///android_asset/intermediate_sample_workout.png");
        beA = new File("///android_asset/beast_sample_workout.png");

        beGB = new File("///android_asset/beginner_workouts_button.png");
        inTB = new File("///android_asset/intermediate_workout_button.png");
        beAB = new File("///android_asset/beast_workout_button.png");

        type = getArguments().getString("Type");

        System.out.println("===========================" + type);

        format = (ImageView) view.findViewById(R.id.workout_format_image_view);
        workoutType = (ImageView) view.findViewById(R.id.workout_type_image_view);
        textView = (TextView) view.findViewById(R.id.workout_format_text_view);

        if(type.equals("Beginner")) {
            Picasso.with(getActivity()).load(beG).into(format);
            Picasso.with(getActivity()).load(beGB).into(workoutType);
            textView.setTextColor(getResources().getColor(R.color.rundle_blue_hex));
        }

        else if(type.equals("Intermediate")) {
            Picasso.with(getActivity()).load(inT).into(format);
            Picasso.with(getActivity()).load(inTB).into(workoutType);
            textView.setTextColor(getResources().getColor(R.color.pink_workout_format));
        }

        else if(type.equals("Beast")) {
            Picasso.with(getActivity()).load(beA).into(format);
            Picasso.with(getActivity()).load(beAB).into(workoutType);
            textView.setTextColor(getResources().getColor(R.color.green_workout_format));
        }
        return view;
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
