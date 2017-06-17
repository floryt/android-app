package com.floryt.app;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by Steven on 6/17/2017.
 */

public class PhotoViewActivity extends AppCompatActivity {
    final String TAG = "PhotoViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        final Uri uri = getIntent().getParcelableExtra("uri");
        final PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        final Bitmap[] theBitmap = new Bitmap[1];
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Looper.prepare();
                try {
                    theBitmap[0] = Glide.
                            with(PhotoViewActivity.this).
                            load(uri).
                            asBitmap().
                            into(-1, -1).
                            get();
                } catch (final Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void dummy) {
                if (null != theBitmap[0]) {
                    // The full bitmap should be available here
                    photoView.setImageBitmap(theBitmap[0]);
                    Log.d(TAG, "Image loaded");
                }
            }
        }.execute();
    }
}
