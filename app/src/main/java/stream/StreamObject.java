package stream;


import android.graphics.drawable.Drawable;

public class StreamObject {

    private String name = "Derik Oliver";
    private String update = "I completed a challenge";
    private Integer nLikes = 10;
    private Integer nComments = 11;
    private Drawable picture;


    public Drawable getPicture() {
        return this.picture;
    }

    public String getName() {
        return this.name;
    }

    public String getUpdate() {
        return this.update;
    }

    public Integer getnLikes() {
        return this.nLikes;
    }

    public Integer getnComments() {
        return this.nComments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setnComments(Integer nComments) {
        this.nComments = nComments;
    }

    public void setnLikes(Integer nLikes) {
        this.nLikes = nLikes;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

}
