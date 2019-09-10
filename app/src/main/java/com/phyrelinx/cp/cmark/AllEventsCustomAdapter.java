package com.phyrelinx.cp.cmark;
//https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AllEventsCustomAdapter extends ArrayAdapter<Event> implements View.OnClickListener{

    private ArrayList<Event> dataSet;
    Context mContext;
    AllEventsCustomAdapter adapter;
    Activity activity;
    Singleton1 singleton1;


    // View lookup cache
    private static class ViewHolder {
        TextView txtdate;
        TextView txtlen;
        TextView txtupload;

    }

    public AllEventsCustomAdapter(ArrayList<Event> data, Context context, Activity activity) {
        super(context, R.layout.alleventdapter_row_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.activity = activity;
        adapter = this;
        singleton1 = Singleton1.getInstance(getContext());

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final Event event=(Event) object;

        switch (v.getId())
        {

            case R.id.eventdatetxt:

//
                  break;
            case R.id.upload_students_txt:
                //System.out.println("Tunde gson "+event.getStudents());
                TrackAsyncAttendance async = new TrackAsyncAttendance(Constants.HTTP_URL_UPATTENDANCE,event.getStudents().toString(),event.getDay(),activity,getContext(),new AsynTaskCallback(){
                    @Override
                    public void processFinish(String str) {
                        final String bf =str;
                        if (bf.contains("success")){
                            new Jasonparse(getContext()).removeSEnt(event.getDay(),getContext());
                            //STatus for send
                            new Jasonparse(getContext()).getAllevent(dataSet,adapter);
                            Toast.makeText(getContext()," Uploaded", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();

                        }else {
                            Toast.makeText(getContext(),bf, Toast.LENGTH_SHORT).show();

                        }

                    }
                });
                async.execute();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.alleventdapter_row_item, parent, false);
            viewHolder.txtdate = (TextView) convertView.findViewById(R.id.eventdatetxt);
            viewHolder.txtlen = (TextView) convertView.findViewById(R.id.eventlen);
            viewHolder.txtupload= (TextView)convertView.findViewById(R.id.upload_students_txt);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtdate.setText(dataModel.getDay());
        viewHolder.txtdate.setOnClickListener(this);
        viewHolder.txtdate.setTag(position);

        viewHolder.txtupload.setOnClickListener(this);
        viewHolder.txtupload.setTag(position);

        viewHolder.txtlen.setText(dataModel.getLenofstudent());
        viewHolder.txtlen.setOnClickListener(this);
        viewHolder.txtlen.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}
