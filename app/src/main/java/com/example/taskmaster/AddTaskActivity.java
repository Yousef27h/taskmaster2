package com.example.taskmaster;

//import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;

//import android.content.Context;
//import android.net.ConnectivityManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
//import android.view.View;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.amplifyframework.AmplifyException;
//import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
//import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddTaskActivity extends AppCompatActivity {
    private Spinner spinner;
    private Spinner teamSpinner;
    private final String[] state = new String[]{"new", "assigned", "in progress", "complete"};
    private Map<String, Team> teams =  new HashMap<>();
    private static final String TAG = "UploadFileMain";
    private String fileType;
    private String fileName;
    private File uploadFile;
    EditText taskTitleInput;
    EditText taskDescriptionInput;
    String imgSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);




        Button saveBtn = findViewById(R.id.saveBtn);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                state);

        spinner = findViewById(R.id.spinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Team 1", "Team 2", "Team 3"});
        teamSpinner = findViewById(R.id.teamSpinner);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(dataAdapter2);

        getTeams();

        taskTitleInput = findViewById(R.id.inputTaskTitle);
        taskDescriptionInput = findViewById(R.id.inputTaskDescription);

        saveBtn.setOnClickListener(view -> {



            switch(teamSpinner.getSelectedItem().toString()){
                case "Team 1":
                    uploadFile();
                    Task task = Task.builder().title(taskTitleInput.getText().toString()).body(taskDescriptionInput.getText().toString()).state(spinner.getSelectedItem().toString()).team(teams.get("Team 1")).fileName(fileName).build();
                    saveToAPI(task);
                    saveToDatastore(task);
//                    Log.i("add to team ", "onCreate: "+task.getTeam().getName());

                    break;
                case "Team 2":
                    uploadFile();
                    Task task2 = Task.builder().title(taskTitleInput.getText().toString()).body(taskDescriptionInput.getText().toString()).state(spinner.getSelectedItem().toString()).team(teams.get("Team 2")).fileName(fileName).build();
                    saveToAPI(task2);
                    saveToDatastore(task2);
//                    Log.i("add to team ", "onCreate: "+task2.getTeam().getName());

                    break;
                case "Team 3":
                    uploadFile();
                    Task task3 = Task.builder().title(taskTitleInput.getText().toString()).body(taskDescriptionInput.getText().toString()).state(spinner.getSelectedItem().toString()).team(teams.get("Team 3")).fileName(fileName).build();
                    saveToAPI(task3);
                    saveToDatastore(task3);
//                    Log.i("add to team ", "onCreate: "+task3.getTeam().getName());

                    break;
            }

                Toast toast = Toast.makeText(getApplicationContext(),"Submitted!", Toast.LENGTH_SHORT);
                toast.show();
        });

        findViewById(R.id.uploadBtn).setOnClickListener(view -> {
            getFileFromDevice();
        });
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null){


            Log.i("INTENT in RESUME", "onResume: "+intent.getData());

            if (intent.getType().equals("text/plain")){
                taskTitleInput.setText(extras.get("android.intent.extra.SUBJECT").toString());
                taskDescriptionInput.setText(extras.get("android.intent.extra.TEXT").toString());
            }else if (intent.getType().contains("image/")){

                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                String src = uri.getPath();

                imgSrc =src;

                fileType = getContentResolver().getType(uri);
                fileName = new SimpleDateFormat("yyMMddHHmmssZ").format(new Date())+"." + fileType.split("/")[1];

                File source = new File(src);
                String file = uri.getLastPathSegment();
                File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CustomFolder"+file);
                uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                uploadFile();
            }
        }

        if (imgSrc != null){
            TextView imgSrc = findViewById(R.id.imgSrc);
            imgSrc.setText(this.imgSrc);
            imgSrc.setVisibility(View.VISIBLE);
        }
    }

    private void getTeams() {
        Amplify.API.query(
                ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        switch(team.getName()) {
                            case "Team1":
                                this.teams.put("Team 1", team);
                                break;
                            case "Team2":
                                this.teams.put("Team 2", team);
                                break;
                            case "Team3":
                                this.teams.put("Team 3", team);
                                break;
                        }
                    }
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );
    }

    public void saveToAPI(Task task){
        Amplify.API.mutate(ModelMutation.create(task),
                success -> Log.i("Tutorial", "Saved item: " + success.getData()),
                error -> Log.e("Tutorial", "Could not save item to API", error)
        );
    }

    public void saveToDatastore(Task task){
        Amplify.DataStore.save(task,
                success -> Log.i("Tutorial", "Saved item: " + success.item()),
                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
        );
    }


    private void uploadFile(){
        Amplify.Storage.uploadFile(
                fileName,
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );
    }

    private void getFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent = Intent.createChooser(intent, "Choose a File");
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("SimpleDateFormat")
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        String src = uri.getPath();

                        imgSrc = src;

                        fileType = getContentResolver().getType(uri);
                        fileName = new SimpleDateFormat("yyMMddHHmmssZ").format(new Date())+"." + fileType.split("/")[1];
//                        Log.i("URIData", "onActivityResult: "+ uri);
//                        Log.i("fileTypeData", "onActivityResult: "+ fileType);
//                        Log.i("extension", "onActivityResult: "+ src);

                        File source = new File(src);
                        String file = uri.getLastPathSegment();
                        File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CustomFolder"+file);
                        uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

                        try {
//                    InputStream in = new FileInputStream(source);
//                    OutputStream out = new FileOutputStream(destination);
//                    FileUtils.copy(in, out);
                            InputStream inputStream = getContentResolver().openInputStream(data.getData());
                            FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });

}