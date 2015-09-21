package stream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.practice.derikpc.workoutanywhere.HomeScreen;
import com.practice.derikpc.workoutanywhere.R;

import java.util.ArrayList;

import databasetools.UserInfoDatabaseTools;
import user.User;

public class StreamActivity extends FragmentActivity implements CommentListener {

    private ArrayList<UserComment> commentArray;
    private UserInfoDatabaseTools uDBTools;

    @Override
    public ArrayList<UserComment> getCommentArray() {
        return commentArray;
    }

    @Override
    public void setCommentArray(ArrayList<UserComment> comments) {
        this.commentArray = comments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);

        uDBTools = new UserInfoDatabaseTools(this);

        commentArray = new ArrayList<UserComment>();

        Fragment fragment = new Stream();

        FragmentManager fM = getSupportFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        fT.replace(R.id.stream_activity, fragment);
        fT.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.sign_out: {
                signOut();
                return true;
            }

            case R.id.exit_the_app: {
                System.exit(0);
                return true;
            }

            case R.id.home_screen: {
                homeScreen();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        String username = User.getUserName();
        uDBTools.updateSignedInByUsername(username, "false");

        Intent intent = new Intent(this, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void homeScreen() {
        finish();
    }
}
