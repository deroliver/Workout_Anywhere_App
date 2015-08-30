package blog;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.ImageView;

public class BlogPost {

    private String title = "";
    private String type = "";
    private String imageURL = "";
    private String id = "";
    private String content = "";
    private String slug = "";
    private String url = "";
    private Bitmap image;
    private boolean liked = false;
    private boolean completed = false;
    private Integer sqliteID = -1;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Integer getSqliteID() {
        return sqliteID;
    }

    public void setSqliteID(Integer sqliteID) {
        this.sqliteID = sqliteID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
