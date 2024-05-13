package com.ar.docscanner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ar.docscanner.R;
import com.ar.docscanner.process.adapter.LockerFIleAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ar.docscanner.PresenterScanner.FOLDER_PATH;

public class FolderLockerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    TextInputLayout Passwordtxt;
    ArrayList<String> inFiles;
    LockerFIleAdapter adapter;
    File[] files;
    File folder;

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeView;

    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_locker);
        setSupportActionBar(findViewById(R.id.toolbar));

        ButterKnife.bind(this);

        folder = new File(FOLDER_PATH, ".Locker");
        if (!folder.exists()) {
            folder.mkdir();
        }

        inFiles = new ArrayList<>();
        files = folder.listFiles();
        adapter = new LockerFIleAdapter(this, inFiles);
        listView.setAdapter(adapter);
        swipeView.setOnRefreshListener(this);

        populateListView();
    }

    public void GoBack(View view) {
        onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forgot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.forgotpassword:
                forgotPassMenu();
                break;
            default:
                Toast.makeText(this, "Something wents wrong...", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forgotPassMenu() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popup = layoutInflater.inflate(R.layout.pop_password, null);


        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(
                popup,
                width,
                height,
                focusable
        );

        popupWindow.showAtLocation(findViewById(R.id.RelativeLayout), Gravity.CENTER, 0, 0);
        findViewById(R.id.RelativeLayout).setAlpha((float) 0.10);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.RelativeLayout).setAlpha((float) 1);
            }
        });

        Button btnPassword = popup.findViewById(R.id.unlock);
        Passwordtxt = popup.findViewById(R.id.Passwordtxt);

        Passwordtxt.setHint("Create new password");
        btnPassword.setText("Update password");

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                if (!validatePassword()) {
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("Locker",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("Pass",Integer.parseInt(Passwordtxt.getEditText().getText().toString()));

                if (editor.commit()) {
                    popupWindow.dismiss();
                    startActivity(new Intent(view.getContext(), DashboardActivity.class));
                    Snackbar.make(view,"Password has been changed", Snackbar.LENGTH_LONG);
                    finish();
                } else {
                    Passwordtxt.setError("Wrong password!");
                }

            }
        });
    }

    private boolean validatePassword() {
        String val = Passwordtxt.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=\\S+$)" +
                ".{6,}" +
                "$";

        if (val.isEmpty()) {
            Passwordtxt.setError("Password is required!");
            return false;
        } else {
            Passwordtxt.setError(null);
            Passwordtxt.setErrorEnabled(false);
            return true;
        }
    }

    public void populateListView() {
        inFiles = new ArrayList<>();
        files = folder.listFiles();
        if (files == null)
            Toast.makeText(this, "No files here...", Toast.LENGTH_LONG).show();
        else {
            addFile(folder);

        }
        Log.v("done", "adding");
        adapter = new LockerFIleAdapter(this, inFiles);
        listView.setAdapter(adapter);
    }

    private void addFile(File file0) {
        File[] files = file0.listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(".txt")) {
                inFiles.add(file.getPath());
                Log.v("adding", file.getName());
            } else if (!file.isDirectory() && file.getName().endsWith(".pdf")) {
                inFiles.add(file.getPath());
                Log.v("adding", file.getName());
            }else if (file.isDirectory()) {
                addFile(file);
            }
        }

    }

    @Override
    public void onRefresh() {
        Log.v("refresh", "refreshing dta");
        populateListView();
        swipeView.setRefreshing(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}