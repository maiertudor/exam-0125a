package com.tm.exammobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tm.exammobile.R;
import com.tm.exammobile.activities.MyCarsActivity;
import com.tm.exammobile.helpers.CarsHelper;
import com.tm.exammobile.models.Car;
import com.tm.exammobile.models.CarBuyRequest;
import com.tm.exammobile.models.CarBuyResponse;
import com.tm.exammobile.models.CarReturnRequest;
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

public class CursorCarsListAdapter extends CursorAdapter{

    private Context context;

    private static final String TAG = "CursorCarsListAdapter";

    public CursorCarsListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_cars_list_item, parent, false);

        final int carId = cursor.getInt(0);

        Button return_car_btn = (Button) view.findViewById(R.id.return_car_btn);
        return_car_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnCar(carId);
            }
        });

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.my_car_name);
        TextView tvType = (TextView) view.findViewById(R.id.my_car_type);
        TextView tvQuantity = (TextView) view.findViewById(R.id.my_car_quantity);

        String name = cursor.getString(1);
        String type = cursor.getString(2);
        int quantity = cursor.getInt(4);

        tvName.setText(name);
        tvType.setText(type);
        tvQuantity.setText(String.format(context.getString(R.string.cars_bought), quantity));

    }

    private void returnCar(int id) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        CarReturnRequest request = new CarReturnRequest();
        request.id = id;
        Call<CarBuyResponse> call = apiService.returnCar(request);
        call.enqueue(new Callback<CarBuyResponse>() {
            @Override
            public void onResponse(Call<CarBuyResponse>call, Response<CarBuyResponse> response) {
                CarBuyResponse responseBody = response.body();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Returned car" + responseBody.name, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Car returned: " + responseBody.name);

                    CarsHelper helper = new CarsHelper(context);
                    if (helper.returnCar(responseBody.id)) {
                        Log.d(TAG, "Local storage updated");
                    }
                    changeCursor(helper.getData());

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
            public void onFailure(Call<CarBuyResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }
}
