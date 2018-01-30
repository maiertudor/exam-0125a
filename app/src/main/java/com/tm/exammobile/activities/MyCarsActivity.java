package com.tm.exammobile.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tm.exammobile.R;
import com.tm.exammobile.adapters.CursorCarsListAdapter;
import com.tm.exammobile.helpers.CarsHelper;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class MyCarsActivity extends AppCompatActivity {

    private static final String TAG = "MyCarsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_cars_activity);

        ListView carsListView = (ListView) findViewById(R.id.mycars_list_view);

        final CarsHelper helper = new CarsHelper(this);
        Cursor data = helper.getData();
        final CursorCarsListAdapter adapter = new CursorCarsListAdapter(this, data);
        carsListView.setAdapter(adapter);

        Button removeBtn = (Button) findViewById(R.id.remove_my_cars_btn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.deleteAllCars()) {
                    Toast.makeText(getApplicationContext(), "Removed all local stored cars", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Removed all local stored cars");

                    adapter.changeCursor(helper.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR Removing all local stored cars", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "ERROR Removing all local stored cars");
                }
            }
        });
    }
}
