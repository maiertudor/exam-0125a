package com.tm.exammobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tm.exammobile.R;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.entry_activity);

        Button clientBtn = (Button) findViewById(R.id.client_activity_btn);
        clientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClientActivity.class));
            }
        });

        Button employeeBtn = (Button) findViewById(R.id.employee_activity_btn);
        employeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EmployeeActivity.class));
            }
        });

    }
}
