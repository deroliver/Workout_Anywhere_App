package trainer;


import android.app.ProgressDialog;

public interface TrainerTypeListener {
    public void setTrainerType(String trainerType);
    public String getTrainerType();
    public void setDayWeek(int[] dayWeek);
    public int[] getDayWeek();
    public ProgressDialog getProgressBar();
    public void setProgressBar(ProgressDialog progress);
}
