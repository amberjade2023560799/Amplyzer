package com.example.amplyzer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewRecordsActivity extends AppCompatActivity {

    ListView listViewRecords;
    DatabaseHelper myDb;

    ArrayList<String> recordList;
    ArrayList<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);

        listViewRecords = findViewById(R.id.listViewRecords);
        myDb = new DatabaseHelper(this);

        loadRecords();

        listViewRecords.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ViewRecordsActivity.this, DetailActivity.class);
            intent.putExtra("ID", idList.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }

    private void loadRecords() {
        Cursor cursor = myDb.getAllData();

        recordList = new ArrayList<>();
        idList = new ArrayList<>();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No bill records found", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String month = cursor.getString(1);
                double finalCost = cursor.getDouble(5);

                idList.add(id);
                recordList.add(month + "\nFinal Cost: RM " + String.format("%.2f", finalCost));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recordList
        );

        listViewRecords.setAdapter(adapter);
        cursor.close();
    }
}