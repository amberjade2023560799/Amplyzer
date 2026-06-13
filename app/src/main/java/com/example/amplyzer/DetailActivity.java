package com.example.amplyzer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    Spinner spinnerDetailMonth;
    EditText etDetailUnit;
    RadioGroup radioGroupDetailRebate;
    TextView tvDetailTotal, tvDetailFinal;
    Button btnUpdate, btnDelete, btnBackToList;

    DatabaseHelper myDb;
    String recordId;

    String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        myDb = new DatabaseHelper(this);

        spinnerDetailMonth = findViewById(R.id.spinnerDetailMonth);
        etDetailUnit = findViewById(R.id.etDetailUnit);
        radioGroupDetailRebate = findViewById(R.id.radioGroupDetailRebate);
        tvDetailTotal = findViewById(R.id.tvDetailTotal);
        tvDetailFinal = findViewById(R.id.tvDetailFinal);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnBackToList = findViewById(R.id.btnBackToList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                months
        );
        spinnerDetailMonth.setAdapter(adapter);

        recordId = getIntent().getStringExtra("ID");

        loadData();

        btnUpdate.setOnClickListener(v -> updateRecord());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Record")
                    .setMessage("Are you sure you want to delete this bill record?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        int deleted = myDb.deleteData(recordId);

                        if (deleted > 0) {
                            Toast.makeText(this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnBackToList.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, ViewRecordsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadData() {
        Cursor cursor = myDb.getDataById(recordId);

        if (cursor.moveToFirst()) {
            String month = cursor.getString(1);
            int unit = cursor.getInt(2);
            double total = cursor.getDouble(3);
            int rebate = cursor.getInt(4);
            double finalCost = cursor.getDouble(5);

            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    spinnerDetailMonth.setSelection(i);
                    break;
                }
            }

            etDetailUnit.setText(String.valueOf(unit));
            setRebateRadioButton(rebate);

            tvDetailTotal.setText(String.format("Total Charges: RM %.2f", total));
            tvDetailFinal.setText(String.format("Final Cost: RM %.2f", finalCost));
        }

        cursor.close();
    }

    private void updateRecord() {
        String unitText = etDetailUnit.getText().toString().trim();

        if (unitText.isEmpty()) {
            etDetailUnit.setError("Please enter electricity unit");
            etDetailUnit.requestFocus();
            return;
        }

        int unit = Integer.parseInt(unitText);

        if (unit < 1 || unit > 1000) {
            etDetailUnit.setError("Unit must be between 1 and 1000 kWh");
            etDetailUnit.requestFocus();
            return;
        }

        int rebate = getSelectedRebate();
        String month = spinnerDetailMonth.getSelectedItem().toString();

        double total = calculateCharges(unit);
        double finalCost = total - (total * rebate / 100);

        boolean updated = myDb.updateData(recordId, month, unit, total, rebate, finalCost);

        if (updated) {
            tvDetailTotal.setText(String.format("Total Charges: RM %.2f", total));
            tvDetailFinal.setText(String.format("Final Cost: RM %.2f", finalCost));
            Toast.makeText(this, "Record updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRebateRadioButton(int rebate) {
        if (rebate == 0) {
            radioGroupDetailRebate.check(R.id.rbDetail0);
        } else if (rebate == 1) {
            radioGroupDetailRebate.check(R.id.rbDetail1);
        } else if (rebate == 2) {
            radioGroupDetailRebate.check(R.id.rbDetail2);
        } else if (rebate == 3) {
            radioGroupDetailRebate.check(R.id.rbDetail3);
        } else if (rebate == 4) {
            radioGroupDetailRebate.check(R.id.rbDetail4);
        } else if (rebate == 5) {
            radioGroupDetailRebate.check(R.id.rbDetail5);
        }
    }

    private int getSelectedRebate() {
        int selectedId = radioGroupDetailRebate.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);

        String rebateText = selectedRadio.getText().toString().replace("%", "");
        return Integer.parseInt(rebateText);
    }

    private double calculateCharges(int unit) {
        double total;

        if (unit <= 200) {
            total = unit * 0.218;
        } else if (unit <= 300) {
            total = (200 * 0.218) + ((unit - 200) * 0.334);
        } else if (unit <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((unit - 300) * 0.516);
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unit - 600) * 0.546);
        }

        return total;
    }
}