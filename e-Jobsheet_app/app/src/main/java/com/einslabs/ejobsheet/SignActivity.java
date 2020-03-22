package com.einslabs.ejobsheet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

public class SignActivity extends Activity {

    private Button mClearBtn;
    private Button mSaveBtn;
    private SignaturePad mSignaturePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_view);

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(SignActivity.this, "Tanda tangan dimulai", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveBtn.setEnabled(true);
                mClearBtn.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveBtn.setEnabled(false);
                mClearBtn.setEnabled(false);
            }
        });

        mClearBtn = (Button) findViewById(R.id.clear_button);
        mSaveBtn = (Button) findViewById(R.id.save_button);

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signBitmap = mSignaturePad.getTransparentSignatureBitmap();
                String str_signBitmap = BitmapToString(signBitmap);
                SharedPreferences sharedPreferences = getSharedPreferences("task", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("signImageBase64", str_signBitmap);
                editor.apply();
            }
        });
    }

    private static String BitmapToString (Bitmap bitmap) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte[] arr = baos.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }
}
