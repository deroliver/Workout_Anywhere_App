package dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.practice.derikpc.workoutanywhere.R;

/**
 * Created by DerikPC on 8/25/2015.
 */
public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity current;
    public Dialog d;
    public Button yes, no;
    public boolean result = false;

    public CustomDialog(Activity a) {
        super(a);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                result = true;
                break;

            case R.id.btn_no:
                result = false;
                break;

            default:
                break;
        }
        dismiss();
    }

    public boolean getResult() {
        return result;
    }
}
