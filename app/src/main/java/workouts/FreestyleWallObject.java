package workouts;

import android.graphics.drawable.Drawable;


public class FreestyleWallObject {

    private String workoutName = "";
    private String workoutType = "";
    private Drawable workoutPicture;
    private String imageURL = "";
    private String url = "";

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public void setWorkoutPicture(Drawable workoutPicture) {
        this.workoutPicture = workoutPicture;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public Drawable getWorkoutPicture() {
        return workoutPicture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
