package com.ar.docscanner.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.docscanner.R;
import com.ar.docscanner.document.DocumentActivity;
import com.ar.docscanner.listdoc.DocsAdapter;
import com.ar.docscanner.listdoc.DocsContract;
import com.ar.docscanner.listdoc.DocsPresenter;
import com.ar.docscanner.util.Const;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentManager extends AppCompatActivity implements DocsContract.IDocsView {

    @BindView(R.id.rcView)
    RecyclerView rcView;



    private static final int REQUEST_STORAGE = 212;
    private DocsContract.IDocsPresenter presenter;
    private DocsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_manager);

        if (ActivityCompat.checkSelfPermission(DocumentManager.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initView();
            initData();
        } else {
            ActivityCompat.requestPermissions(DocumentManager.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    private void initView() {
        hooks();
    }

    private void hooks() {
        ButterKnife.bind(this);
        presenter = new DocsPresenter(this, this);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DocsAdapter(this, presenter);
        rcView.setAdapter(adapter);
    }

    private void initData() {
        adapter.loadData(presenter.getListDocs(Const.FOLDER_DOC));
    }

    @Override
    public void onItemClick(File file) {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.putExtra("folder", file.getAbsolutePath());
        intent.putExtra("type", 0);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final File folder) {
        if (folder.exists()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("Delete this document folder?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (File file : folder.listFiles())
                                file.delete();
                            folder.delete();
                            Toast.makeText(DocumentManager.this, "Deleted", Toast.LENGTH_LONG).show();
                            presenter = new DocsPresenter(DocumentManager.this, DocumentManager.this);
                            adapter = new DocsAdapter(DocumentManager.this, presenter);
                            rcView.setAdapter(adapter);
                            startActivity(new Intent(DocumentManager.this, DocumentManager.class));
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    public void Back(View view) {
        onBackPressed();
    }
}