package com.pavchishin.scanner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;

import static com.pavchishin.scanner.SecondActivity.DOC_QUANTITY;
import static com.pavchishin.scanner.SecondActivity.LIST_PLACES;
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

    ImageView imageView;

    String scanValue;
    int count;


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
        count = Integer.parseInt((String) lastPlace.getText());
        try {
            Thread.sleep(200);
            scannerField.setText("");
            scannerStart(listDouble, scanValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void scannerStart(HashSet<String> listDoublle, String scanValue) {

        for (String name : listDoublle) {
            if (scanValue.contains(name)){
                count--;
                lastPlace.setText(String.valueOf(count));
                infoField.setText(String.format("Штрихкод найден! %s", name));
                imageView.setImageResource(R.drawable.ok_im);
                infoField.setTextColor(Color.GREEN);
                listDoublle.remove(name);
                sp.play(soundGood, 1, 1, 0, 0, 1);
                break;
            } else {
                infoField.setText(String.format("Штрихкод не найден! %s", scanValue));
                infoField.setTextColor(Color.RED);
                imageView.setImageResource(R.drawable.not_ok_im);
                sp.play(soundBad, 1, 1, 0, 0, 1);
            }
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) { }
}
