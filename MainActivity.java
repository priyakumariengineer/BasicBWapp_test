package com.example.i20035.grayscale;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Uri imageUri;
    ImageView imageView;
    Bitmap grayBitmap,imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug())
        {
            Toast.makeText(this, "openCV loaded successfully", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "OpenCV failed", Toast.LENGTH_SHORT).show();
        }

        imageView = (ImageView)findViewById(R.id.imageView);

    }

    public void openGallary(View v)
    {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 100 && resultCode == RESULT_OK && data != null )
        {
             imageUri = data.getData();

             try
             {
                 imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
             }
             catch( IOException e )
             {
                e.printStackTrace();
             }

             imageView.setImageBitmap(imageBitmap);
        }
    }


    public void convert2gray(View v)
    {

        Mat Rgba = new Mat();
        Mat grayMat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        grayBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        Utils.bitmapToMat(imageBitmap,Rgba);
        Imgproc.cvtColor(Rgba,grayMat,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(grayMat, grayBitmap);

        imageView.setImageBitmap(grayBitmap);
    }


    public void saveToInternalStorage( View v )
    {
        //String defaultPicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        //String appPicShowDir = defaultPicDir + "/GrayScale/";
        //File file = new File(appPicShowDir);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GrayScale");
        file.mkdirs();

        File mypath = new File( file.getAbsolutePath()+"/profile.png");
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(mypath);
            grayBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(mypath));
            sendBroadcast(intent);

            Toast.makeText(this, "File saved in " + mypath.getAbsolutePath() , Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fos.flush();
                fos.close();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }

    }
}
