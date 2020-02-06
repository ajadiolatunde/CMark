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
import android.widget.LinearLayout;
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
import java.util.ArrayList;

/**
 * Created by olatunde on 11/10/17.
 */

public class Qrcheckout extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

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
    ArrayList<Tag> tagsdueexit;
    int count=0;
    LinearLayout ll;



    Singleton1 singleton1;
    ImageButton imgbtn;
    Button savebtn;
    Boolean pic_avalable= false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcheckout);
        ll = (LinearLayout)findViewById(R.id.layouthorizontalout);

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

        savebtn = (Button)findViewById(R.id.saveBtn);

        imgview = (ImageView)findViewById(R.id.ttakepicture);

        savebtn.setVisibility(View.GONE);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //Remove from checkin table
                for (Tag tag:tagsdueexit) {
                    new Jasonparse(getBaseContext()).checkOut(tag.getTag_id());
                    tag.setTag_out(String.valueOf(System.currentTimeMillis()));
                    new Jasonparse(getBaseContext()).updateAttendnance(tag.getId(), tag, true);


                }

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

                        Intent intent1 = new Intent(Qrcheckout.this, MainActivity.class);
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
                    ll.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                                bcode = barcodes.valueAt(0).displayValue;

                            if (!bcode.equals(previous)) {
                                    if (bcode.startsWith("P")&& !parent.startsWith("P")) {
//                                        check if parent in checkin table
                                        ArrayList<String> listofChildtag = new Jasonparse(getBaseContext()).canParentCheckOutlist(bcode);
//                                        barcodeInfo2.setText(" :"+String.valueOf(listofChildtag.size()));

                                        if (listofChildtag.size()>0) {
                                            parent = bcode;
//                                            barcodeInfo.setText(parent);
                                            mp.start();
                                            previous = bcode;
                                            bcode = " ";

                                            tagsdueexit = new Jasonparse(getBaseContext()).getRealId(listofChildtag);
                                            System.out.println("Tunde size of keys to change"+tagsdueexit.size());
                                            for (Tag t:tagsdueexit) {
                                                addBtn(t);
                                            };

                                            count++;
                                            //obtain tag info
                                            Tag tag =tagsdueexit.get(0);

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

                                }
                                if (count==1){
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


    private void addBtn(final Tag tag){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Button myButton = new Button(this);
        myButton.setText(tag.getTag_id());
        myButton.setId(tagsdueexit.indexOf(tag));
        myButton.setPadding(1,2,1,2);
        myButton.setBackground(getResources().getDrawable(R.drawable.myfirst));


        if (tag.tag_id.startsWith("P")){
            myButton.setCompoundDrawables(getBaseContext().getResources().getDrawable(R.drawable.ic_person_black_24dp),null,null,null);

        }else{
            myButton.setTextColor(getResources().getColor(R.color.aluminum));

            myButton.setCompoundDrawables(getBaseContext().getResources().getDrawable(R.drawable.ic_child_friendly_black_24dp),null,null,null);

        }
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButton.setVisibility(View.GONE);
//                listofchild.remove(tagid);
            }
        });


        ll.addView(myButton, lp);




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
