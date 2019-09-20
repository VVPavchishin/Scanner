
package com.pavchishin.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String FILE_DIR = "WorkFiles";
    final static String FILES_ARRAY = "ArrayFiles";
    final static String TAG = "--->>>";

    private Button startButton;
    private ProgressBar progressBar;

    File [] files;
    List<String> fileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        fileNames = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        startButton.setEnabled(false);
        goToSecondPage();
    }

    private void goToSecondPage() {
        File workDirPath = new File(Environment.getExternalStorageDirectory() + File.separator + FILE_DIR);
        if (workDirPath.exists()) {
            files = listFiles(workDirPath).toArray(new File[0]);
            for (File file : files){
                fileNames.add(file.getName());
            }
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putStringArrayListExtra(FILES_ARRAY, (ArrayList<String>) fileNames);
            startActivity(intent);

        } else {
            Toast toast = Toast.makeText(this, "Folder not found!",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public ArrayList<File> listFiles(File dir) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFiles(file));
            else
                files.add(file);
        }
        return files;
    }
}
