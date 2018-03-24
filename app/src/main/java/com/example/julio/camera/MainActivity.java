package com.example.julio.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int RESULT_LOAD_IMAGE = 2;
    private static Foto fotoLogin;
    private AlertDialog mensagem;
    private ImageButton imagBtnPhoto;
    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagBtnPhoto = findViewById(R.id.photo);
        fotoLogin = Foto.init(1920, 1080);
        imagBtnPhoto.setImageBitmap(fotoLogin.getFoto());
    }


    // abrir app da camera do android
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.julio.camera.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            fotoLogin.setFoto(photoPath);

        }else if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            fotoLogin.setFoto(picturePath);
        }
        imagBtnPhoto.setImageBitmap(fotoLogin.getFoto());
    }


    // tela mensagem para editar a foto
    public void mensPhoto(View v) {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.add_photo_login, null);
        // btn galeria
        view.findViewById(R.id.btn_galeria).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                selectImageGaleria();
                mensagem.dismiss();
            }
        });
        // btn camera
        view.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dispatchTakePictureIntent();
                mensagem.dismiss();
            }
        });
        // btn remover foto
        Button btn= view.findViewById(R.id.btn_remover_foto);
        if(fotoLogin.getFoto() == null)
            btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoLogin.cleanFoto();
                imagBtnPhoto.setImageBitmap(fotoLogin.getFoto());
                mensagem.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        mensagem = builder.create();
        mensagem.show();
    }


    // abrir app do android para carregar a foto (galeria)
    private void selectImageGaleria() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), RESULT_LOAD_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                    RESULT_LOAD_IMAGE);
        }
    }
}