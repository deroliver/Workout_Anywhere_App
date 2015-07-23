package calendar;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.practice.derikpc.workoutanywhere.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;


public class CalendarViewFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        CalendarPickerView calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        Date janFirst = new Date(115, 0, 1);
        Date today = new Date();
        calendar.init(janFirst, nextYear.getTime()).withSelectedDate(today);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {

            @Override
            public void onDateSelected(Date date) {
                int year = date.getYear();
                year = year + 1900;
                int month = date.getMonth();
                month = month + 1;
                Toast.makeText(getActivity(), "" + month + "/" + date.getDate() + "/" + year, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        return view;
    }
}
