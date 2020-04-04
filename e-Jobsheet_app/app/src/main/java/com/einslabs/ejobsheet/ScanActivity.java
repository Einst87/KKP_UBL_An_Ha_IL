package com.einslabs.ejobsheet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    //public String apiUrl = getString(R.string.api_url);


    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent ( this, ProfileActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Log.v("TAG", rawResult.getText()); // Prints scan results
//        Log.v("TAG", rawResult.getBarcodeFormat().toString());
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
//        Intent i = new Intent(this, JobsheetActivity.class);
//        SharedPreferences sharedPreferences = getSharedPreferences("Task", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("SN", rawResult.getText());
//        editor.apply();
        //getSN(apiUrl, rawResult.getText());


        mScannerView.resumeCameraPreview(this);

//        startActivity(i);
//        finish();
    }

    private void getSN (String apiUrl, String SN) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String requestBody = "{\n" +
                " \"SN\": \"" + SN + " \" \n" +
                "}";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, requestBody);

        //start request ke server
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        //get response async
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getMsg("Tidak dapat menemukan data SN tersebut atau tidak ada koneksi internet");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                SharedPreferences sharedPreferences = getSharedPreferences("Task", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
                getMsg(response.body().toString());
            }
        });
    }

    private void getMsg (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
