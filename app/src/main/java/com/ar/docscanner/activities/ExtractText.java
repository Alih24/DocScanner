package com.ar.docscanner.activities;

import static com.ar.docscanner.PresenterScanner.FOLDER_PATH;
import static com.guna.ocrlibrary.OcrCaptureActivity.TextBlockObject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ar.docscanner.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.guna.ocrlibrary.OCRCapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ExtractText extends AppCompatActivity {

    private final int CAMERA_SCAN_TEXT = 0;
    private final int LOAD_IMAGE_RESULTS = 1;

    ImageView imgBack;
    private String folder;
    TextView TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_text);

        hooks();

        OCRCapture.Builder(this)
                .setUseFlash(false)
                .setAutoFocus(true)
                .buildWithRequestCode(CAMERA_SCAN_TEXT);
    }

    private void hooks() {
        folder = "ExtractText";

        TextView = findViewById(R.id.TextView);
        imgBack = findViewById(R.id.imgBack);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CAMERA_SCAN_TEXT) {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    TextView.setText(data.getStringExtra(TextBlockObject));
                }
            } else if (requestCode == LOAD_IMAGE_RESULTS) {
                Uri pickedImage = data.getData();
                String text = OCRCapture.Builder(this).getTextFromUri(pickedImage);
                TextView.setText(text);
            }
        }
    }

    public void SaveToFile(View view) {
        String filename = new Date().toString() + ".txt";

        try {
            File root = new File(FOLDER_PATH, folder);
            if (!root.exists()) {
                root.mkdirs();
            }
            if (TextView.getText().toString() == "") {
                Toast.makeText(this, "No text has been extract....", Toast.LENGTH_SHORT).show();
                return;
            }
            File gpxfile = new File(root, filename);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(TextView.getText().toString());
            writer.flush();
            writer.close();
            Toast.makeText(this, "File has been saved", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CopyToClipBoard(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied", TextView.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Text copied!", Toast.LENGTH_SHORT).show();
    }

    public void GoToBack(View view) {
        onBackPressed();
    }
}