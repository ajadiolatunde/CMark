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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class EdituserCustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;
    ArrayList<String> grouplist;
    EdituserCustomAdapter adapter;
    String table;
    Activity activity;
    Singleton1 singleton1 = Singleton1.getInstance(getContext());

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtGroup;
        TextView txtDelete;
        TextView txtUpload;
    }

    public EdituserCustomAdapter(String table, ArrayList<DataModel> data, ArrayList<String> sft, Context context, Activity activity) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.grouplist = sft;
        this.table = table;
        this.activity = activity;
        adapter = this;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {

            case R.id.upload_txt:
                String data = new Gson().toJson(dataModel,DataModel.class);
                System.out.println("Tunde gson "+data);

                TrackAsync async = new TrackAsync(dataModel.getId(),Constants.HTTP_URL_DATA,dataModel.toServer(),activity,new AsynTaskCallback(){
                    @Override
                    public void processFinish(String str) {
                        final String bf =str;
                        if (bf.contains("success")){
                            Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                            new Jasonparse(getContext()).deleteAccount(dataModel.getId());
                            new Jasonparse(getContext()).loadDatatoList(Constants.REGISTER,dataSet);


                            adapter.notifyDataSetChanged();
                            File filedir = new File(getContext().getFilesDir(), Constants.PHOTODIRR);
                            File file  = new File(filedir.toString(), dataModel.getId() + ".jpg");
                            File file1  = new File(filedir.toString(), dataModel.getId());

                            if (file.exists())file.delete();
                            if (file1.exists())   file1.delete();
                            //STatus for send

                        }else {
                            Toast.makeText(getContext(),bf,Toast.LENGTH_SHORT).show();

                        }

                    }
                });
                async.execute();
                break;
            case R.id.deletetxt:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new Jasonparse(getContext()).deleteAccount(dataModel.getId());
                                new Jasonparse(getContext()).loadDatatoList(table,dataSet);
                                Toast.makeText(mContext,dataModel.getName()+" deleted", Toast.LENGTH_SHORT).show();

                                adapter.notifyDataSetChanged();

                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete "+dataModel.getId()+"?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
                break;

            case R.id.name:
                Toast.makeText(getContext(),dataModel.getId(),Toast.LENGTH_SHORT).show();
                Intent intentedit = new Intent(getContext(),Register.class);
                intentedit.putExtra("id",dataModel.getId());
                intentedit.putExtra("serial",dataModel);
                getContext().startActivity(intentedit);
                activity.finish();

//                Snackbar.make(v, dataModel.getId(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
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
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtDelete = (TextView) convertView.findViewById(R.id.deletetxt);
            viewHolder.txtUpload = (TextView) convertView.findViewById(R.id.upload_txt);



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

        viewHolder.txtUpload.setOnClickListener(this);
        viewHolder.txtUpload.setTag(position);



        // Return the completed view to render on screen
        return convertView;
    }


}
