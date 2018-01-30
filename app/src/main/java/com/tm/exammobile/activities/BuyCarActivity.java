package com.tm.exammobile.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tm.exammobile.R;
import com.tm.exammobile.helpers.CarsHelper;
import com.tm.exammobile.models.Car;
import com.tm.exammobile.models.CarBuyRequest;
import com.tm.exammobile.models.CarBuyResponse;
import com.tm.exammobile.rest.ApiClient;
import com.tm.exammobile.rest.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class BuyCarActivity extends AppCompatActivity{

    private static final String TAG = "BuyCarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_car_activity);

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Button buyCarBtn = (Button) findViewById(R.id.submit_buy);
        buyCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etId = (EditText) findViewById(R.id.name_input);
                EditText etQuantity = (EditText) findViewById(R.id.quantity_input);

                CarBuyRequest request = new CarBuyRequest();
                request.id = etId.getText().toString();
                request.quantity = Integer.valueOf(etQuantity.getText().toString());

                Call<CarBuyResponse> call = apiService.buyCar(request);
                call.enqueue(new Callback<CarBuyResponse>() {
                    @Override
                    public void onResponse(Call<CarBuyResponse>call, Response<CarBuyResponse> response) {
                        CarBuyResponse responseBody = response.body();
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Bought car" + responseBody.name, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Car bought: " + responseBody.name);

                            CarsHelper helper = new CarsHelper(getApplicationContext());
                            helper.addCar(new Car(responseBody.id, responseBody.name, responseBody.type, 1, responseBody.status));

                            Intent intent = new Intent(getApplicationContext(), MyCarsActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                String warning = jObjError.getString("text");
                                Toast.makeText(getApplicationContext(), "Warning: " + warning, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Warning: " + warning);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CarBuyResponse>call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });
            }
        });
    }
}
