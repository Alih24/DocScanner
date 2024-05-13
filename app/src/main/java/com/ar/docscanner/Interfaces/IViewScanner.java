package com.ar.docscanner.Interfaces;

import android.graphics.Bitmap;



public interface IViewScanner {

    void onResult(Bitmap bitmap);

    void chooseImage();

    void editImage(String path);
}
