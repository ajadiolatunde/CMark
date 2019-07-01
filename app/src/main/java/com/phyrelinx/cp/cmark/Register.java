package com.phyrelinx.cp.cmark;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
    Uri photofile;
    ImageView imVCature_pic;
    Button submit;
    EditText firstname,middlename ,lastname,useridedit,phoneEdit,dobEdit;
    Spinner gender,groupquad;
    File file;
    File filedir;
    String id ="fg";
    boolean ispicready = false;
    ArrayList<String> sft;
    Boolean editmode = false;
    DataModel model;
    Singleton1 singleton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        singleton1 = Singleton1.getInstance(getApplicationContext());
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                id = extras.getString("id");
                model=(DataModel) intent.getSerializableExtra("serial");
                System.out.println("Tunde serial "+model.toString());

                editmode = true;
                // TODO: Do something with the value of isNew.
            }else{
                editmode = false;
            }
        }else {
            System.out.println("Tunde extra is null "+editmode);

        }
        System.out.println("Tunde extra is "+editmode);
        useridedit = (EditText)findViewById(R.id.useriddd);
        useridedit.setText((singleton1.getUnit()+String.valueOf(System.currentTimeMillis())));
        useridedit.setEnabled(false);
        phoneEdit = (EditText)findViewById(R.id.phoneNumber);
        dobEdit = (EditText)findViewById(R.id.dobed);
        firstname = (EditText)findViewById(R.id.fname);
        middlename = (EditText)findViewById(R.id.mname);
        lastname = (EditText)findViewById(R.id.lname);
        gender = (Spinner)findViewById(R.id.gender_spinner);
         sft = new ArrayList<>();
        new Jasonparse(getBaseContext()).getListof(sft,Constants.GROUP);
        ArrayAdapter<String> sadapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,sft);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imVCature_pic =(ImageView)findViewById(R.id.imgv_pic);
        if (editmode){
            useridedit.setText(id);
            useridedit.setEnabled(false);
            firstname.setText(model.getFname());
            lastname.setText(model.getLname());
            middlename.setText(model.getMname());
            phoneEdit.setText(model.getPhone());
            dobEdit.setText(model.getDob());
            String[] sex = getResources().getStringArray(R.array.spinnergender);
//            for (String s:sex){
//                if (s.equals(model.getGender())){
//                    gender.setSelection(sex);
//                }
//            }


            filedir = new File(getFilesDir(), Constants.PHOTODIRR);

            File f = new File(filedir.toString(), id + ".jpg");


            Glide.with(Register.this)
                    .load(f.toString())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .into(imVCature_pic);
        }


        submit = (Button)findViewById(R.id.submitbtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editmode)ispicready = true;
                if (ispicready){
                    imVCature_pic.setEnabled(false);
                    boolean sready = false;
                    final String fn =firstname.getText().toString();
                    final String ln = lastname.getText().toString();
                    final String mn = middlename.getText().toString();
                    final String phone = phoneEdit.getText().toString();
                    final String dob = dobEdit.getText().toString();

                    final String  gen = (String) gender.getSelectedItem();

                    if (fn.length()<2){
                        firstname.setError("Firstname required");

                    }else if (ln.length()<3){
                        lastname.setError("lastname required");
                    } if (mn.length()<3){
                        middlename.setError("middlename required");
                    }  if (phone.length()<11){
                        phoneEdit.setError("Invalid phone number!");
                    }else {
                        sready = true;
                    }

                   if (sready) {

                       Toast.makeText(getBaseContext(),"Saved",Toast.LENGTH_SHORT).show();
                       new Jasonparse(getBaseContext()).register(id,fn,mn,ln,dob,gen,phone,singleton1.getStaff(),"0");
                       Intent intent = new Intent(Register.this,MainActivity.class);
                       startActivity(intent);
                       finish();


                   }

                }else{
                    Toast.makeText(getBaseContext(),"Get picture",Toast.LENGTH_SHORT).show();
                }
            }
        });


        useridedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (useridedit.getText().toString().matches("^0") )
                {
                    // Not allowed
                    Toast.makeText(getBaseContext(), "not allowed", Toast.LENGTH_LONG).show();
                    useridedit.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imVCature_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    id = useridedit.getText().toString();

                }catch (Exception io){
                    io.printStackTrace();
                    id ="fg";
                }
                System.out.println("Tunde id is "+id+"  len " +id.length());

                if (id.length()>0) {
                    boolean cantakeattendance  = new Jasonparse(getBaseContext()).canRegister(id);
                    if (editmode){
                        cantakeattendance = false;
                    }
                    if (!cantakeattendance){
                        filedir = new File(getFilesDir(), Constants.PHOTODIRR);
                        if (!filedir.exists())filedir.mkdir();
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

                        file = new File(filedir.toString(), id + ".jpg");
                        if (file.exists()){
                            System.out.println("Tunde file exist ");
                            if (file.delete())System.out.println("Tunde deleted succesfully");
                        };
                        if (file.exists()){
                            System.out.println("Tunde file still exist ");

                        }
                        //old sdk 23
                        // Uri photofile = Uri.fromFile(file);
                        //new sdk 24
                        photofile = FileProvider.getUriForFile(Register.this, BuildConfig.APPLICATION_ID + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photofile);
                        startActivityForResult(intent, 1);
                    }else {
                        useridedit.setError("** Duplicate id");
                    }
                }else {


                    useridedit.setError("Please enter a valid id");

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if request code is same we pass as argument in startActivityForResult
        if(requestCode==1){
            //create instance of File with same name we created before to get image from storage
            File file = new File(filedir.toString(), id+".jpg");
            //Crop the captured image using an other intent
            try {
                /*the user's device may not support cropping*/

                //cropCapturedImage(Uri.fromFile(file),Register.this);
                cropCapturedImage(photofile,Register.this);
                ispicready = true;
            }
            catch(ActivityNotFoundException aNFE){
                //display an error message if user device doesn't support
                String errorMessage = "Sorry - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        if(requestCode==2){
            //Create an instance of bundle and get the returned data
            System.out.println("Tunde  data "+data);
            if (data!=null && data.hasExtra("data")) {
                Bundle extras = data.getExtras();
                Uri uri = data.getData();

                System.out.println("Tunde  data " + extras);

                //get the cropped bitmap from extras
                Bitmap thePic = extras.getParcelable("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                //-----------------------------------------------------------------------------------------------------------
                File f = new File(filedir.toString(), id + "");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    ispicready = true;
                    useridedit.setEnabled(false);

                } catch (IOException io) {
                    io.printStackTrace();
                }

                //set image bitmap to image view
                //imVCature_pic.setImageBitmap(thePic);

                System.out.println("Tunde " + thePic.getConfig().toString());
            }

            Glide.with(Register.this)
                    .load(file.toString())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .into(imVCature_pic);

        }
    }

    public void cropCapturedImage(Uri picUri, Context context){
        //https://stackoverflow.com/questions/39313752/how-to-pick-image-for-crop-from-camera-or-gallery-in-android-7-0/39633047
        context.grantUriPermission("com.android.camera",picUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(picUri, "image/*");
        //Permission
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //set crop properties

        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //you must setup this
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 2);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Register.this, MainActivity.class);
        startActivity(intent);
        finish();

        // Otherwise defer to system default behavior.
        // super.onBackPressed();
    }


}
