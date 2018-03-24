package com.example.julio.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.IOException;

/**
 * Created by julio on 20/03/18.
 */

public class Foto {
    private static Foto imgFoto;
    private Bitmap foto;
    private int tamFotoWidth, tamFotoHeight;

    public static Foto init(int tamFotoWidth, int tamFotoHeight){
        if(imgFoto == null)
            imgFoto = new Foto(tamFotoWidth, tamFotoHeight);
        return imgFoto;
    }

    public Foto(Bitmap foto, int tamFotoWidth, int tamFotoHeight){
        this(tamFotoWidth,tamFotoHeight);
        this.foto = foto;
    }

    public Foto(int tamFotoWidth, int tamFotoHeight) {
        this.tamFotoWidth = tamFotoWidth;
        this.tamFotoHeight = tamFotoHeight;
    }

    private Bitmap resizeImage(Bitmap foto, int width, int height) {
        int w = foto.getWidth();
        int h = foto.getHeight();

        float scaleX = (float) width / foto.getWidth();
        float scaleY = (float) height / foto.getHeight();

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);

        return Bitmap.createBitmap(foto, 0, 0, w, h, matrix, false);
    }

    private Bitmap resizeImage(String imag, int targetW, int targetH) {
        // Get the dimensions of the bitPhoto
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imag, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imag, bmOptions);
    }


    public void cleanFoto(){
        foto = null;
    }

    // sets
    public void setFoto(String img){
        this.foto = resizeImage(img,tamFotoWidth,tamFotoHeight);
        //this.miniatura = resizeImage(foto,tamMiniaturaWidth, tamMiniaturaHeight);
    }

    public void setTamFotoWidth(int tamFotoWidth) {
        this.tamFotoWidth = tamFotoWidth;
    }

    public void setTamFotoHeight(int tamFotoHeight) {
        this.tamFotoHeight = tamFotoHeight;
    }


    // gets
    public Bitmap getFoto() {
        return foto;
    }

    public int getTamFotoWidth() {
        return tamFotoWidth;
    }

    public int getTamFotoHeight() {
        return tamFotoHeight;
    }
}
