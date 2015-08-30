package trainer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.practice.derikpc.workoutanywhere.R;


public class TrainerWarmUpTab extends Fragment {

    ImageView graphic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trainer_warm_up_tab, container, false);

        graphic = (ImageView) view.findViewById(R.id.warm_up_graphic);

        return view;
    }
}
