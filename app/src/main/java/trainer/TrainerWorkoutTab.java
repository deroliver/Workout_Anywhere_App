package trainer;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import trainertools.BWTrainerBeginner;
import trainertools.BWTrainerInt;
import trainertools.OriginalTrainerBeginner;
import trainertools.OriginalTrainerInt;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.practice.derikpc.workoutanywhere.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;


public class TrainerWorkoutTab extends Fragment {

    private TextView workoutInfo;
    private String info;
    private ImageView workoutImage;
    private TrainerTypeListener listener;
    private String trainerType;
    private int[] dayWeek;
    private String imageString;
    private ImageLoader imageLoader;
    private String packageName = "";
    private DisplayImageOptions options;

    public void updateFragment() {
        dayWeek = listener.getDayWeek();
        trainerType = listener.getTrainerType();

        System.out.println("Trainer Type: " + trainerType);
        System.out.println("Week: " + dayWeek[0] + "   Day: " + dayWeek[1]);

        if(dayWeek != null) {
            switch (trainerType) {
                case "OriginalBeginner": {
                    info = OriginalTrainerBeginner.getInstance().getContent(getActivity(), dayWeek[0], dayWeek[1]);
                    imageString = OriginalTrainerBeginner.getInstance().getWorkoutImage(getActivity(), dayWeek[0], dayWeek[1]);
                    break;
                }

                case "OriginalInt": {
                    info = OriginalTrainerInt.getInstance().getContent(getActivity(), dayWeek[0], dayWeek[1]);
                    imageString = OriginalTrainerInt.getInstance().getWorkoutImage(getActivity(), dayWeek[0], dayWeek[1]);
                    break;
                }
                case "BodyweightBeginner":
                    info = BWTrainerBeginner.getInstance().getContent(getActivity(), dayWeek[0], dayWeek[1]);
                    imageString = BWTrainerBeginner.getInstance().getWorkoutImage(getActivity(), dayWeek[0], dayWeek[1]);
                    break;
                case "BodyweightInt":
                    info = BWTrainerInt.getInstance().getContent(getActivity(), dayWeek[0], dayWeek[1]);
                    imageString = BWTrainerInt.getInstance().getWorkoutImage(getActivity(), dayWeek[0], dayWeek[1]);
                    break;

                default:
                    info = "Could Not Get Workout Info";
                    imageString = "";
                    break;
            }

            System.out.println(imageString);
            int resID = getResources().getIdentifier(imageString, "drawable", packageName);

            imageLoader.displayImage("drawable://" + resID, workoutImage, options);

            workoutInfo.setText(info);
        }

    }

    public DisplayImageOptions getDisplayOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.loading_error)
                .showImageOnFail(R.drawable.loading_error)
                .delayBeforeLoading(100)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();

        return  options;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (TrainerTypeListener)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trainer_workout_tab, container, false);

        packageName = getActivity().getPackageName();

        workoutInfo = (TextView) view.findViewById(R.id.workout_info_textview);
        workoutImage = (ImageView) view.findViewById(R.id.workout_trainer_image);

        imageLoader = ImageLoader.getInstance();
        options = getDisplayOptions();
        dayWeek = new int[2];
        return view;
    }
}
