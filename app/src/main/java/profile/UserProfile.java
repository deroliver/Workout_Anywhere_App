package profile;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.practice.derikpc.workoutanywhere.HomeScreen;
import com.practice.derikpc.workoutanywhere.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import blog.BlogPostActivity;
import databasetools.CompletedDatabaseTools;
import databasetools.FavoritesDatabaseTools;
import databasetools.UserInfoDatabaseTools;
import user.User;

public class UserProfile extends FragmentActivity {
    private String USER[] = null;

    private int year_x, month_x, day_x;
    private int DIALOG_ID = 0;

    private static String FNAME = "";
    private static String STATUS = "";
    private static String AVATARURL = "";

    private static Boolean first = true;

    private String recentFavoriteTitle;
    private String recentCompletedTitle;
    private String recentCommentedTitle;

    private String recentFavoriteURL;
    private String recentCommentedURL;
    private String recentCompletedURL;

    private String recentFavoriteImageURL;
    private String recentCommentedImageURL;
    private String recentCompletedImageURL;

    private ImageView favoritedImage;
    private ImageView completedImage;
    private ImageView commentedImage;
    private ImageView avatar;

    private ImageView likeFavoritedButton;
    private ImageView completedFavoritedButton;
    private ImageView likeCompletedButton;
    private ImageView completedCompletedButton;
    private ImageView likeCommentedButton;
    private ImageView completedCommentedButton;

    private Boolean favoriteFavorite = true;
    private Boolean completedFavorite = false;
    private Boolean favoriteCompleted = false;
    private Boolean completedCompleted = true;
    private Boolean favoriteCommented = false;
    private Boolean completedCommented = false;

    private ProgressBar favoritedBar;
    private ProgressBar completedBar;
    private ProgressBar commentedBar;

    private TextView welcome;
    private TextView favoritedTitle;
    private TextView completedTitle;
    private TextView commentedTitle;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    CompletedDatabaseTools cDBTools;
    FavoritesDatabaseTools fDBTools;
    UserInfoDatabaseTools uDBTools;

    private String asyncParams[];

    private Button viewAllFavoritedWorkoutVideos;
    private Button viewAllCompletedWorkoutVideos;
    private Button viewAllCommentedWorkoutVideos;

    private HashMap<String, String> recentFavorite;
    private HashMap<String, String> recentCompleted;
    private HashMap<String, String> recentCommented;

    private ArrayList<HashMap<String, String>> allFavorites;
    private ArrayList<HashMap<String, String>> allCompleted;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        cDBTools = new CompletedDatabaseTools(this);
        fDBTools = new FavoritesDatabaseTools(this);
        uDBTools = new UserInfoDatabaseTools(this);

        viewAllFavoritedWorkoutVideos = (Button) findViewById(R.id.view_all_favorited_videos_button);
        viewAllCompletedWorkoutVideos = (Button) findViewById(R.id.view_all_completed_videos_button);
        viewAllCommentedWorkoutVideos = (Button) findViewById(R.id.view_all_commented_videos_button);

        welcome = (TextView) findViewById(R.id.name_text_view);
        avatar = (ImageView) findViewById(R.id.user_picture_image_view);

        favoritedTitle = (TextView) findViewById(R.id.favorited_title);
        completedTitle = (TextView) findViewById(R.id.completed_title);
        commentedTitle = (TextView) findViewById(R.id.commented_title);

        favoritedImage = (ImageView) findViewById(R.id.favorited_image);
        completedImage = (ImageView) findViewById(R.id.completed_image);
        commentedImage = (ImageView) findViewById(R.id.commented_image);

        favoritedBar = (ProgressBar) findViewById(R.id.favorited_progress_bar);
        completedBar = (ProgressBar) findViewById(R.id.completed_progress_bar);
        commentedBar = (ProgressBar) findViewById(R.id.commented_progress_bar);

        likeFavoritedButton = (ImageView) findViewById(R.id.favorited_favorited_workout_video_button);
        completedFavoritedButton = (ImageView) findViewById(R.id.completed_favorited_workout_video_button);

        likeCompletedButton = (ImageView) findViewById(R.id.favorite_completed_workout_video_button);
        completedCompletedButton = (ImageView) findViewById(R.id.completed_completed_workout_video_button);

        likeCommentedButton = (ImageView) findViewById(R.id.favorited_commented_workout_video_button);
        completedCommentedButton = (ImageView) findViewById(R.id.completed_commented_workout_video_button);

        setOnClickListeners();

        new Async().execute();
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(UserProfile.this, R.style.MyTheme);
            progress.setCancelable(false);
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            options = getDisplayOptions();


            Calendar cal = Calendar.getInstance();
            year_x = cal.get(Calendar.YEAR);
            month_x = cal.get(Calendar.MONTH);
            day_x = cal.get(Calendar.DAY_OF_MONTH);

            allFavorites = fDBTools.getAllFavorites();
            allCompleted = cDBTools.getAllCompleted();

            recentFavorite = new HashMap<String, String>();
            recentCommented = new HashMap<String, String>();
            recentCompleted = new HashMap<String, String>();


            asyncParams = new String[2];

            System.out.println("Num Favs: " + fDBTools.getNumFavorites() + " Num Completed: " + cDBTools.getNumCompleted());

            for(int i = 0; i < allCompleted.size(); i++) {
                System.out.println(allCompleted.get(i).get("url"));
            }
            if(allFavorites.size() > 0)
                recentFavorite = allFavorites.get(allFavorites.size() -1);

            if(allCompleted.size() > 0)
                recentCompleted = allCompleted.get(allCompleted.size() -1);

            recentFavoriteURL = recentFavorite.get("url");
            recentCompletedURL = recentCompleted.get("url");

            System.out.println("Favorited URL: " + recentFavoriteURL);
            System.out.println("Completed URL: " + recentCompletedURL);

            asyncParams[0] = recentFavoriteURL;
            asyncParams[1] = recentCompletedURL;

            imageLoader = ImageLoader.getInstance();


            FNAME = User.getFirstName();
            AVATARURL = User.getUserAvatarUrl();
            STATUS = User.getUserStatus();

            System.out.println(FNAME + AVATARURL + STATUS + User.getUserID());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            welcome.setText("Welcome " + FNAME);

            char first = AVATARURL.charAt(0);
            if(first == '/') {
                AVATARURL = "http://" + AVATARURL.substring(2);
            }

            imageLoader.displayImage(AVATARURL, avatar);
            new PostInfo().execute(asyncParams);

            new checkFavorites().execute();
            new checkCompleted().execute();
        }
    }

    private class checkFavorites extends AsyncTask<String, Void, Boolean[]> {

        @Override
        protected Boolean[] doInBackground(String... params) {
            Boolean results[] = new Boolean[2];
            for (HashMap<String, String> fav : allFavorites) {
                if (fav.get("url").equals(recentCompletedURL)) {
                    results[0] = true;
                    favoriteCompleted = true;
                }

                if (fav.get("url").equals(recentCommentedURL)) {
                    results[1] = true;
                    favoriteCompleted = true;
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            if (result[0] != null) {
                if (result[0]) {
                    imageLoader.displayImage("drawable://" + R.drawable.like_button, likeCompletedButton);
                }
            }

            if (result[1] != null) {
                if (result[1]) {
                    imageLoader.displayImage("drawable://" + R.drawable.like_button, likeCompletedButton);
                }
            }
        }
    }


    private class checkCompleted extends AsyncTask<String, Void, Boolean[]> {

        @Override
        protected Boolean[] doInBackground(String... params) {
            Boolean results[] = new Boolean[2];
            for (HashMap<String, String> comp : allCompleted) {
                if (comp.get("url").equals(recentFavoriteURL)) {
                    results[0] = true;
                    completedFavorite = true;
                }

                if (comp.get("url").equals(recentCommentedURL)) {
                    results[1] = true;
                    completedCommented = true;
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            if (result[0] != null) {
                if (result[0]) {
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedFavoritedButton);
                }
            }

            if (result[1] != null) {
                if (result[1]) {
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedCommentedButton);
                }
            }
            progress.dismiss();
        }
    }


    private class PostInfo extends AsyncTask<String, Void, Boolean[]> {
        private Boolean successful[] = new Boolean[3];

        @Override
        protected Boolean[] doInBackground(String... urls) {
            successful[0] = false;
            successful[1] = false;
            successful[2] = false;
            try {
                for (int i = 0; i < urls.length; i++) {
                    if (urls[i] != null) {
                        successful[i] = true;
                        HttpGet httpPost = new HttpGet(urls[i]);
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpResponse response = httpClient.execute(httpPost);

                        int status = response.getStatusLine().getStatusCode();

                        if (status == 200) {
                            HttpEntity entity = response.getEntity();
                            String data = EntityUtils.toString(entity);

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject post = jsonObject.getJSONObject("post");


                            JSONObject thumbnail_images = post.getJSONObject("thumbnail_images");

                            JSONObject image = thumbnail_images.getJSONObject("medium");

                            if (i == 0) {
                                recentFavoriteTitle = post.getString("title_plain");
                                recentFavoriteImageURL = image.getString("url");
                                if (recentFavoriteTitle == null || recentFavoriteImageURL == null)
                                    successful[0] = false;
                            } else if (i == 1) {
                                recentCompletedTitle = post.getString("title_plain");
                                recentCompletedImageURL = image.getString("url");
                                if (recentCompletedTitle == null || recentCompletedImageURL == null)
                                    successful[1] = false;
                            } else if (i == 2) {
                                recentCommentedTitle = post.getString("title_plain");
                                recentCommentedImageURL = image.getString("url");
                                if (recentCommentedTitle == null || recentCommentedImageURL == null)
                                    successful[2] = false;
                            }
                        }
                    }
                }
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return successful;
        }

        @Override
        protected void onPostExecute(Boolean[] successful) {
            if (successful[0]) {
                favoritedImage.setClickable(true);
                viewAllFavoritedWorkoutVideos.setClickable(true);
                completedFavoritedButton.setClickable(true);
                likeFavoritedButton.setClickable(true);
                favoritedBar.setVisibility(View.VISIBLE);

                favoritedTitle.setText(recentFavoriteTitle);
                display(favoritedImage, recentFavoriteImageURL, favoritedBar);

            } else {
                completedFavoritedButton.setClickable(false);
                likeFavoritedButton.setClickable(false);

                favoritedImage.setImageDrawable(null);
                favoritedBar.setVisibility(View.INVISIBLE);
                favoritedTitle.setText("Could not Load");
                favoritedImage.setClickable(false);
                viewAllFavoritedWorkoutVideos.setClickable(false);
            }


            if (successful[1]) {
                completedImage.setClickable(true);
                viewAllCompletedWorkoutVideos.setClickable(true);
                completedCompletedButton.setClickable(true);
                likeCompletedButton.setClickable(false);
                completedBar.setVisibility(View.VISIBLE);

                completedTitle.setText(recentCompletedTitle);
                display(completedImage, recentCompletedImageURL, completedBar);

            } else {
                completedCompletedButton.setClickable(false);
                likeCompletedButton.setClickable(false);

                completedImage.setImageDrawable(null);
                completedBar.setVisibility(View.INVISIBLE);
                completedTitle.setText("Could not Load");
                completedImage.setClickable(false);
                viewAllCompletedWorkoutVideos.setClickable(false);
            }


            if (successful[2]) {
                commentedImage.setClickable(true);
                viewAllCommentedWorkoutVideos.setClickable(true);
                completedCommentedButton.setClickable(true);
                likeCommentedButton.setClickable(true);
                commentedBar.setVisibility(View.VISIBLE);

                commentedTitle.setText(recentCommentedTitle);
                display(commentedImage, recentCommentedImageURL, commentedBar);

            } else {
                completedCommentedButton.setClickable(false);
                likeCommentedButton.setClickable(false);

                commentedImage.setImageDrawable(null);
                commentedBar.setVisibility(View.INVISIBLE);
                commentedTitle.setText("Could not Load");
                commentedImage.setClickable(false);
                viewAllCommentedWorkoutVideos.setClickable(false);
            }

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            Toast.makeText(getApplicationContext(), day_x + "/" + month_x + "/" + year, Toast.LENGTH_LONG).show();
        }
    };


    public void display(ImageView img, String url, final ProgressBar spinner) {

        imageLoader.displayImage(url, img, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public DisplayImageOptions getDisplayOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.loading_error)
                .showImageOnFail(R.drawable.loading_error)
                .delayBeforeLoading(100)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        return options;
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }


    public void setOnClickListeners() {
        likeFavoritedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteFavorite) {
                    favoriteFavorite = false;
                    imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, likeFavoritedButton);
                    fDBTools.deleteFavoriteByURL(recentFavoriteURL);
                } else if (!favoriteFavorite) {
                    favoriteFavorite = true;
                    imageLoader.displayImage("drawable://" + R.drawable.like_button, likeFavoritedButton);
                    fDBTools.insertFavorite(recentFavoriteURL, "Favorite");
                }
            }
        });

        completedFavoritedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completedFavorite) {
                    completedFavorite = false;
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completedFavoritedButton);
                    cDBTools.deleteCompletionByURL(recentFavoriteURL);
                } else if (!completedFavorite) {
                    showDialog(DIALOG_ID);
                    completedFavorite = true;
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedFavoritedButton);
                    cDBTools.insertCompletion(recentFavoriteURL, day_x + ":" + month_x + ":" + year_x);
                }
            }
        });

        likeCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteCompleted) {
                    favoriteCompleted = false;
                    imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, likeCompletedButton);
                    fDBTools.deleteFavoriteByURL(recentCompletedURL);
                } else if (!favoriteCompleted) {
                    favoriteCompleted = true;
                    imageLoader.displayImage("drawable://" + R.drawable.like_button, likeCompletedButton);
                    fDBTools.insertFavorite(recentCompletedURL, "Favorite");
                }
            }
        });

        completedCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completedCompleted) {
                    completedCompleted = false;
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completedCompletedButton);
                    cDBTools.deleteCompletionByURL(recentCompletedURL);
                } else if (!completedCompleted) {
                    showDialog(DIALOG_ID);
                    completedCompleted = true;
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedCompletedButton);
                    cDBTools.insertCompletion(recentCompletedURL, day_x + ":" + month_x + ":" + year_x);
                }
            }
        });


        likeCommentedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteCommented) {
                    favoriteCommented = false;
                    imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, likeCommentedButton);
                    fDBTools.deleteFavoriteByURL(recentCommentedURL);
                } else if (!favoriteCommented) {
                    favoriteCommented = true;
                    imageLoader.displayImage("drawable://" + R.drawable.like_button, likeCommentedButton);
                    fDBTools.insertFavorite(recentCommentedURL, "Favorite");
                }
            }
        });

        completedCommentedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completedCommented) {
                    completedCommented = false;
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completedCommentedButton);
                    cDBTools.deleteCompletionByURL(recentCommentedURL);
                } else if (!completedCommented) {
                    showDialog(DIALOG_ID);
                    completedCommented = true;
                    imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedCommentedButton);
                    cDBTools.insertCompletion(recentCommentedURL, day_x + ":" + month_x + ":" + year_x);
                }
            }
        });


        favoritedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlogPostActivity.class);
                intent.putExtra("URL", recentFavoriteURL);
                intent.putExtra("Position", allFavorites.size() - 1);
                intent.putExtra("Liked", favoriteFavorite);
                intent.putExtra("Completed", completedFavorite);
                startActivityForResult(intent, 1);
            }
        });

        completedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlogPostActivity.class);
                intent.putExtra("URL", recentCompletedURL);
                intent.putExtra("Position", allCompleted.size() - 1);
                intent.putExtra("Liked", favoriteCompleted);
                intent.putExtra("Completed", completedCompleted);
                startActivityForResult(intent, 2);
            }
        });

        commentedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BlogPostActivity.class);
                intent.putExtra("URL", recentCommentedURL);
                //intent.putExtra("Position", allCompleted.size() - 1);
                intent.putExtra("Liked", favoriteCommented);
                intent.putExtra("Completed", completedCommented);
                startActivityForResult(intent, 3);
            }
        });


        viewAllFavoritedWorkoutVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileWorkoutsList.class);
                intent.putExtra("Type", "Favorites");

                startActivity(intent);
            }
        });

        viewAllCompletedWorkoutVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileWorkoutsList.class);
                intent.putExtra("Type", "Completed");

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            finish();
        }

        System.out.println(requestCode);
        final Intent intent = data;
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Boolean completed = intent.getBooleanExtra("Completed", false);
                        Boolean liked = intent.getBooleanExtra("Liked", false);
                        String date = intent.getStringExtra("Date");

                        if (completed && !completedFavorite) {
                            completedFavorite = true;
                            cDBTools.insertCompletion(recentFavoriteURL, date);
                        } else if (!completed && completedFavorite) {
                            completedFavorite = false;
                            cDBTools.deleteCompletionByURL(recentFavoriteURL);
                        }

                        if (liked && !favoriteFavorite) {
                            favoriteFavorite = true;
                            fDBTools.insertFavorite(recentFavoriteURL, "Blog");
                        } else if (!liked && favoriteFavorite) {
                            favoriteFavorite = false;
                            fDBTools.deleteFavoriteByURL(recentFavoriteURL);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(favoriteFavorite)
                            imageLoader.displayImage("drawable://" + R.drawable.like_button, likeFavoritedButton);
                        else
                            imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, likeFavoritedButton);

                        if(completedFavorite)
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedFavoritedButton);
                        else
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completedFavoritedButton);

                    }
                }.execute();

            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Boolean completed = intent.getBooleanExtra("Completed", false);
                        Boolean liked = intent.getBooleanExtra("Liked", false);
                        String date = intent.getStringExtra("Date");

                        if (completed && !completedCompleted) {
                            completedCompleted = true;
                            cDBTools.insertCompletion(recentCompletedURL, date);
                        } else if (!completed && completedCompleted) {
                            completedCompleted = false;
                            cDBTools.deleteCompletionByURL(recentCompletedURL);
                        }

                        if (liked && !favoriteCompleted) {
                            favoriteCompleted = true;
                            fDBTools.insertFavorite(recentCompletedURL, "Blog");
                        } else if (!liked && favoriteCompleted) {
                            favoriteCompleted = false;
                            fDBTools.deleteFavoriteByURL(recentCompletedURL);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(favoriteCompleted)
                            imageLoader.displayImage("drawable://" + R.drawable.like_button, likeCompletedButton);
                        else
                            imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, likeCompletedButton);

                        if(completedCompleted)
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedCompletedButton);
                        else
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completedCompletedButton);
                    }
                }.execute();

            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Boolean completed = intent.getBooleanExtra("Completed", false);
                        Boolean liked = intent.getBooleanExtra("Liked", false);
                        String date = intent.getStringExtra("Date");

                        if (completed && !completedCommented) {
                            completedCommented = true;
                            cDBTools.insertCompletion(recentCommentedURL, date);
                        } else if (!completed && completedCommented) {
                            completedCommented = false;
                            cDBTools.deleteCompletionByURL(recentCommentedURL);
                        }

                        if (liked && !favoriteCommented) {
                            favoriteCommented = true;
                            fDBTools.insertFavorite(recentCommentedURL, "Blog");
                        } else if (!liked && favoriteCommented) {
                            favoriteCommented = false;
                            fDBTools.deleteFavoriteByURL(recentCommentedURL);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(favoriteCommented)
                            imageLoader.displayImage("drawable://" + R.drawable.like_button, likeCommentedButton);
                        else
                            imageLoader.displayImage("drawable://" + R.drawable.like_before_pressed_button, likeCommentedButton);

                        if(completedCommented)
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_full, completedCommentedButton);
                        else
                            imageLoader.displayImage("drawable://" + R.drawable.completed_check_mark_button_empty, completedCommentedButton);
                    }
                }.execute();

            }
        }
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
