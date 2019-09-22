
package com.pavchishin.scanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.pavchishin.scanner.SecondActivity.DOC_QUANTITY;
import static com.pavchishin.scanner.SecondActivity.LIST_PLACES;
import static com.pavchishin.scanner.SecondActivity.PLACE_QUANTITY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String FILE_DIR = "WorkFiles";
    final static String FILES_ARRAY = "ArrayFiles";
    final static String TAG = "--->>>";
    final static String TEMP_DIR = "TEMP";
    final static String TEMP_FILE = "tempFile.txt";

    AlertDialog.Builder dialog;
    Context context;

    int number;

    private Button startButton;

    File [] files;
    List<String> fileNames;
    List<String> fileNamesRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        context = MainActivity.this;

        startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        fileNames = new ArrayList<>();
        fileNamesRestore = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        startButton.setEnabled(false);
        checkTempFolder();
    }

    private void checkTempFolder() {
        File path = new File(this.getCacheDir() + File.separator + TEMP_DIR);
        if (path.list().length > 0){
            createDialog(path);
        } else {
            path.mkdirs();
            goToSecondPage();
        }
    }

    private void createDialog(File path) {
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Сделайте выбор");
        String restoreData = "Востановить данные";
        String newScanning = "Новое сканирование";
        dialog.setPositiveButton(restoreData, (dialog, which) -> goToScanPage());

        dialog.setNegativeButton(newScanning, (dialog, which) -> {
            dialog.cancel();
            goToSecondPage();
            new File(path, TEMP_FILE).getAbsoluteFile().delete();
        });
        dialog.show();
    }

    private void goToScanPage() {
        File tmpFile = new File(this.getCacheDir() + File.separator + TEMP_DIR, TEMP_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(tmpFile))){
            String str = "";
            while ((str = br.readLine()) != null) {
                if (str.length() > 3) {
                    fileNamesRestore.add(str);
                } else {
                    number = Integer.parseInt(str.trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File workDirPath = new File(Environment.getExternalStorageDirectory() + File.separator + FILE_DIR);
        if (workDirPath.exists()) {
            files = listFiles(workDirPath).toArray(new File[0]);
        }

        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        intent.putStringArrayListExtra(LIST_PLACES, (ArrayList<String>) fileNamesRestore);
        intent.putExtra(DOC_QUANTITY, files.length);
        intent.putExtra(PLACE_QUANTITY, number);
        startActivity(intent);
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
