package workouts;

import android.graphics.drawable.Drawable;


public class FreestyleWallObject {

    private String workoutName = "";
    private String workoutType = "";
    private String imageURL = "";
    private String url = "";
    private boolean liked = false;
    private boolean completed = false;
    private Integer SqliteID = -1;

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

    public String getWorkoutName() {
        return workoutName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Integer getSqliteID() {
        return SqliteID;
    }

    public void setSqliteID(Integer sqliteID) {
        SqliteID = sqliteID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
