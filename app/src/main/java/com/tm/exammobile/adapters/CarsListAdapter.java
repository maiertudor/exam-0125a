package com.tm.exammobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tm.exammobile.R;
import com.tm.exammobile.models.Car;

import java.util.List;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class CarsListAdapter extends ArrayAdapter<Car>{

    public CarsListAdapter(Context context, List<Car> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Car car = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_list_item, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvType = (TextView) convertView.findViewById(R.id.type);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.status);
        TextView tvQuantity = (TextView) convertView.findViewById(R.id.quantity);

        if (car != null) {
            tvName.setText(car.getName());
            tvType.setText(car.getType());
            tvStatus.setText(String.format(getContext().getString(R.string.status_s), car.getStatus()));
            tvQuantity.setText(String.format(getContext().getString(R.string.cars_available_d), car.getQuantity()));
        }

        return convertView;
    }
}
