package com.tm.exammobile.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tm.exammobile.ExamApplication;
import com.tm.exammobile.R;
import com.tm.exammobile.adapters.CarsListAdapter;
import com.tm.exammobile.listeners.ConnectionReceiver;
import com.tm.exammobile.models.Car;
import com.tm.exammobile.rest.ApiClient;
import com.tm.exammobile.rest.ApiInterface;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener {

    private static final String TAG = "ClientActivity";
    private WebSocketClient mWebSocketClient;
    private CarsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity);

        connectWebSocket();

//        CarsHelper helper = new CarsHelper(getApplicationContext());
//        SQLiteDatabase db = helper.getWritableDatabase();
//        helper.onUpgrade(db,0,0);

        Button buy_car_button = (Button) findViewById(R.id.buy_car_button);
        buy_car_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuyCarActivity.class);
                startActivity(intent);
            }
        });

        Button my_cars_btn = (Button) findViewById(R.id.my_cars_btn);
        my_cars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyCarsActivity.class));
            }
        });

        Button retry_btn = (Button) findViewById(R.id.retry_btn);
        if (isNetworkAvailable()) {
            retry_btn.setEnabled(true);
        } else {
            retry_btn.setEnabled(false);
        }

        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExamApplication.getInstance().setConnectionListener(this);
        ConnectionReceiver receiver = new ConnectionReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        ListView carsListView = (ListView) findViewById(R.id.cars_list_view);
        List<Car> cars = new ArrayList<>();
        adapter = new CarsListAdapter(this, cars);
        carsListView.setAdapter(adapter);

        Call<List<Car>> call = apiService.getCars();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionReceiver receiver = new ConnectionReceiver();
        unregisterReceiver(receiver);
    }

    @NonNull
    private ProgressDialog getProgressDialog() {
        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ClientActivity.this);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("waiting for cars");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        // show it
        progressDialog.show();
        return progressDialog;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Button retry_btn = (Button) findViewById(R.id.retry_btn);
        if (!isConnected) {
            retry_btn.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No internet connection or server currently down, please retry", Toast.LENGTH_SHORT).show();
        } else {
            retry_btn.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Connection acquired", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("http://192.168.0.150:4000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Received" + message);
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            Car car = new Car();
                            car.setId(jsonObject.getInt("id"));
                            car.setName(jsonObject.getString("name"));
                            car.setType(jsonObject.getString("type"));
                            car.setStatus(jsonObject.getString("status"));
                            car.setQuantity(jsonObject.getInt("quantity"));
                            adapter.add(car);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
