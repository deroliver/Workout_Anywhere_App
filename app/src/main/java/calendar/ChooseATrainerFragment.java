package calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;

import dialog.CustomDialog;

public class ChooseATrainerFragment extends FragmentActivity {

    private ImageView originalBeginnerButton;
    private ImageView originalIntButton;

    private ImageView bodyWeightBeginnerButton;
    private ImageView bodyWeightIntButton;

    private ImageView fatBurnerBeginnerButton;
    private ImageView fatBurnerIntButton;

    private String typeChosen = "";
    private Boolean yesOrNo = false;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_a_trainer);

        customDialog = new CustomDialog(ChooseATrainerFragment.this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        originalBeginnerButton = (ImageView) findViewById(R.id.original_beginner_trainer_button);
        originalIntButton = (ImageView) findViewById(R.id.original_int_trainer_button);

        bodyWeightBeginnerButton = (ImageView) findViewById(R.id.week12_beginner_trainer_button);
        bodyWeightIntButton = (ImageView) findViewById(R.id.week12_int_trainer_button);

        fatBurnerBeginnerButton = (ImageView) findViewById(R.id.fat_burner_beginner_trainer_button);
        fatBurnerIntButton = (ImageView) findViewById(R.id.fat_burner_int_trainer_button);



        originalBeginnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseATrainerFragment.this.customDialog.show();
                typeChosen = "OriginalBeginner";
            }
        });

        originalIntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseATrainerFragment.this.customDialog.show();
                typeChosen = "OriginalInt";
            }
        });

        bodyWeightBeginnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseATrainerFragment.this.customDialog.show();
                typeChosen = "BodyweightBeginner";
            }
        });

        bodyWeightIntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseATrainerFragment.this.customDialog.show();
                typeChosen = "BodyweightInt";
            }
        });

        fatBurnerBeginnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseATrainerFragment.this.customDialog.show();
                typeChosen = "FatBurnerBeginner";
            }
        });

        fatBurnerIntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseATrainerFragment.this.customDialog.show();
                typeChosen = "FatBurnerInt";
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        String type = "";

        yesOrNo = customDialog.getResult();
        System.out.println(yesOrNo.toString());
        Toast.makeText(getApplicationContext(), yesOrNo.toString(), Toast.LENGTH_LONG);
        if(yesOrNo) {
            type = typeChosen;
        }

        if(type.equals("")) {
            finish();
        } else {
            intent.putExtra("trainerType", typeChosen);
            setResult(RESULT_OK, intent);
        }

        finish();
    }
}
