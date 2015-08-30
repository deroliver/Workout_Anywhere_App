package calendar;

import java.util.Date;

public interface SeekChangeListener {
    public int[] getDayWeekNumber();
    public void setDayWeekNumber(int[] date);
    void onPageSelected();
    public Date getDateSelected();
    public void setDateSelected(Date date);
    public void setTrainerType(String trainerType);
    public String getTrainerType();
}
