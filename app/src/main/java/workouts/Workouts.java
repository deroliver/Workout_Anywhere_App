package workouts;



import android.app.Activity;
import android.os.Bundle;

import com.practice.derikpc.workoutanywhere.R;

public class Workouts extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do loading data here

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI here
                        setContentView(R.layout.workouts);
                    }
                });
            }
        }).start();
    }
}
