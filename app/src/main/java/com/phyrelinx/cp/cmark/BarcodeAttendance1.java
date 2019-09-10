package com.phyrelinx.cp.cmark;

//import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class BarcodeAttendance1 extends AppCompatActivity {
    SurfaceView cameraView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    ImageView imageView;
    TextView phonetxtc,phonetxtn,phonetxtg;
    Boolean first = false;
    String code,id;
    String previous=null;
    File filedir;
    Singleton1 singleton1;
    DataModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance1);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                id = extras.getString("id");
                if (!id.equals(Constants.SKIP))model=(DataModel) intent.getSerializableExtra("serial");

            }else{

            }
        }else {


        }
        previous="00";
        imageView =(ImageView)findViewById(R.id.imgview);
        phonetxtc = (TextView)findViewById(R.id.phonetxtcount);
        phonetxtn = (TextView)findViewById(R.id.phonetxtname);

        phonetxtg = (TextView)findViewById(R.id.phonetxtgroup);
        singleton1 = Singleton1.getInstance(getApplicationContext());

        cameraView = (SurfaceView)findViewById(R.id.srf);
        filedir = new File(getFilesDir(),Constants.PHOTODIRR);
        if (!filedir.exists())filedir.mkdir();

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
                int camera = ContextCompat.checkSelfPermission(BarcodeAttendance1.this, android.Manifest.permission.CAMERA);
                try {

                    if (camera == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Tunde permission  granted");

                        cameraSource.start(cameraView.getHolder());
                    }else{
                        Toast.makeText(getBaseContext(),"Permission not granted",Toast.LENGTH_SHORT).show();
                        System.out.println("Tunde permission not granted");
                    }
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("Tunde permission changed ");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                System.out.println("Tunde permission stopped");

                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor() {
            @Override
            public void release() {
                System.out.println("Tunde released");

            }

            @Override
            public void receiveDetections(Detector.Detections detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    phonetxtc.post(new Runnable() {    // Use the post method of the TextView
                                      public void run() {
                                          //Check if parent tag first=true
                                          System.out.println("Tunde runinig   scann    granted");

                                          code = barcodes.valueAt(0).displayValue;
                                          if(!previous.equals(code)) {

                                              if (new Jasonparse(getBaseContext()).canTakeAttendance(code)) {
                                                  String details =singleton1.userDetails;
                                                  String filename=" ";
                                                  try {
                                                      JSONObject det = new JSONObject(details);
                                                      String fn = det.getString("firstname");
                                                      String mn = det.getString("middle");
                                                      String ln = det.getString("lastname");
                                                      String grp = det.getString("group");
                                                      String loc = new Jasonparse(getBaseContext()).getGroupLocation(grp);


                                                      System.out.println("Tunde details  "+details);
                                                      phonetxtn.setText(fn+" "+mn+" "+ln);
                                                      phonetxtg.setText(grp);


                                                  }catch (JSONException io){
                                                      io.printStackTrace();
                                                  }
                                                  toneDisplay(getBaseContext(), code,details);

                                                  String time = String.valueOf(System.currentTimeMillis());
                                                  new Jasonparse(getBaseContext()).markAttendnance(code,"phyrelinx","qr",time,getBaseContext());
                                                  Glide.with(getBaseContext())
                                                          .load(singleton1.getPhoto(code+".jpg").toString())
                                                          .apply(new RequestOptions()
                                                                  .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                                  .placeholder(R.drawable.ic_person_black_24dp)
                                                                  .circleCrop()
                                                                  .skipMemoryCache(true))
                                                          .into(imageView);

                                                  String cut = singleton1.getCount();
                                                  phonetxtc.setText((cut.equals("0"))?"":cut);

                                              }else {
                                                  toneDisplayerror(getBaseContext(), code);
                                                  phonetxtn.setText("");
                                                  Glide.with(getBaseContext())
                                                          .load(singleton1.getPhoto(".jpg").toString())
                                                          .apply(new RequestOptions()
                                                                  .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                                  .placeholder(R.drawable.ic_person_black_24dp)
                                                                  .circleCrop()
                                                                  .skipMemoryCache(true))
                                                          .into(imageView);
                                              }

                                              previous = code;
                                          }


                                      }
                                  }
                    );
                }
            }
        });

    }
    private void toneDisplay(Context context, String cod,String name){
        //Toast.makeText(context, cod, Toast.LENGTH_SHORT).show();
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150);
    }

    private void toneDisplayerror(Context context, String cod){
        Toast.makeText(context, "Not registered!", Toast.LENGTH_SHORT).show();
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(BarcodeAttendance1.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }


}
