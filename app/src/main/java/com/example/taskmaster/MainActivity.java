package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Handler handler;
    private SharedPreferences sharedPreferences;
    private String teamName = "";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.addPlugin(new AWSCognitoAuthPlugin());
//            Amplify.configure(getApplicationContext());
//
//            Log.i("Tutorial", "Initialized Amplify");
//        } catch (AmplifyException e) {
//            Log.e("Tutorial", "Could not initialize Amplify", e);
//        }

        // check authenticated

        // sign up
//        AuthSignUpOptions options = AuthSignUpOptions.builder()
//                .userAttribute(AuthUserAttributeKey.email(), "yousef_haimour@yahoo.com")
//                .build();
//
//        Amplify.Auth.signUp("username", "Password123", options,
//                result -> Log.i("AuthQuickStart", "Result: " + result.toString()),
//                error -> Log.e("AuthQuickStart", "Sign up failed", error)
//        );

        // confirm sign up
//        Amplify.Auth.confirmSignUp(
//                "username",
//                "204751",
//                result -> Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
//                error -> Log.e("AuthQuickstart", error.toString())
//        );

        // sign in
//        Amplify.Auth.signIn(
//                "username",
//                "Password123",
//                result -> Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete"),
//                error -> Log.e("AuthQuickstart", error.toString())
//        );

//        Team team = Team.builder().name("Team1").build();
//        Team team2 = Team.builder().name("Team2").build();
//        Team team3 = Team.builder().name("Team3").build();
//
//        Amplify.API.mutate(ModelMutation.create(team1),
//                success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );
//        Amplify.API.mutate(ModelMutation.create(team2),
//                success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );
//        Amplify.API.mutate(ModelMutation.create(team3),
//                success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );


        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                    return false;
                });

        Button addActivityBtn = findViewById(R.id.addActivityBtn);
        addActivityBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AddTaskActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.signOutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Amplify.Auth.signOut(
                        () -> {
                            Log.i("AuthQuickstart", "Signed out successfully");
                            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                            startActivity(intent);
                        },
                        error -> Log.e("AuthQuickstart", error.toString())
                );
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        teamName = sharedPreferences.getString("teamName","");

        Log.i("team", "onResume: " + teamName);
        TextView username = findViewById(R.id.usernameTitle);
        username.setText(sharedPreferences.getString("username","User")+" Tasks");

        if (isNetworkAvailable(getApplicationContext()) && tasks.isEmpty()){
            Amplify.API.query(ModelQuery.list(Task.class),
                    response -> {
                        for (Task task : response.getData()) {
                            if ( !this.tasks.contains(task) && task != null && task.getTeam() != null ) {
                                    this.tasks.add(task);
                            }
                            Log.i("API", "Query: " + task);
                        }
                        handler.sendEmptyMessage(1);
                    }, failure -> Log.e("Add", "Could not query DataStore", failure)
            );
        }else {
            Amplify.DataStore.query(Task.class,tasks -> {
                        while (tasks.hasNext()) {
                            Task task = tasks.next();
                            if (task.getTitle() != null && !this.tasks.contains(task)) {
                                    this.tasks.add(task);
                            }
                            Log.i("Datastore", "Query: " + task);
                        }
                    }, failure -> Log.e("Add", "Could not query DataStore", failure)
                    );
        }
        teamName = sharedPreferences.getString("teamName","");
        setTaskAdapter();
    }

    public void setTaskAdapter(){
        ArrayList<Task> teamTasks;
        if (!teamName.isEmpty()){
            teamTasks = new ArrayList<>();
            for (Task task:
                 tasks) {
                if (task.getTeam().getName().equals(teamName)) teamTasks.add(task)  ;
            }
        }else {
            teamTasks = tasks;
        }
        recyclerView = findViewById(R.id.recyclerView);
        taskAdapter = new TaskAdapter(teamTasks, new TaskAdapter.OnTaskListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                intent.putExtra("taskName", teamTasks.get(position).getTitle());
                intent.putExtra("taskBody", teamTasks.get(position).getBody());
                intent.putExtra("taskState", teamTasks.get(position).getState());
                startActivity(intent);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDeleteClick(int position) {
                Task task = tasks.remove(position);

                Amplify.API.mutate(ModelMutation.delete(task),
                        success -> Log.i("Tutorial", "Deleted item API: " + success.getData()),
                        error -> Log.e("Tutorial", "Could not save item to DataStore", error)
                );

                Amplify.DataStore.delete(task,
                        success -> Log.i("Tutorial", "Deleted item DataStore: " + success.item().getTitle()),
                        error -> Log.e("Tutorial", "Could not save item to DataStore", error)
                );

                taskAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Item Deleted!", Toast.LENGTH_SHORT).show();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(taskAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsItem) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
