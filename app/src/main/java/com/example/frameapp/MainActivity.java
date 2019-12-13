package com.example.frameapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Matrix;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_IMAGE_INT = 1;
    private static final int LOAD_FRAME_INT = 2;

    private static final int INTERNET_PERMISSION = 10;
    Bitmap frame;
    Bitmap orignalImage;
    Bitmap proccesedImage;
    ImageView imageView;
    Button loadButton;
//    Button save_Button;
    Button saveToGallery;
    Uri sourceUri;
    String sourceUriText;
    //    private ProgressBar progressBar;
    Button load_frame;
    Button share;
    Button rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        progressBar = findViewById(R.id.progress_bar);
        imageView = findViewById(R.id.image);

        loadButton = findViewById(R.id.loadimage);
        saveToGallery = findViewById(R.id.savetogallery);
        share = findViewById(R.id.share);
        rotate = findViewById(R.id.rotate);
        load_frame = findViewById(R.id.loadframe);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateBitmap();
                proccesedImage = combineImages(frame,orignalImage);
                imageView.setImageBitmap(proccesedImage);
            }
        });

        load_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "Permission is already granted", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(MainActivity.this, FrameChooser.class);
                    startActivityForResult(intent, LOAD_FRAME_INT);
                }
                else{
                    requestStoragePermission();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), proccesedImage, "Framed Image", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });

//load image
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        == PackageManager.PERMISSION_GRANTED){
                    requestStoragePermission();
                }
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, LOAD_IMAGE_INT);

//                String fileName = System.currentTimeMillis() + ".jpg";
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }



                Intent intent = new Intent(Intent.ACTION_PICK);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setType("image/*");
                startActivityForResult(intent, LOAD_IMAGE_INT);


            }
        });

        saveToGallery.setOnClickListener(new View.OnClickListener() {

            // TODO not implemented well
            @Override
            public void onClick(View v) {
                saveToGallery.setActivated(false);
                saveToGallery.setTextColor(Color.RED);
//                FileOutputStream output;
                // Find the SD Card path
//                File filepath = Environment.getExternalStorageDirectory();
//
//                // Create a new folder in SD Card
//                File dir = new File(filepath.getAbsolutePath()
//                        // + "/Raman/"
//                        + "/DCIM/Camera/"
//                );
//                dir.mkdirs();

                // Retrieve the image from the res folder TODO changed {priniciple to getResources()}
                //        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.circleframe);
                //        Bitmap bitmap1 = drawable.getBitmap();

                // Create a name for the saved image


                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsoluteFile().getAbsolutePath() + "/Raman/");
                dir.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

//                File file = new File(dir,timeStamp+ ".jpg" );

                 File file = new File(dir, timeStamp + ".jpg");

                Toast.makeText(MainActivity.this, "File Tested " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

//                if (file.exists()) //file.delete();

                try {

//                    output = new FileOutputStream(file);
                    FileOutputStream out = new FileOutputStream(file);

                    // Compress into png format image from 0% - 100%
                    proccesedImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    Toast.makeText(MainActivity.this, "Firle saved", Toast.LENGTH_LONG).show();
                    saveToGallery.setActivated(true);
                    saveToGallery.setTextColor(Color.BLACK);

                }

                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Not saved", Toast.LENGTH_LONG).show();
                }
                //                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(file);
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    sendBroadcast(intent);
                }
            }
        });

        orignalImage = BitmapFactory.decodeResource(getResources(),R.drawable.kingfisher);

        frame = BitmapFactory.decodeResource(getResources(),R.drawable.circleframe);



        proccesedImage = combineImages(frame,orignalImage);
        imageView.setImageBitmap(proccesedImage);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNET_PERMISSION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNET_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this, "String test ", Toast.LENGTH_SHORT);
        if(requestCode == LOAD_IMAGE_INT && resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    sourceUri = data.getData();
                    sourceUriText = sourceUri.toString();
//                    String str = sourceUri.getLastPathSegment();

                    Bitmap bm1 ;//= null
                    Bitmap newBitmap = null;

                    String filePath ="";
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(sourceUri, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                         filePath = cursor.getString(columnIndex);
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                    .........
                    }
                    cursor.close();

                    Toast.makeText(MainActivity.this, "String test " + filePath.substring(filePath.lastIndexOf(".") + 1), Toast.LENGTH_LONG).show();

//                    System.out.println("String test " + sourceUriText);

//                    Log.d("String", "String test " + sourceUriText);
                    try {
                        orignalImage = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(sourceUri));
                        proccesedImage = combineImages(frame,orignalImage);
                        imageView.setImageBitmap(proccesedImage);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(this,"file error", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
        else if(requestCode == LOAD_FRAME_INT && resultCode == RESULT_OK){

            Toast.makeText(MainActivity.this,
                    "Fetching the frame " + data.getStringExtra("description"), Toast.LENGTH_SHORT).show();

            MyAsyncTask task = new MyAsyncTask(this);
            task.execute(data.getStringExtra("description")+"");
        }
    }

    public  Bitmap rotateBitmap()//Bitmap source)//, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);//angle);
        return Bitmap.createBitmap(frame /*source*/, 0, 0, frame.getWidth()/*source.getWidth()*/, frame.getHeight()/*source.getHeight()*/, matrix, true);
    }

    /**/
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap combineImages(Bitmap frame, Bitmap image) {

        Bitmap cs = null;
        Bitmap rs = null;

        rs = Bitmap.createScaledBitmap(frame, image.getWidth(),
                image.getHeight(), true);

        cs = Bitmap.createBitmap(rs.getWidth(), rs.getHeight(),
                Bitmap.Config.RGB_565);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(image, 0, 0, null);
        comboImage.drawBitmap(rs, 0, 0, null);

        if (rs != null) {
            rs.recycle();
            rs = null;
        }
        Runtime.getRuntime().gc();

        return cs;
    }

    private static class MyAsyncTask extends AsyncTask<String , Integer, String>{
        private WeakReference<MainActivity> activityWeakReference;
        Bitmap bitmap;
        public MyAsyncTask(MainActivity activity) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

//            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            super.onPostExecute(s);
            activity.frame = bitmap;

            activity.proccesedImage = activity.combineImages(bitmap,activity.orignalImage);
            activity.imageView.setImageBitmap(activity.proccesedImage);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            bitmap = getBitmapFromURL(strings[0]);
            return "Fished";
        }
    }



}