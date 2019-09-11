package com.pavchishin.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.pavchishin.scanner.MainActivity.FILES_ARRAY;
import static com.pavchishin.scanner.MainActivity.FILE_DIR;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    final static String DOC_QUANTITY = "QuantityDoc";
    final static String LIST_PLACES = "NamePlace";

    LinearLayout buttonLayout;
    LinearLayout placeLayout;

    Button btnScan;

    ArrayList<String> listFileNames;
    ArrayList<String> listPlaceNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        buttonLayout = findViewById(R.id.layout_button);
        placeLayout = findViewById(R.id.layout_places);

        btnScan = findViewById(R.id.button_scan);
        btnScan.setOnClickListener(this);

        Intent intent = getIntent();
        listFileNames = intent.getStringArrayListExtra(FILES_ARRAY);
        for (String files : listFileNames) {
            fillNameLayout(files);
        }

        listPlaceNames = new ArrayList<>();
    }

    private void fillNameLayout(String fileName) {
        new Thread(() -> {
            Set<String> placeList = new HashSet<>();
            try {
                File workDirPath = new File(Environment.getExternalStorageDirectory() + File.separator + FILE_DIR);
                InputStream stream = new FileInputStream(workDirPath.toString() + File.separator + fileName);
                XSSFWorkbook workbook = new XSSFWorkbook(stream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;
                String cellValue;

                for (int rowIndex = 19; rowIndex < sheet.getLastRowNum(); rowIndex++){
                    row = sheet.getRow(rowIndex);
                    if (row != null) {
                        Cell cell = row.getCell(7);
                        cellValue = cell.getStringCellValue();
                        if (cellValue != null &&  cellValue.length() != 0) {
                            placeList.add(cellValue);
                        }
                    }
                }

                for (String list : placeList) {
                    runOnUiThread(() -> {
                        if(!listPlaceNames.contains(list)) {
                            listPlaceNames.add(list);
                            Button button = new Button(SecondActivity.this);
                            button.setText(list);
                            button.setTextSize(20);
                            button.setTextColor(Color.WHITE);
                            button.setBackgroundColor(Color.BLACK);
                            placeLayout.addView(button);
                        }

                    });
                }

                String val = String.valueOf(workbook.getSheetAt(0).getRow(15).getCell(4));
                //Log.d("tag", val);
                runOnUiThread(() -> {

                    Button button = new Button(this);
                    button.setText(val);
                    button.setTextSize(20);
                    button.setTextColor(Color.WHITE);
                    button.setBackgroundColor(Color.BLACK);
                    buttonLayout.addView(button);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(SecondActivity.this, ScanActivity.class);
        int numDocs = buttonLayout.getChildCount();
        intent.putExtra(DOC_QUANTITY, numDocs);
        intent.putStringArrayListExtra(LIST_PLACES, listPlaceNames);
        startActivity(intent);
    }
}
