package com.tm.exammobile.rest;

import com.tm.exammobile.models.Car;
import com.tm.exammobile.models.CarAddRequest;
import com.tm.exammobile.models.CarAddResponse;
import com.tm.exammobile.models.CarBuyRequest;
import com.tm.exammobile.models.CarBuyResponse;
import com.tm.exammobile.models.CarDeleteRequest;
import com.tm.exammobile.models.CarDeleteResponse;
import com.tm.exammobile.models.CarReturnRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Last edit by tudor.maier on 29/01/2018.
 */

public interface ApiInterface {

    @GET("cars")
    Call<List<Car>> getCars();

    @GET("all")
    Call<List<Car>> getAllCars();

    @POST("buyCar")
    Call<CarBuyResponse> buyCar(@Body CarBuyRequest request);

    @POST("returnCar")
    Call<CarBuyResponse> returnCar(@Body CarReturnRequest id);

    @POST("removeCar")
    Call<CarDeleteResponse> deleteCar(@Body CarDeleteRequest request);

    @POST("addCar")
    Call<CarAddResponse> addCar(@Body CarAddRequest request);
}
