package com.tm.exammobile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tm.exammobile.R;
import com.tm.exammobile.models.CarAddRequest;
import com.tm.exammobile.models.CarAddResponse;
import com.tm.exammobile.models.CarDeleteRequest;
import com.tm.exammobile.models.CarAddResponse;
import com.tm.exammobile.rest.ApiClient;
import com.tm.exammobile.rest.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class AddCarActivity extends AppCompatActivity {


    private static final String TAG = "AddCarActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_car_activity);

        final EditText etName = (EditText) findViewById(R.id.add_car_name);
        final EditText etType = (EditText) findViewById(R.id.add_car_type);

        Button submit = (Button) findViewById(R.id.submit_add);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable eName = etName.getText();
                Editable eType = etType.getText();
                if (eName != null && eType != null) {
                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);

                    CarAddRequest request = new CarAddRequest();
                    request.name = eName.toString();
                    request.type = eType.toString();

                    Call<CarAddResponse> call = apiService.addCar(request);
                    call.enqueue(new Callback<CarAddResponse>() {
                        @Override
                        public void onResponse(Call<CarAddResponse> call, Response<CarAddResponse> response) {
                            CarAddResponse responseBody = response.body();
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Added car" + responseBody.name, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Added deleted: " + responseBody.name);
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
                        public void onFailure(Call<CarAddResponse> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            }
        });
    }
}
