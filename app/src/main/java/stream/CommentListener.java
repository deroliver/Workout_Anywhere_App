package stream;

import java.util.ArrayList;


public interface CommentListener {
    public void setCommentArray(ArrayList<UserComment> comments);
    public ArrayList<UserComment> getCommentArray();
}
