package com.phyrelinx.cp.cmark;
//https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ViewAttendanceCustomAdapter extends ArrayAdapter<Tag> implements View.OnClickListener{

    private ArrayList<Tag> dataSet;
    Context mContext;
    ArrayList<String> grouplist;
    ViewAttendanceCustomAdapter adapter;
    Singleton1 singleton1 = Singleton1.getInstance(getContext());

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtUpload;
        TextView txtDelete;
        TextView txtPhone;
        ImageView fingerenrol;
    }

    public ViewAttendanceCustomAdapter( ArrayList<Tag> data, Context context) {
        super(context, R.layout.viewattendance_row_item, data);
        this.dataSet = data;
        this.mContext=context;
        adapter = this;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final Tag dataModel=(Tag) object;

        switch (v.getId())
        {
            case R.id.viewdetailstxt:
                Toast.makeText(getContext(),"Yes ",Toast.LENGTH_SHORT).show();
                System.out.println("Tunde press");
                final String ids = dataModel.getId();

//                final String item1 = (String) parent.getItemAtPosition(0);
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View convertView = (View) inflater.inflate(R.layout.manageuserdialog, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(ids);
                alertDialog.setView(convertView);

                final AlertDialog dialog = alertDialog.create();


                File filedir = new File(getContext().getFilesDir(), Constants.PHOTODIRR);
                File file  = new File(filedir.toString(), ids + ".jpg");

                ImageView img = (ImageView)convertView.findViewById(R.id.imgedit);
                TextView currentgrp = (TextView) convertView.findViewById(R.id.currenetgrptxt);
                currentgrp.setText(dataModel.getParent());
                File mPhotoFile = singleton1.getPhotoFile(dataModel.getTag_in());


                Glide.with(getContext())
//                        .load(Constants.HTTP_URL_CHILDPHOTO+ids+".jpg")
                        .load(mPhotoFile+".jpg")
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.ic_person_black_24dp)
//                                .circleCrop()
                                .skipMemoryCache(true))
                        .into(img);

                dialog.show();
                break;

            case R.id.name:
                Toast.makeText(getContext(),dataModel.getId(),Toast.LENGTH_SHORT).show();
                Intent intentedit = new Intent(getContext(),Qrcodedetect.class);
                intentedit.putExtra("id",dataModel.getId());
                intentedit.putExtra("serial",dataModel);

                break;

            case R.id.uploadtxt:
                Toast.makeText(getContext(),dataModel.getId(),Toast.LENGTH_SHORT).show();
                System.out.println("Tunde print attendace-----"+new Gson().toJson(dataModel));



                break;

        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Tag dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.viewuser_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtUpload = (TextView) convertView.findViewById(R.id.uploadtxt);
            viewHolder.txtDelete = (TextView) convertView.findViewById(R.id.viewdetailstxt);
            viewHolder.txtPhone = (TextView) convertView.findViewById(R.id.phoneEnroltxt);


            viewHolder.fingerenrol = (ImageView) convertView.findViewById(R.id.fingerenrolimg);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getTag_id());
        viewHolder.txtName.setOnClickListener(this);
        viewHolder.txtName.setTag(position);

        viewHolder.txtDelete.setOnClickListener(this);
        Timestamp ts = new Timestamp(Long.parseLong(dataModel.getTag_in()));
        viewHolder.txtDelete.setText(String.valueOf(ts.toLocaleString()));

        viewHolder.txtDelete.setTag(position);

        viewHolder.txtPhone.setTag(position);
        viewHolder.txtPhone.setText(dataModel.getPhone());

        viewHolder.txtUpload.setText(dataModel.getId());
        viewHolder.txtUpload.setOnClickListener(this);
        viewHolder.txtUpload.setTag(position);
        viewHolder.fingerenrol.setOnClickListener(this);
        viewHolder.fingerenrol.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }


}
