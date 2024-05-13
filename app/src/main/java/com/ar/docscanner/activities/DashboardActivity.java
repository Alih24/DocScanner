package com.ar.docscanner.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ar.docscanner.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;

import static com.ar.docscanner.PresenterScanner.FOLDER_NAME;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final float END_SCALE = 0.7f;
    private static final int REQUEST_STORAGE = 212;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private String pathCamera = null;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView Menu_Icon;
    CoordinatorLayout content;
    TextInputLayout Passwordtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView();
        } else {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initView();
            } else {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callCamera();
            } else {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        }

    }

    private void callCamera() {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String folderName = mSharedPref.getString("storage_folder", FOLDER_NAME);
        File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        pathCamera = folder.getAbsolutePath() + "/.TEMP_CAMERA.xxx";
        getSharedPreferences("BVH", MODE_PRIVATE).edit().putString("path", pathCamera).commit();
        getSharedPreferences("BVH", MODE_PRIVATE).edit().putInt("type", 1).commit();
        File f = new File(pathCamera);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri outputFileUri;
        if (Build.VERSION.SDK_INT < 24)
            outputFileUri = Uri.fromFile(f);
        else {
            outputFileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", f);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    private void initView() {
        hooks();

        ClickListeners();

        navigationDrawer();
    }

    private void ClickListeners() {
        
        Menu_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_pdf:
                startActivity(new Intent(this, MyPDFActivity.class));
                break;
            case R.id.nav_text:
                startActivity(new Intent(this, ExtractText.class));
                break;
            case R.id.nav_doc:
                startActivity(new Intent(this, DocumentManager.class));
                break;
            case R.id.nav_dona:
                startActivity(new Intent(this, DonationsActivity.class));
                break;
        }

        return false;
    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        animateDrawer();
    }

    private void animateDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.nav_animation));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;

                content.setScaleX(offsetScale);
                content.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = content.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;

                content.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void hooks() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Menu_Icon = findViewById(R.id.menu_icon);
        content = findViewById(R.id.content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int i = 0;
        i++;
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            pathCamera = getSharedPreferences("BVH", MODE_PRIVATE).getString("path", pathCamera);
            File f = new File(pathCamera);
            if (f.exists()) {
                SimpleDocumentScannerActivity.startScanner(DashboardActivity.this, pathCamera, "");
            } else {
                Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void FromCamera(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            callCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    public void FolderLocker(View view) {

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popup = layoutInflater.inflate(R.layout.pop_password,null);


        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(
                popup,
                width,
                height,
                focusable
        );

        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        findViewById(R.id.drawer_layout).setAlpha((float) 0.10);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.drawer_layout).setAlpha((float)1);
            }
        });

        Button btnPassword = popup.findViewById(R.id.unlock);
        Passwordtxt = popup.findViewById(R.id.Passwordtxt);

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                if(!validatePassword()){
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("Locker",MODE_PRIVATE);
                int pass = preferences.getInt("Pass",12345);

                if(pass == 12345){
                    if(Integer.parseInt(Passwordtxt.getEditText().getText().toString())==12345)
                    {
                        popupWindow.dismiss();
                        Intent intent = new Intent(DashboardActivity.this, FolderLockerActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Passwordtxt.setError("Wrong password!");
                    }
                }else {
                    if(Integer.parseInt(Passwordtxt.getEditText().getText().toString())==pass)
                    {
                        popupWindow.dismiss();
                        Intent intent = new Intent(DashboardActivity.this, FolderLockerActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Passwordtxt.setError("Wrong password!");
                    }
                }

            }
        });

    }

    private boolean validatePassword() {
        String val = Passwordtxt.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            Passwordtxt.setError("Password is required!");
            return false;
        } else {
            Passwordtxt.setError(null);
            Passwordtxt.setErrorEnabled(false);
            return true;
        }
    }

    public void FromGallary(View view) {
        getSharedPreferences("BVH", MODE_PRIVATE).edit().putInt("type", 1).commit();
        SimpleDocumentScannerActivity.startScanner(this, "", "");
    }

    public void TextExtraction(View view) {
        startActivity(new Intent(this, ExtractText.class));
    }

    public void FileManager(View view) {
        startActivity(new Intent(this, FileManagerActivity.class));
    }

    public void DocScanner(View view) {
        startActivity(new Intent(this, DocumentManager.class));
    }

    public void PDFManager(View view) {
        startActivity(new Intent(this, MyPDFActivity.class));
    }
}