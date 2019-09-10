package com.phyrelinx.cp.cmark;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
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
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by olatunde on 11/10/17.
 */

public class Qrcodedetect extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    TextView barcodeInfo;
    TextView barcodeInfo2;
    SurfaceView cameraView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    int Count;
    private File mPhotoFile;
    ImageView imgview;
    Boolean first = true;
    EditText phoneedit;
    Boolean cd =true;
    String parent,child,realid,bcode;
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
        setContentView(R.layout.qrcodelayout);
        final Intent intent = getIntent();
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
        phoneedit = (EditText)findViewById(R.id.phoneCap);
        phoneedit.setText((!realid.equals(Constants.SKIP))?model.getPhone():" ");

        PackageManager packageManager = this.getPackageManager();
        //final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent captureImage = new Intent("android.media.action.IMAGE_CAPTURE");
        File filedir = new File(getFilesDir(), Constants.MAPPHOTODIRR);
        if (!filedir.exists())filedir.mkdir();


        mPhotoFile = singleton1.getPhotoFile(String.valueOf(tsLong));

        boolean canTakePicture =mPhotoFile != null && captureImage.resolveActivity(packageManager)!=null;

        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        barcodeInfo = (TextView)findViewById(R.id.code1_info);
        barcodeInfo2 = (TextView)findViewById(R.id.code2_info);
        savebtn = (Button)findViewById(R.id.saveBtn);

        imgview = (ImageView)findViewById(R.id.takepicture);


        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage,ARG_PHOTO_REQUEST);

            }
        });
        imgview.setEnabled(canTakePicture);
        savebtn.setVisibility(View.GONE);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkphone()){
                    if (pic_avalable) {
                        //use timestamp for non registered
                        String[] session = singleton1.getPrefKey(Constants.SESSION_TEACHER).split(" ");
                        tag = new Tag(String.valueOf(tsLong), child, (realid.equals(Constants.SKIP))?Whoyouutil.skipIdevice(tsLong,Qrcodedetect.this):realid, parent, Whoyouutil.getDeviceId(getBaseContext()), session[0], phoneedit.getText().toString(),session[1]);
                        String all = new Gson().toJson(tag,Tag.class);
                        if (new Jasonparse(getBaseContext()).canTakeAttendance((realid.equals(Constants.SKIP))?Whoyouutil.skipIdevice(tsLong,Qrcodedetect.this):realid)) {
                            new Jasonparse(getBaseContext()).checkIn(child, parent);
                            new Jasonparse(getBaseContext()).markAttendnance((realid.equals(Constants.SKIP))?Whoyouutil.skipIdevice(tsLong,Qrcodedetect.this):realid, all);
                            savebtn.setEnabled(false);
                            cameraSource.stop();
                            Intent intent1 = new Intent(Qrcodedetect.this,MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }else {
                            Toast.makeText(getBaseContext(),"You have marked attendance today!  " ,Toast.LENGTH_SHORT).show();

                        }
                        //System.out.println("Tunde ============== "+all);

                    }else {
                        Toast.makeText(getBaseContext(),"Get picture",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    phoneedit.setError("Required!");
                }
            }
        });


        if(canTakePicture){
           // Uri uri= Uri.fromFile(mPhotoFile);
            Uri photofile = FileProvider.getUriForFile(Qrcodedetect.this, BuildConfig.APPLICATION_ID + ".provider", mPhotoFile);

            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,photofile);

        }



        barcodeDetector = new BarcodeDetector.Builder(Qrcodedetect.this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();


        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                int camera = ContextCompat.checkSelfPermission(Qrcodedetect.this, android.Manifest.permission.CAMERA);
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
        cameraView.requestFocus();
        barcodeDetector.setProcessor(new Detector.Processor() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();


                if (barcodes.size() != 0) {
                    final MediaPlayer mp = MediaPlayer.create(Qrcodedetect.this, R.raw.ap);
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                                bcode = barcodes.valueAt(0).displayValue;
                                if (!bcode.equals(previous)) {
                                    if (bcode.startsWith("P")&& !parent.startsWith("P")) {
                                        parent = bcode;
                                        barcodeInfo.setText(parent);
                                        mp.start();
                                        previous = bcode;
                                        bcode = " ";
                                        count++;
                                    }
                                    if (bcode.startsWith("C") && !child.startsWith("C")) {
                                        //check if used
                                        mp.start();
                                        if (new Jasonparse(getBaseContext()).canUsechildTag(bcode)) {
                                            child = bcode;
                                            previous = bcode;
                                            mp.start();
                                            barcodeInfo2.setText(child);

                                            bcode = " ";
                                            count++;
                                        }else{
                                            Toast.makeText(getBaseContext(),"Tag is used already!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                if (count==2){
                                   // bcode = previous;

                                    barcodeDetector.release();
                                    //enable save
                                    savebtn.setVisibility(View.VISIBLE);

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
            //Toast.makeText(getBaseContext(), String.valueOf(mPhotoFile)+"  null....", Toast.LENGTH_LONG).show();

            int id = this.getResources().getIdentifier("drawable/dr", null,this.getPackageName());
            imgview.setImageResource(id);

        }else {
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            //mimageView.setImageBitmap(bitmap);
            pic_avalable = true;
           // Toast.makeText(getBaseContext(), String.valueOf(mPhotoFile), Toast.LENGTH_SHORT).show();

            Glide.with(Qrcodedetect.this)
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
        Intent intent=new Intent(Qrcodedetect.this, MainActivity.class);
        startActivity(intent);
        finish();

        // Otherwise defer to system default behavior.
        // super.onBackPressed();
    }
}
