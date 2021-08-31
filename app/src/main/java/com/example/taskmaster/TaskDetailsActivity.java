package com.example.taskmaster;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class TaskDetailsActivity extends AppCompatActivity {

    private String fileName;
    private URL url;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();

        TextView textTitle = findViewById(R.id.taskName);
        TextView textState = findViewById(R.id.taskState);
        TextView textDescription = findViewById(R.id.taskBody);
        TextView locationText = findViewById(R.id.locationText);

        fileName = intent.getExtras().getString("taskFileName");

        Log.i("FIlE_NAME", "onCreate: "+fileName);
        textTitle.setText(intent.getExtras().getString("taskName"));
        textState.setText( "("+intent.getExtras().getString("taskState")+")");
        textDescription.setText( intent.getExtras().getString("taskBody"));
        locationText.setText("Lat: "+intent.getExtras().getString("taskLat")+", Long: "+intent.getExtras().getString("taskLong"));

        Amplify.Storage.getUrl(
                fileName,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully generated: " + result.getUrl());
                    url= result.getUrl();
                },
                error -> Log.e("MyAmplifyApp", "URL generation failure", error)
        );

        ImageView imageView = findViewById(R.id.imageView);
        Amplify.Storage.downloadFile(
                fileName,
                new File(getApplicationContext().getFilesDir() +"/"+ fileName),
                result -> {
                    Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getPath());
                    String fileType = null;

                    try {
                        fileType = Files.probeContentType(result.getFile().toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (fileType.split("/")[0].equals("image")){
                        imageView.setImageBitmap(BitmapFactory.decodeFile(result.getFile().getPath()));
                    }
                    else {
                        String linkedText = String.format("<a href=\"%s\">download File</a> ", url);

                        TextView test = findViewById(R.id.taskLink);
                        test.setText(Html.fromHtml(linkedText, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        test.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    },
                error -> Log.e("MyAmplifyApp",  "Download Failure ",error)
        );

    }


}