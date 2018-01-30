package com.tm.exammobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tm.exammobile.R;
import com.tm.exammobile.activities.MyCarsActivity;
import com.tm.exammobile.helpers.CarsHelper;
import com.tm.exammobile.models.Car;
import com.tm.exammobile.models.CarBuyResponse;
import com.tm.exammobile.models.CarDeleteRequest;
import com.tm.exammobile.models.CarDeleteResponse;
import com.tm.exammobile.rest.ApiClient;
import com.tm.exammobile.rest.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class EmployeeCarsListAdapter extends ArrayAdapter<Car> {

    private static final String TAG = "EmployeeAdapter";
    private Context context;

    public EmployeeCarsListAdapter(Context context, List<Car> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Car car = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.employee_car_list_item, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.employee_car_name);
        TextView tvType = (TextView) convertView.findViewById(R.id.employee_car_type);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.employee_car_status);
        TextView tvQuantity = (TextView) convertView.findViewById(R.id.employee_car_quantity);

        if (car != null) {
            tvName.setText(car.getName());
            tvType.setText(car.getType());
            tvStatus.setText(String.format(getContext().getString(R.string.status_s), car.getStatus()));
            tvQuantity.setText(String.format(getContext().getString(R.string.cars_available_d), car.getQuantity()));
        }

        Button deleteBtn = (Button) convertView.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car != null) {
                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);

                    CarDeleteRequest request = new CarDeleteRequest();
                    request.id = car.getId();

                    Call<CarDeleteResponse> call = apiService.deleteCar(request);
                    call.enqueue(new Callback<CarDeleteResponse>() {
                        @Override
                        public void onResponse(Call<CarDeleteResponse> call, Response<CarDeleteResponse> response) {
                            CarDeleteResponse responseBody = response.body();
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Deleted car" + responseBody.name, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Car deleted: " + responseBody.name);
                                remove(car);
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    String warning = jObjError.getString("text");
                                    Toast.makeText(context, "Warning: " + warning, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Warning: " + warning);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<CarDeleteResponse> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            }
        });

        return convertView;
    }
}
