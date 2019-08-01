package com.fidelituscorp.reception.rest;

import com.fidelituscorp.reception.models.Departments;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of ContactList
    */
    @Headers("Content-Type: application/json")
    @GET("/api/departments")
    Call<Departments> getDepartments();

    @POST("/api/images")
    Call<ResponseBody> uploadImages(@Body RequestBody form);
}
