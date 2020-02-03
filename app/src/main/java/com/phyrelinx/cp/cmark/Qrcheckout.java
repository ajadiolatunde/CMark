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
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;

import java.io.File;
import java.io.IOException;

/**
 * Created by olatunde on 11/10/17.
 */

public class Qrcheckout extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    TextView barcodeInfo;
    TextView barcodeInfo2;
    SurfaceView cameraView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    int Count;
    private File mPhotoFile;
    ImageView imgview;
    Boolean first = true;
    TextView phoneedit;
    Boolean cd =true;
    String parent,child,realid,bcode,regChildtag;
    String time_uiid ="tunde";
    private static final int ARG_PHOTO_REQUEST =2;
    String previous =" ";
    Long tsLong;
    DataModel model;
    int count=0;


    Singleton1 singleton1;
    ImageButton imgbtn;
    Tag tag;
    Button savebtn;
    Boolean pic_avalable= false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcheckout);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        tsLong = System.currentTimeMillis();
        if (extras != null) {
            if (extras.containsKey("id")) {
                realid = extras.getString("id");
                if (!realid.equals(Constants.SKIP)) {
                    model = (DataModel) intent.getSerializableExtra("serial");
                    realid = model.getId();
                }

            }else{

            }
        }else {


        }

        singleton1 = Singleton1.getInstance(getApplicationContext());
        phoneedit = (TextView)findViewById(R.id.phoneCapout);
        phoneedit.setEnabled(false);

        //final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File filedir = new File(getFilesDir(), Constants.MAPPHOTODIRR);
        if (!filedir.exists())filedir.mkdir();
        for (File file:filedir.listFiles()){
            System.out.println("Tunde files ---------------- "+file.toString());

        }

        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        cameraView.requestFocus();

        barcodeInfo = (TextView)findViewById(R.id.code1_info);
        barcodeInfo2 = (TextView)findViewById(R.id.code2_info);
        savebtn = (Button)findViewById(R.id.saveBtn);

        imgview = (ImageView)findViewById(R.id.ttakepicture);

        savebtn.setVisibility(View.GONE);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //Remove from checkin table
                    new Jasonparse(getBaseContext()).checkOut(tag.getTag_id());
                    //update Attendance table
                    tag.setTag_out(String.valueOf(System.currentTimeMillis()));

                    new Jasonparse(getBaseContext()).updateAttendnance(tag.getId(), tag, true);
                    savebtn.setEnabled(false);
                    if (new Jasonparse(getBaseContext()).getMode().equals(Constants.MODE_AF)) {
                        if (mPhotoFile.exists())mPhotoFile.delete();
                        cameraSource.release();
                        cameraSource.stop();
                        Intent intent1 = new Intent(Qrcheckout.this, MainActivity.class);
                        startActivity(intent1);
                        finish();
                    }else{
                        if (mPhotoFile.exists())mPhotoFile.delete();
                        cameraSource.stop();

                        Intent intent1 = new Intent(Qrcheckout.this, ManageUser.class);
                        startActivity(intent1);
                        finish();
                    }

            }
        });

        barcodeDetector = new BarcodeDetector.Builder(Qrcheckout.this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();


        cameraSource = new CameraSource
                .Builder(Qrcheckout.this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                int camera = ContextCompat.checkSelfPermission(Qrcheckout.this, android.Manifest.permission.CAMERA);
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
        parent =" ";
        child =" ";
        regChildtag="NO";
        cameraView.requestFocus();


        barcodeDetector.setProcessor(new Detector.Processor() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();


                if (barcodes.size() != 0) {
                    final MediaPlayer mp = MediaPlayer.create(Qrcheckout.this, R.raw.ap);
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                                bcode = barcodes.valueAt(0).displayValue;

                            if (!bcode.equals(previous)) {
                                    if (bcode.startsWith("P")&& !parent.startsWith("P")) {
                                        String res = new Jasonparse(getBaseContext()).canParentCheckOut(bcode);
                                        if (!res.equals("0")) {
                                            parent = bcode;
                                            barcodeInfo.setText(parent);
                                            mp.start();
                                            previous = bcode;
                                            bcode = " ";
                                            regChildtag =res;

                                            String realchildid = new Jasonparse(getBaseContext()).getRealId(regChildtag);
                                            System.out.println("Tunde "+regChildtag+" "+realchildid+" "+res);

                                            count++;
                                            //obtain tag info
                                            tag =singleton1.getTagtochange();



                                                System.out.println("Tunde " + regChildtag + " " + realchildid + " " + tag.getTag_in());


                                                mPhotoFile = singleton1.getPhotoFile(String.valueOf(tag.getTag_in()));


                                                Glide.with(Qrcheckout.this)
                                                        .load(mPhotoFile + ".jpg")
                                                        .apply(new RequestOptions()
                                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                                .centerCrop()
                                                                .skipMemoryCache(true))
                                                        .into(imgview);
                                                savebtn.setVisibility(View.VISIBLE);
                                                phoneedit.setText(tag.getPhone());



                                        }else{
                                            Toast.makeText(getBaseContext(),"No records found!",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    previous = bcode;
                                    //Ensure p is loaded first
                                    if (bcode.startsWith("C") && !child.startsWith("C")&& parent.startsWith("P")) {
                                        //check if used
                                        mp.start();
                                        previous = bcode;
                                        if (bcode.equals(regChildtag)) {
                                            child = bcode;

                                            mp.start();
                                            barcodeInfo2.setText(child);

                                            bcode = " ";
                                            phoneedit.setText(tag.getPhone());

                                            count++;
                                            if (new Jasonparse(getBaseContext()).tagExistinToilet(child)){
                                                Toast.makeText(getBaseContext(), "Your child still in toilet", Toast.LENGTH_SHORT).show();

                                            }else {
                                                Toast.makeText(getBaseContext(), "Match found!", Toast.LENGTH_SHORT).show();
                                                savebtn.setVisibility(View.VISIBLE);
                                            }


                                        }else{
                                            Toast.makeText(getBaseContext(),"Wrong tag!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                if (count==2){
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
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == ARG_PHOTO_REQUEST ){
            System.out.println("Tunde   arg photo ready");
            PictureUtils.resize_compress(this, String.valueOf(tsLong));
            //phyLocation.setPhoto("Yes");
            upDatePhotoView();
        }
    }

    private  void upDatePhotoView(){

        if (mPhotoFile == null || !mPhotoFile.exists()){
            Toast.makeText(getBaseContext(), String.valueOf(mPhotoFile)+"  null....", Toast.LENGTH_LONG).show();

            int id = this.getResources().getIdentifier("drawable/dr", null,this.getPackageName());
            imgview.setImageResource(id);

        }else {
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            //mimageView.setImageBitmap(bitmap);
            pic_avalable = true;
            Toast.makeText(getBaseContext(), String.valueOf(mPhotoFile), Toast.LENGTH_SHORT).show();

            Glide.with(Qrcheckout.this)
                    .load(mPhotoFile+".jpg")
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .skipMemoryCache(true))
                    .into(imgview);
        }
    }

    private boolean checkphone(){
        boolean status = false;
        if (phoneedit.getText().length()>10 )status =true;
        return status;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Qrcheckout.this, MainActivity.class);
        startActivity(intent);
        finish();

        // Otherwise defer to system default behavior.
        // super.onBackPressed();
    }
}
