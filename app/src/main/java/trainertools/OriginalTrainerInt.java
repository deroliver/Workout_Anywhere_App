package trainertools;

import android.content.Context;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;

public class OriginalTrainerInt {
    String [] weekStringArray;
    private static OriginalTrainerInt trainer = null;

    public static OriginalTrainerInt getInstance() {
        if(trainer == null) {
            trainer = new OriginalTrainerInt();
        }
        return trainer;
    }

    public String getTitle(Context context, int weekNumber, int dayNumber) {
        String content;

        switch(weekNumber) {
            case  1: weekStringArray = context.getResources().getStringArray(R.array.int_week_1);  break;
            case  2: weekStringArray = context.getResources().getStringArray(R.array.int_week_2);  break;
            case  3: weekStringArray = context.getResources().getStringArray(R.array.int_week_3);  break;
            case  4: weekStringArray = context.getResources().getStringArray(R.array.int_week_4);  break;
            case  5: weekStringArray = context.getResources().getStringArray(R.array.int_week_5);  break;
            case  6: weekStringArray = context.getResources().getStringArray(R.array.int_week_6);  break;
            case  7: weekStringArray = context.getResources().getStringArray(R.array.int_week_7);  break;
            case  8: weekStringArray = context.getResources().getStringArray(R.array.int_week_8);  break;
            case  9: weekStringArray = context.getResources().getStringArray(R.array.int_week_9);  break;
            case 10: weekStringArray = context.getResources().getStringArray(R.array.int_week_10); break;
            case 11: weekStringArray = context.getResources().getStringArray(R.array.int_week_11); break;
            case 12: weekStringArray = context.getResources().getStringArray(R.array.int_week_12); break;

            default:
                Toast.makeText(context, "Could Not Get Trainer Info", Toast.LENGTH_LONG).show();
        }

        if(dayNumber == 5) {
            return "Rest Day";
        } else if(dayNumber == 6) {
            return "Meal Preparation Day";
        } else if(weekStringArray == null || weekNumber > 12) {
            Toast.makeText(context, "Could Not Get Trainer Info", Toast.LENGTH_LONG).show();
            return "Non-Trainer Day";
        } else {
            content = weekStringArray[dayNumber];
            String[] subStrings = content.split("~");
            String title = subStrings[0];
            System.out.println("Title: " + title);

            return title;
        }
    }


    public String getContent(Context context, int weekNumber, int dayNumber) {
        String content;

        switch(weekNumber) {
            case  1: weekStringArray = context.getResources().getStringArray(R.array.int_week_1);  break;
            case  2: weekStringArray = context.getResources().getStringArray(R.array.int_week_2);  break;
            case  3: weekStringArray = context.getResources().getStringArray(R.array.int_week_3);  break;
            case  4: weekStringArray = context.getResources().getStringArray(R.array.int_week_4);  break;
            case  5: weekStringArray = context.getResources().getStringArray(R.array.int_week_5);  break;
            case  6: weekStringArray = context.getResources().getStringArray(R.array.int_week_6);  break;
            case  7: weekStringArray = context.getResources().getStringArray(R.array.int_week_7);  break;
            case  8: weekStringArray = context.getResources().getStringArray(R.array.int_week_8);  break;
            case  9: weekStringArray = context.getResources().getStringArray(R.array.int_week_9);  break;
            case 10: weekStringArray = context.getResources().getStringArray(R.array.int_week_10); break;
            case 11: weekStringArray = context.getResources().getStringArray(R.array.int_week_11); break;
            case 12: weekStringArray = context.getResources().getStringArray(R.array.int_week_12); break;

            default:
                Toast.makeText(context, "Could Not Get Trainer Info", Toast.LENGTH_LONG).show();
        }

        content = weekStringArray[dayNumber];
        String[] subStrings = content.split("~");
        String workout = subStrings[1];
        System.out.println("Workout: " + workout);

        return workout;
    }


    public String getWorkoutImage(Context context, int weekNumber, int dayNumber) {
        String imageString;

        switch(weekNumber) {
            case  1: weekStringArray = context.getResources().getStringArray(R.array.int_week_1);  break;
            case  2: weekStringArray = context.getResources().getStringArray(R.array.int_week_2);  break;
            case  3: weekStringArray = context.getResources().getStringArray(R.array.int_week_3);  break;
            case  4: weekStringArray = context.getResources().getStringArray(R.array.int_week_4);  break;
            case  5: weekStringArray = context.getResources().getStringArray(R.array.int_week_5);  break;
            case  6: weekStringArray = context.getResources().getStringArray(R.array.int_week_6);  break;
            case  7: weekStringArray = context.getResources().getStringArray(R.array.int_week_7);  break;
            case  8: weekStringArray = context.getResources().getStringArray(R.array.int_week_8);  break;
            case  9: weekStringArray = context.getResources().getStringArray(R.array.int_week_9);  break;
            case 10: weekStringArray = context.getResources().getStringArray(R.array.int_week_10); break;
            case 11: weekStringArray = context.getResources().getStringArray(R.array.int_week_11); break;
            case 12: weekStringArray = context.getResources().getStringArray(R.array.int_week_12); break;

            default:
                Toast.makeText(context, "Could Not Get Trainer Info", Toast.LENGTH_LONG).show();
        }

        String image = weekStringArray[dayNumber];
        String[] subStrings = image.split("~");
        imageString = subStrings[0];
        System.out.println("Workout: " + imageString);

        return imageString;
    }
}
