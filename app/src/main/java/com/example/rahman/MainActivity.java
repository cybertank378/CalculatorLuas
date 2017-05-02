package com.example.rahman;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {


    public static final int PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    @InjectView(R.id.btnRecord)
    AppCompatButton mRecord;
    @InjectView(R.id.btnSubmit)
    AppCompatButton mSubmit;
    @InjectView(R.id.input_panjang)
    EditText mEdtPanjang;
    @InjectView(R.id.input_lebar)
    EditText mEdtLebar;
    @InjectView(R.id.txt_hasil)
    TextView mHasil;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        ButterKnife.inject(this);

        runOnUiThread(new Runnable() {
            public void run() {
                boolean result = checkPermission();
                if (!result) {
                    initClickListener();
                }
            }
        });


    }

    private void initClickListener() {
        mRecord.setOnClickListener(mRecordClickListener());
        mSubmit.setOnClickListener(mSubmitClickListener());
    }

    private View.OnClickListener mSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mEdtPanjang.getText().toString().length() == 0 ){
                    mEdtPanjang.setError( "Panjang Dimensi diperlukan!" );
                    mEdtPanjang.requestFocus();
                } else if (mEdtLebar.getText().toString().length() == 0){
                    mEdtLebar.setError( "Lebar Dimensi diperlukan!" );
                    mEdtLebar.requestFocus();
                } else if(mEdtPanjang.getText().toString().length() == 0 && mEdtLebar.getText().toString().length() == 0){
                    mEdtPanjang.setError( "Panjang Dimensi diperlukan!" );
                    mEdtLebar.setError( "Lebar Dimensi diperlukan!" );
                } else {
                    String mPanjang = mEdtPanjang.getText().toString();
                    String mLebar   = mEdtLebar.getText().toString();
                    double p = Double.parseDouble(mPanjang);
                    double l = Double.parseDouble(mLebar);
                    double mLuas = p * l;
                    String data = "Hasil luas dari " + p + " x " + l + " = " + mLuas;
                    mHasil.setText(String.valueOf(mLuas));
                    if (FileHelper.writeExternalStorage(mContext, data)) {
                        Toast.makeText(mContext, "Saved to file", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Error save file!!!", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        };
    }

    private View.OnClickListener mRecordClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHasil.setText(FileHelper.readExternalStorage());
                
            }
        };
    }

    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FileHelper.readExternalStorage();
                } else {
                    //code for deny
                }
                break;
        }
    }
}
