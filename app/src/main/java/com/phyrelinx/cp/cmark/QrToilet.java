package com.phyrelinx.cp.cmark;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * Created by olatunde on 11/10/17.
 */

public class QrToilet extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    TextView barcodeInfo;
    TextView barcodeInfo2;
    SurfaceView cameraView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    int Count;

    Boolean first = true;
    EditText phoneedit;
    Boolean cd =true;
    String child,realid,bcode;
    String time_uiid ="tunde";
    private static final int ARG_PHOTO_REQUEST =2;
    String previous =" ";
    Long tsLong;
    Switch modeSwitch;
    int count=0;
    Singleton1 singleton1;
    TextView textView_count;
    Tag tag;
    Button savebtn;
    Boolean out;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrtoilet);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        textView_count = (TextView)findViewById(R.id.qrcounttoilet_txt);

        singleton1 = Singleton1.getInstance(getApplicationContext());
        textView_count.setText(new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE));
        modeSwitch = (Switch)findViewById(R.id.toiletmode_switch);
        System.out.println("Tunde "+modeSwitch.isChecked());
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE).equals("0")){
                    modeSwitch.setChecked(false);

                }else {
                    modeSwitch.setChecked(true);
                    modeSwitch.setText("Out");
                    modeSwitch.setCompoundDrawablesWithIntrinsicBounds(getBaseContext().getResources().getDrawable(R.drawable.ic_navigate_before_black_24dp),null,null,null);
                }
                if (!isChecked){
                    modeSwitch.setText("In");
                    modeSwitch.setCompoundDrawablesWithIntrinsicBounds(getBaseContext().getResources().getDrawable(R.drawable.ic_navigate_next_black_24dp),null,null,null);


                }

                out=modeSwitch.isChecked();

                Toast.makeText(getBaseContext(), String.valueOf(out), Toast.LENGTH_SHORT).show();


            }
        });


        PackageManager packageManager = this.getPackageManager();

        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        barcodeInfo = (TextView)findViewById(R.id.barcodetoilet);
        barcodeDetector = new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();


        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                int camera = ContextCompat.checkSelfPermission(QrToilet.this, android.Manifest.permission.CAMERA);
                try {

                    if (camera == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    }else{
                        Toast.makeText(getBaseContext(),"Permission not granted", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        child =" ";
        cameraView.requestFocus();

        barcodeDetector.setProcessor(new Detector.Processor() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                out=modeSwitch.isChecked();
                if (barcodes.size() != 0) {
                    final MediaPlayer mp = MediaPlayer.create(QrToilet.this, R.raw.ap);
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                                bcode = barcodes.valueAt(0).displayValue;
                                if (!bcode.equals(previous)) {
                                    if (bcode.startsWith("C")) {
                                        child = bcode;
                                        //check if used
                                        mp.start();
                                        previous =bcode;
                                        //check if in checkingtable
                                        if (!new Jasonparse(getBaseContext()).canUsechildTag(child)) {


                                            //Check if already in toilet table
                                            if (!out) {
                                                if (!new Jasonparse(getBaseContext()).tagExistinToilet(child)) {
                                                    new Jasonparse(getBaseContext()).ToiletIn(child);
                                                    String realid = new Jasonparse(getBaseContext()).getRealId(child);
                                                    Tag tage = singleton1.getTagtochange();
                                                    tage.setToilet_in(String.valueOf(System.currentTimeMillis()));
                                                    new Jasonparse(getBaseContext()).updateAttendnance(realid, tage, false);
                                                    textView_count.setText(new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE));



                                                } else {
                                                    Toast.makeText(getBaseContext(), "Student already in toilet", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                if (new Jasonparse(getBaseContext()).tagExistinToilet(child)) {
                                                    String realid = new Jasonparse(getBaseContext()).getRealId(child);
                                                    Tag tage = singleton1.getTagtochange();
                                                    tage.setToilet_out(String.valueOf(System.currentTimeMillis()));
                                                    new Jasonparse(getBaseContext()).updateAttendnance(realid, tage, false);
                                                    new Jasonparse(getBaseContext()).TOiletOut(child);
                                                    textView_count.setText(new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE));



                                                }else{
                                                    Toast.makeText(getBaseContext(), "Wrong tag,Student not in toilet!", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                            //Check switch mode

                                            previous = bcode;
                                            bcode = " ";
                                            count++;
                                        }else{
                                            Toast.makeText(getBaseContext(),"Tag is not registered!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                if (count==10){
                                   // bcode = previous;

                                    barcodeDetector.release();
                                    //enable save

                                }

                        }
                    });
                }


            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(QrToilet.this, MainActivity.class);
        startActivity(intent);
        finish();

        // Otherwise defer to system default behavior.
        // super.onBackPressed();
    }
}
