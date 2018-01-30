package com.tm.exammobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tm.exammobile.ExamApplication;
import com.tm.exammobile.R;
import com.tm.exammobile.adapters.CarsListAdapter;
import com.tm.exammobile.adapters.EmployeeCarsListAdapter;
import com.tm.exammobile.listeners.ConnectionReceiver;
import com.tm.exammobile.models.Car;
import com.tm.exammobile.rest.ApiClient;
import com.tm.exammobile.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class EmployeeActivity extends AppCompatActivity {

    private static final String TAG = "EmployeeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_activity);

        Button addBtn = (Button) findViewById(R.id.add_car_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmployeeActivity.this, AddCarActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        ListView carsListView = (ListView) findViewById(R.id.employee_cars_list_view);
        List<Car> cars = new ArrayList<>();
        final EmployeeCarsListAdapter adapter = new EmployeeCarsListAdapter(this, cars);
        carsListView.setAdapter(adapter);

        Call<List<Car>> call = apiService.getAllCars();

        final ProgressDialog progressDialog = getProgressDialog();

        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                List<Car> cars = response.body();
                progressDialog.dismiss();
                adapter.clear();
                adapter.addAll(cars);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Number of cars received: " + cars.size());
                Toast.makeText(getApplicationContext(), "Number of cars received: " + cars.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());

                Toast.makeText(getApplicationContext(), "No internet connection or server currently down, please retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private ProgressDialog getProgressDialog() {
        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(EmployeeActivity.this);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("waiting for cars");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        // show it
        progressDialog.show();
        return progressDialog;
    }
}
