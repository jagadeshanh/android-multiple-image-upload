package com.fidelituscorp.reception;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fidelituscorp.reception.models.Department;
import com.fidelituscorp.reception.rest.Api;
import com.fidelituscorp.reception.rest.Client;
import com.fidelituscorp.reception.utils.RealPathUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Department> departments;
    List<MultipartBody.Part> parts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    private void uploadImage() {
        System.out.println("step1: first step is to show the intent to choose pictures");

        Intent openGalleryIntent = new Intent();
        openGalleryIntent.setType("*/*");
        openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openGalleryIntent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {

            ArrayList<String> files = new ArrayList<>();

            System.out.println("step2: Now the user has choose pictures");
            if (data.getClipData() != null) {
                System.out.println("step3: multiple pictures are chose");
                int count = data.getClipData().getItemCount();
                System.out.println(count);
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String filePath = getAbsolutePath(imageUri);
                    files.add(filePath);
                }
            } else if (data.getData() != null) {
                System.out.println("step3: One single picture is choose");
                Uri imageUri = data.getData();
                String filePath = getAbsolutePath(imageUri);
                files.add(filePath);
            }

            System.out.println("step4: Send pictures to server");
            // now that we have multiple files ready
            // let's call retrofit service and send images to server

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("esi_number", "ESI1234567890");
            builder.addFormDataPart("uan_number", "UAN1234567890");

            for (int i = 0; i < files.size(); i++) {
                File file = new File(files.get(i));
                builder.addFormDataPart("images[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            }

            MultipartBody requestBody = builder.build();
            Api api = Client.api();
            Call<ResponseBody> call = api.uploadImages(requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println("response received");
                    System.out.println(response.body());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("error received");
                    System.out.println(t.getMessage());
                    System.out.println(t.toString());
                }
            });
        } else {
            Toast t = Toast.makeText(this, "Sorry permission error", Toast.LENGTH_LONG);
            t.show();
        }
    }

    private String getAbsolutePath(Uri uri) {
        String path = null;
        path = RealPathUtils.getRealPathFromURI_API19(MainActivity.this, uri);
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 201 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            uploadImage();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        } else {
            uploadImage();
        }
    }


}
