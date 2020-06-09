package com.example.mediastore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_CODE = 4454;
    private static final String URI_RESULT = "URI_RESULT";

    private EditText fileNameEditText;

    @Nullable
    static Uri readResult(@NonNull Intent data) {
        return data.getParcelableExtra(URI_RESULT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    private void initViews() {
        fileNameEditText = findViewById(R.id.fileNameEditText);

        findViewById(R.id.chooseFileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted()) {
                    readUri();
                } else {
                    requestPermission();
                }
            }
        });
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_CODE
        );
    }

    private void readUri() {
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{
                fileNameEditText.getText().toString()
        };

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor == null || !cursor.moveToNext()) {
                Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
                return;
            }

            String id = cursor.getString(0);

            Uri photoUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
            );

            Intent data = new Intent().putExtra(URI_RESULT, photoUri);

            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != READ_EXTERNAL_STORAGE_CODE) {
            return;
        }

        int result = grantResults[0];
        switch (result) {
            case PackageManager.PERMISSION_GRANTED: {
                readUri();
                break;
            }
            case PackageManager.PERMISSION_DENIED: {
                showPermissionDescription();
                break;
            }
            default:
        }
    }

    private void showPermissionDescription() {
        if (isRequestPermissionRationale()) {
            Toast.makeText(this, R.string.permission_denied_description, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.permission_never_ask_again_description, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
    }
}
