package com.phyrelinx.cp.cmark;
//https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

public class ManageUserCustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;
    ArrayList<String> grouplist;
    Activity activity;
    ManageUserCustomAdapter adapter;
    String table;
    Singleton1 singleton1 = Singleton1.getInstance(getContext());

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtGroup;
        TextView txtDelete;
        TextView txtVersion;
        ImageView fingerenrol;
    }

    public ManageUserCustomAdapter(String table, ArrayList<DataModel> data, ArrayList<String> sft, Context context, Activity activity) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.grouplist = sft;
        this.table = table;
        adapter = this;
        this.activity=activity;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.viewdetailstxt:
                Toast.makeText(getContext(),"Yes ",Toast.LENGTH_SHORT).show();
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




                Glide.with(getContext())
                        .load(Constants.HTTP_URL_CHILDPHOTO+ids+".jpg")
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .circleCrop()
                                .placeholder(R.drawable.ic_person_black_24dp)
                                .skipMemoryCache(true))
                        .into(img);

                dialog.show();
                break;

            case R.id.name:
                Toast.makeText(getContext(),dataModel.getId(),Toast.LENGTH_SHORT).show();
                if (new Jasonparse(getContext()).getMode().equals(Constants.MODE_AF)) {
                    Intent intentedit = new Intent(getContext(), Qrcodedetect.class);
                    intentedit.putExtra("id", dataModel.getId());
                    intentedit.putExtra("phone", dataModel.getPhone());
                    intentedit.putExtra("serial", dataModel);
                    getContext().startActivity(intentedit);
                    activity.finish();
                }else {
                    Tag tag = Singleton1.getInstance(getContext()).getTagtochange();
                    //tag.setId(dataModel.getId());
                    String oldid = tag.getId();
                    //Set new tag id
                    tag.setId(dataModel.getId());
                    new Jasonparse(getContext()).updateAttendnance(dataModel.getId(),oldid, tag, true);
                    Intent intentedit = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(intentedit);
                    activity.finish();

                }



                break;

            case R.id.fingerenrolimg:

                break;

        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.manageuser_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtGroup = (TextView) convertView.findViewById(R.id.grouptxt);
            viewHolder.txtDelete = (TextView) convertView.findViewById(R.id.viewdetailstxt);

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

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtName.setOnClickListener(this);
        viewHolder.txtName.setTag(position);

        viewHolder.txtDelete.setOnClickListener(this);
        viewHolder.txtDelete.setTag(position);

        viewHolder.txtGroup.setText(dataModel.getPhone());
        viewHolder.txtGroup.setOnClickListener(this);
        viewHolder.txtGroup.setTag(position);
        viewHolder.fingerenrol.setOnClickListener(this);
        viewHolder.fingerenrol.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }


}
