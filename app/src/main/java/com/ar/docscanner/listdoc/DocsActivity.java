package com.ar.docscanner.listdoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.docscanner.R;
import com.ar.docscanner.document.DocumentActivity;
import com.ar.docscanner.listdoc.base.BaseActivity;
import com.ar.docscanner.util.Const;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocsActivity extends BaseActivity implements DocsContract.IDocsView {
    @BindView(R.id.rcView)
    RecyclerView rcView;
    private LinearLayout llAds;

    private DocsContract.IDocsPresenter presenter;
    private DocsAdapter adapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_docs;
    }

    @Override
    protected void initData() {
        adapter.loadData(presenter.getListDocs(Const.FOLDER_DOC));
    }

    @Override
    protected void initView() {
        llAds = findViewById(R.id.ll_ads);
        ButterKnife.bind(this);
        presenter = new DocsPresenter(this, this);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DocsAdapter(this, presenter);
        rcView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(File file) {
        Intent intent = new Intent(DocsActivity.this, DocumentActivity.class);
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
                            Toast.makeText(DocsActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                            presenter = new DocsPresenter(DocsActivity.this, DocsActivity.this);
                            adapter = new DocsAdapter(DocsActivity.this, presenter);
                            rcView.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
