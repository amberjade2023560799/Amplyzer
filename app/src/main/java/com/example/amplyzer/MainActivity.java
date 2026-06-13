package com.example.amplyzer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnit;
    RadioGroup radioGroupRebate;
    TextView tvTotalCharges, tvFinalCost;
    Button btnCalculateSave, btnReset, btnViewRecords, btnAbout;

    DatabaseHelper myDb;

    double totalCharges = 0;
    double finalCost = 0;
    int selectedRebate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        btnCalculateSave = findViewById(R.id.btnCalculateSave);
        btnReset = findViewById(R.id.btnReset);
        btnReset = findViewById(R.id.btnReset);
        btnViewRecords = findViewById(R.id.btnViewRecords);
        btnAbout = findViewById(R.id.btnAbout);

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                months
        );
        spinnerMonth.setAdapter(adapter);

        btnCalculateSave.setOnClickListener(v -> calculateAndSave());
        btnReset.setOnClickListener(v -> {
            spinnerMonth.setSelection(0);
            etUnit.setText("");
            radioGroupRebate.check(R.id.rb0);
            tvTotalCharges.setText("Total Charges: RM 0.00");
            tvFinalCost.setText("Final Cost: RM 0.00");

            Toast.makeText(MainActivity.this, "Form reset successfully", Toast.LENGTH_SHORT).show();
        });
        btnReset.setOnClickListener(v -> {
            spinnerMonth.setSelection(0);
            etUnit.setText("");
            radioGroupRebate.check(R.id.rb0);
            tvTotalCharges.setText("Total Charges: RM 0.00");
            tvFinalCost.setText("Final Cost: RM 0.00");
            Toast.makeText(this, "Form reset", Toast.LENGTH_SHORT).show();
        });

        btnViewRecords.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewRecordsActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void calculateAndSave() {
        String unitText = etUnit.getText().toString().trim();

        if (unitText.isEmpty()) {
            etUnit.setError("Please enter electricity unit");
            etUnit.requestFocus();
            return;
        }

        int unit = Integer.parseInt(unitText);

        if (unit < 1 || unit > 1000) {
            etUnit.setError("Unit must be between 1 and 1000 kWh");
            etUnit.requestFocus();
            return;
        }

        selectedRebate = getSelectedRebate();
        totalCharges = calculateCharges(unit);
        finalCost = totalCharges - (totalCharges * selectedRebate / 100);

        tvTotalCharges.setText(String.format("Total Charges: RM %.2f", totalCharges));
        tvFinalCost.setText(String.format("Final Cost: RM %.2f", finalCost));

        String month = spinnerMonth.getSelectedItem().toString();

        boolean inserted = myDb.insertData(month, unit, totalCharges, selectedRebate, finalCost);

        if (inserted) {
            Toast.makeText(this, "Bill calculated and saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save bill", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSelectedRebate() {
        int selectedId = radioGroupRebate.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);

        String rebateText = selectedRadio.getText().toString().replace("%", "");
        return Integer.parseInt(rebateText);
    }

    private double calculateCharges(int unit) {
        double total = 0;

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