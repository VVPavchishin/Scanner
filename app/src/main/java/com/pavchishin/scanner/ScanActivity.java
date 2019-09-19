package com.pavchishin.scanner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;

import static com.pavchishin.scanner.SecondActivity.DOC_QUANTITY;
import static com.pavchishin.scanner.SecondActivity.LIST_PLACES;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener, SoundPool.OnLoadCompleteListener {

    final int MAX_STREAMS = 1;
    final static String FILE_TMP = "tempFile.txt";
    final static String TAG = "--->>>";

    SoundPool sp;
    int soundGood;
    int soundBad;


    TextView quantityDocs;
    TextView quantityPlace;
    TextView lastPlace;
    TextView infoField;

    EditText scannerField;
    HashSet<String> listDouble;

    ImageView imageView;
    Button exit;

    String scanValue;
    int count;

    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        scannerField = findViewById(R.id.scan_text);
        scannerField.setOnClickListener(this);
        scannerField.getBackground().mutate().
                setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);

        soundGood = sp.load(this, R.raw.good, 1);
        soundBad = sp.load(this, R.raw.bad1, 1);


        quantityDocs = findViewById(R.id.txt_quantity_pl);
        quantityPlace = findViewById(R.id.txt_quantity_places);
        lastPlace = findViewById(R.id.txt_ostatok);
        infoField = findViewById(R.id.txt_signal);

        imageView = findViewById(R.id.image_ok_not);

        exit = findViewById(R.id.btn_exit);
        exit.setOnClickListener(view -> this.finishAffinity());

        Intent intent = getIntent();
        int docQuantity = intent.getIntExtra(DOC_QUANTITY, 0);
        ArrayList<String> namesPlace = intent.getStringArrayListExtra(LIST_PLACES);
        listDouble = new HashSet<>(namesPlace);
        quantityDocs.setText(String.valueOf(docQuantity));
        quantityPlace.setText(String.valueOf(listDouble.size()));
        lastPlace.setText(String.valueOf(listDouble.size()));
    }

    @Override
    public void onClick(View v) {
        scanValue = String.valueOf(scannerField.getText());
        count = Integer.parseInt(lastPlace.getText().toString());
            scannerField.setText("");
            scannerStart(listDouble, scanValue);
            saveToFile(listDouble);
    }

    private void scannerStart(HashSet<String> listDouble, String scanValue) {

        for (String name : listDouble) {
            if (scanValue.contains(name)){
                count--;
                lastPlace.setText(String.valueOf(count));
                infoField.setText(String.format("Штрихкод найден! %s", name));
                imageView.setImageResource(R.drawable.ok_im);
                infoField.setTextColor(Color.GREEN);
                listDouble.remove(name);
                sp.play(soundGood, 1, 1, 0, 0, 1);
                break;
            } else {
                infoField.setText(String.format("Штрихкод не найден! %s", scanValue));
                sp.play(soundBad, 1, 1, 0, 0, 1);
                infoField.setTextColor(Color.RED);
                imageView.setImageResource(R.drawable.not_ok_im);

            }
        }
    }

    private void saveToFile(HashSet<String> listDouble) {
        File file = new File(this.getCacheDir(), FILE_TMP);
        writeFile(file);
    }

    private void writeFile(File file) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                openFileOutput(file.getName(), MODE_APPEND)))){
            Log.d(TAG, file.getName());
            bw.write("Содержимое файла " + file.getName() + "\n");
            Log.d(TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) { }
}
