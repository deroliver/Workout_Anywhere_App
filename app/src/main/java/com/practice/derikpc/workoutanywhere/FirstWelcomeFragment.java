package com.practice.derikpc.workoutanywhere;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;


import java.io.File;

public class FirstWelcomeFragment extends Fragment {

    private ImageView Wallpaper;
    private View view;
    ImageLoader imageLoader;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.first_welcome_sign_in_fragment, container, false);

        imageLoader = ImageLoader.getInstance();

        Wallpaper = (ImageView) view.findViewById(R.id.workout_anywhere_picture_first);

        imageLoader.displayImage("drawable://" + R.drawable.wallpaper_collage, Wallpaper);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        imageLoader.displayImage("drawable://" + R.drawable.wallpaper_collage, Wallpaper);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
        Wallpaper.setImageBitmap(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
