package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeAttendance extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    ImageView imageView;
    TextView phonetxtc,phonetxtn,phonetxtg,phonetxtloc;
    Singleton1 singleton1;
    Boolean first = false;
    String code;
    String previous=null;
    File filedir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode);

    }

    @Override
    public void handleResult(Result rawResult) {


        code = rawResult.getText();
        System.out.println("Tunde details -------- "+code);


        if(!previous.equals(code)) {

            if (new Jasonparse(getBaseContext()).canTakeAttendance(code)) {
                String details =singleton1.userDetails;
                String filename=" ";
                try {
                    JSONObject det = new JSONObject(details);
                    final String fn = det.getString("firstname").toUpperCase();
                    final String mn = det.getString("middle").toUpperCase();
                    final String ln = det.getString("lastname").toUpperCase();
                    final String grp = det.getString("group").toLowerCase();
                    final String loc = new Jasonparse(getBaseContext()).getgroupvalue(grp);
                    final  String allgroup = singleton1.getPrefKey(Constants.GROUP);

                    System.out.println("Tunde group  "+grp +" "+loc+" "+allgroup);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            phonetxtn.setText(fn+" "+mn+" "+ln);
                            phonetxtg.setText(grp);
                            phonetxtloc.setText(loc);
                        }
                    });




                }catch (JSONException io){
                    io.printStackTrace();
                    System.out.println("Tunde details error  "+details);

                }
                toneDisplay(getBaseContext(), code,details);

                String time = String.valueOf(System.currentTimeMillis());
                new Jasonparse(getBaseContext()).markAttendnance(code,"phyrelinx","qr",time,getBaseContext());
                Glide.with(getBaseContext())
                        .load(singleton1.getPhoto(code+".jpg").toString())
//                                                          .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                                          .skipMemoryCache(true)
//                                                          .centerCrop()
                        //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .into(imageView);

                String cut = singleton1.getCount();
                phonetxtc.setText((cut.equals("0"))?"":cut);

            }else {
                toneDisplayerror(getBaseContext(), code);
                phonetxtn.setText("");
                Glide.with(getBaseContext())
                        .load(singleton1.getPhoto(".jpg").toString())
                        .into(imageView);
            }

            previous = code;
        }

        // If you would like to resume scanning, call this method below:<br />
        mScannerView.resumeCameraPreview(this);
    }

    public void QrScanner(View view) {
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.<br />
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
    @Override
    public void onResume() {
        super.onResume();
        //mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />
        setContentView(R.layout.barcode);
        mScannerView = (ZXingScannerView)findViewById(R.id.scanner);
        previous="00";
        imageView =(ImageView)findViewById(R.id.imgview);
        phonetxtc = (TextView)findViewById(R.id.phonetxtcount);
        phonetxtn = (TextView)findViewById(R.id.phonetxtname);
        phonetxtloc = (TextView)findViewById(R.id.phonetxtloc);

        phonetxtg = (TextView)findViewById(R.id.phonetxtgroup);
        filedir = new File(getFilesDir(),Constants.PHOTODIRR);
        if (!filedir.exists())filedir.mkdir();
        singleton1 = Singleton1.getInstance(getApplicationContext());
        mScannerView = (ZXingScannerView)findViewById(R.id.scanner);
       // mScannerView.setAspectTolerance(0.5f);
        //setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(false);

        mScannerView.startCamera();
    }

    private void toneDisplay(Context context, String cod, String name){
        Toast.makeText(context, cod, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(BarcodeAttendance.this,MainActivity.class);
        startActivity(intent);
        this.finish();



    }
}