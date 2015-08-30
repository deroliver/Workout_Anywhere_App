package workouts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.DatePicker;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;

import java.util.List;

public class Workouts extends FragmentActivity implements ProgressDialogListener {
    private ProgressDialog progress;

    @Override
    public void setProgressBar(ProgressDialog progress) {
        this.progress = progress;
    }

    @Override
    public ProgressDialog getProgressBar() {
        return progress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workouts);

        progress = new ProgressDialog(Workouts.this, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.show();

        Fragment fragment = new SpecificWorkout();

        FragmentManager fM = getSupportFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        fT.replace(R.id.workouts_activity, fragment);
        fT.commit();

    }

    @Override
    protected void onDestroy() {
        System.out.println("Workouts onDestroy Called");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
