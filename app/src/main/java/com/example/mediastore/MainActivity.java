package com.example.mediastore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static int SETTINGS_REQUEST = 5843;

    private ImageView background;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        background = findViewById(R.id.background);
        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri photoUri = SettingsActivity.readResult(data);
            Objects.requireNonNull(photoUri);
            background.setImageBitmap(readImage(photoUri));
        }
    }

    @Nullable
    private Bitmap readImage(@NonNull Uri photoUri) {
        try (InputStream stream = getContentResolver().openInputStream(photoUri)) {
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            return null;
        }
    }
}
