package com.ar.docscanner.process.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ar.docscanner.R;
import com.ar.docscanner.activities.FolderLockerActivity;
import com.balysv.materialripple.MaterialRippleLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LockerFIleAdapter extends BaseAdapter {
    private FolderLockerActivity mContext;
    private static LayoutInflater inflater;
    private ArrayList<String> mFeedItems;
    private String mFileName;

    public LockerFIleAdapter(FolderLockerActivity context, ArrayList<String> FeedItems) {
        this.mContext = context;
        this.mFeedItems = FeedItems;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    static class viewHolder {

        @BindView(R.id.name)
        TextView textView;
        @BindView(R.id.date)
        TextView tvDate;
        @BindView(R.id.parent)
        LinearLayout linearLayout;
        @BindView(R.id.ripple)
        MaterialRippleLayout mRipple;

        public viewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return mFeedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mFeedItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LockerFIleAdapter.viewHolder holder;
        if (view != null) {
            holder = (LockerFIleAdapter.viewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.lock_list_item, parent, false);
            holder = new LockerFIleAdapter.viewHolder(view);
            view.setTag(holder);
        }

        String[] name = mFeedItems.get(position).split("/");
        holder.textView.setText(name[name.length - 1]);

        holder.tvDate.setText(getDate(position));

        holder.mRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mContext)
                        .title("File actions")
                        .items(R.array.Lockitems)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                switch (which) {
                                    case 0: //Open
                                        openFile(mFeedItems.get(position));
                                        break;

                                    case 1: //delete
                                        deleteFile(mFeedItems.get(position));
                                        break;

                                    case 2: //rename
                                        renameFile(position);
                                        break;

                                    case 3: //Print
                                        doPrint(mFeedItems.get(position));
                                        break;

                                    case 4: //Share
                                        shareFile(mFeedItems.get(position));
                                        break;
                                }
                            }
                        })
                        .show();
                notifyDataSetChanged();
            }
        });

        return view;
    }

//    private void moveFile(String name) {
//        String FolderLocker = "Locker";
//
//        File root=  new File(FOLDER_PATH,FolderLocker);
//
//        if(!root.exists())
//            root.mkdir();
//
//        File fmove = new File(name);
//
//        if(!fmove.exists()) {
//            Toast.makeText(mContext, "File not exists in this location...", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        boolean done = CopyAssets.with(mContext)
//                .from(fmove.getPath())
//                .to(root.getPath())
//                .copy();
//
//        if(done)
//            deleteFile(name);
//        Toast.makeText(mContext, "File has been locked!", Toast.LENGTH_SHORT).show();
//    }

    private String getDate(int position) {
        File file = new File(mFeedItems.get(position));
        long TimeinMilliSeccond = file.lastModified();
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date(TimeinMilliSeccond));
        return dateString;
    }

    private void openFile(String name) {
        File file = new File(name);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT < 24)
            uri = Uri.fromFile(file);
        else {
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
        }
        target.setDataAndType(uri, "application/pdf");
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No app to read File File", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFile(String name) {
        File fdelete = new File(name);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(mContext, "File deleted.", Toast.LENGTH_LONG).show();
                mContext.onRefresh();
            } else {
                Toast.makeText(mContext, "File can't be deleted.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void renameFile(final int position) {
        new MaterialDialog.Builder(mContext)
                .title("Creating File")
                .content("Enter file name")
                .input("Example : abc", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input == null) {
                            Toast.makeText(mContext, "Name cannot be blank", Toast.LENGTH_LONG).show();

                        } else {
                            String newname = input.toString();
                            File oldfile = new File(mFeedItems.get(position));
                            String x[] = mFeedItems.get(position).split("/");
                            String newfilename = "";
                            for (int i = 0; i < x.length - 1; i++)
                                newfilename = newfilename + "/" + x[i];

                            File newfile = new File(newfilename + "/" + newname + ".txt");

//                            Log.e("Old file name", oldfile + " ");
//                            Log.e("New file name", newfile + " ");

                            if (!newfile.exists()) {
                                if (oldfile.renameTo(newfile)) {
                                    Toast.makeText(mContext, "File renamed.", Toast.LENGTH_LONG).show();
                                    mContext.onRefresh();
                                } else {
                                    Toast.makeText(mContext, "File can't be renamed.", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(mContext, "File already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .show();
    }

    /**
     * Prints a file
     *
     * @param fileName Path of file to be printed
     */
    private void doPrint(String fileName) {
        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);

        mFileName = fileName;
        String jobName = mContext.getString(R.string.app_name) + " Document";
        if (Build.VERSION.SDK_INT >= 19) {
            printManager.print(jobName, pda, null);
        } else {
            Toast.makeText(mContext, "Error: Your Android version cannot support print.", Toast.LENGTH_SHORT).show();
        }
    }

    private PrintDocumentAdapter pda = new PrintDocumentAdapter() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(mFileName);
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (Exception e) {
                //Catch exception
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("myFile").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

            callback.onLayoutFinished(pdi, true);
        }
    };

    private void shareFile(String name) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/File");
        File file = new File(name);
        Uri uri;
        if (Build.VERSION.SDK_INT < 24)
            uri = Uri.fromFile(file);
        else {
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            mContext.startActivity(Intent.createChooser(intent, "Share File file"));
        } catch (Exception e) {
            Toast.makeText(mContext, "Error: Cannot open or share created File report.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
