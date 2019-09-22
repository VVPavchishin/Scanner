package com.pavchishin.scanner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static com.pavchishin.scanner.MainActivity.TEMP_DIR;
import static com.pavchishin.scanner.MainActivity.TEMP_FILE;
import static com.pavchishin.scanner.MainActivity.TAG;
import static com.pavchishin.scanner.SecondActivity.DOC_QUANTITY;
import static com.pavchishin.scanner.SecondActivity.LIST_PLACES;
import static com.pavchishin.scanner.SecondActivity.PLACE_QUANTITY;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener, SoundPool.OnLoadCompleteListener {

    final int MAX_STREAMS = 1;

    SoundPool sp;
    int soundGood;
    int soundBad;


    TextView quantityDocs;
    TextView quantityPlace;
    TextView lastPlace;
    TextView infoField;

    EditText scannerField;
    HashSet<String> listDouble;

    Button exitBtn;

    ImageView imageView;
    LinearLayout showLayout;

    String scanValue;
    int count;
    int placeQuantity;


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

        exitBtn = findViewById(R.id.btn_exit);

        quantityDocs = findViewById(R.id.txt_quantity_pl);
        quantityPlace = findViewById(R.id.txt_quantity_places);
        lastPlace = findViewById(R.id.txt_ostatok);
        infoField = findViewById(R.id.txt_signal);

        imageView = findViewById(R.id.image_ok_not);
        showLayout = findViewById(R.id.show_layout);

        Intent intent = getIntent();
        int docQuantity = intent.getIntExtra(DOC_QUANTITY, 0);
        placeQuantity = intent.getIntExtra(PLACE_QUANTITY, 0);
        ArrayList<String> namesPlace = intent.getStringArrayListExtra(LIST_PLACES);

        listDouble = new HashSet<>(namesPlace);
        quantityDocs.setText(String.valueOf(docQuantity));
        quantityPlace.setText(String.valueOf(placeQuantity));
        lastPlace.setText(String.valueOf(listDouble.size()));

        showOnDisplay(listDouble);

        exitBtn.setOnClickListener(view -> {
            File path = new File(this.getCacheDir() + File.separator + TEMP_DIR);
            new File(path, TEMP_FILE).getAbsoluteFile().delete();
            finishAffinity();
            System.exit(0);
        });
    }

    private void showOnDisplay(HashSet<String> namesPlace) {
        for (String name : namesPlace) {
            addButtons(name);
        }
    }

    @Override
    public void onClick(View v) {
        scanValue = String.valueOf(scannerField.getText());
        count = Integer.parseInt((String) lastPlace.getText());
        try {
            Thread.sleep(200);
            scannerField.setText("");
            scannerStart(listDouble, scanValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                removeFromShow();
                sp.play(soundGood, 1, 1, 0, 0, 1);
                break;
            } else {
                infoField.setText(String.format("Штрихкод не найден! %s", scanValue));
                infoField.setTextColor(Color.RED);
                imageView.setImageResource(R.drawable.not_ok_im);
                sp.play(soundBad, 1, 1, 0, 0, 1);
            }
        }
        writeToFile(listDouble);
    }

    private void writeToFile(HashSet<String> listDouble) {
        File path = new File(this.getCacheDir() + File.separator + TEMP_DIR, TEMP_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))){
            bw.write(placeQuantity + "\n");
            for (String str : listDouble){
                bw.write(str + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeFromShow() {
        showLayout.removeAllViews();
        for (String nameBtn : listDouble){
            addButtons(nameBtn);
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) { }

    private void addButtons(String name) {
        Button button = new Button(ScanActivity.this);
        button.setText(name);
        button.setTextSize(30);
        button.setGravity(Gravity.RIGHT);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.BLACK);
        showLayout.addView(button);
    }
}
