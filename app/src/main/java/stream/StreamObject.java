package stream;


import android.graphics.drawable.Drawable;

public class StreamObject {

    private String usersName = "";
    private String activityAction = "";
    private String activityContent = "";
    private Integer nLikes = 0;
    private Integer nComments = 0;
    private String userImageURL = "";
    private String activityID = "";
    private String activityUserID = "";
    private String dateTime = "";


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getActivityContent() {
        return activityContent;
    }

    public void setActivityContent(String activityContent) {
        this.activityContent = activityContent;
    }

    public String getActivityAction() {
        return activityAction;
    }

    public void setActivityAction(String activityAction) {
        this.activityAction = activityAction;
    }

    public String getActivityUserID() {
        return activityUserID;
    }

    public void setActivityUserID(String activityUserID) {
        this.activityUserID = activityUserID;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getName() {
        return this.usersName;
    }

    public Integer getnLikes() {
        return this.nLikes;
    }

    public Integer getnComments() {
        return this.nComments;
    }

    public void setName(String name) {
        this.usersName = name;
    }

    public void setnComments(Integer nComments) {
        this.nComments = nComments;
    }

    public void setnLikes(Integer nLikes) {
        this.nLikes = nLikes;
    }

    public String getImageURL() {
        return userImageURL;
    }

    public void setImageURL(String imageURL) {
        this.userImageURL = imageURL;
    }
}
