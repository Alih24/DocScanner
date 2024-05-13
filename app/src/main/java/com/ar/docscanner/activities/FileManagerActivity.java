package com.ar.docscanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ar.docscanner.R;
import com.ar.docscanner.process.adapter.FilesAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ar.docscanner.PresenterScanner.FOLDER_PATH;

public class FileManagerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<String> inFiles;
    FilesAdapter adapter;
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
        setContentView(R.layout.activity_file_manager);

        ButterKnife.bind(this);


        folder = new File(FOLDER_PATH, "ExtractText");
        if (!folder.exists()) {
            folder.mkdir();
        }

        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        inFiles = new ArrayList<>();
        files = folder.listFiles();
        adapter = new FilesAdapter(this, inFiles);
        listView.setAdapter(adapter);
        swipeView.setOnRefreshListener(this);

        populateListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        adapter = new FilesAdapter(this, inFiles);
        listView.setAdapter(adapter);
    }

    private void addFile(File file0) {
        File[] files = file0.listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(".txt")) {
                inFiles.add(file.getPath());
                Log.v("adding", file.getName());
            } else if (file.isDirectory()) {
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