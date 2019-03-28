package com.phyrelinx.cp.cmark;
//https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;

public class DetailsCustomAdapter extends ArrayAdapter<String> implements View.OnClickListener{

    private ArrayList<String> dataSet;
    Context mContext;
    ArrayList<String> grouplist;
    DetailsCustomAdapter adapter;
    Singleton1 singleton1 ;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txttime;
        TextView txttype;

    }

    public DetailsCustomAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.detailsrow_item, data);
        this.dataSet = data;
        this.mContext=context;
        adapter = this;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        String dataModel=(String) object;

        switch (v.getId())
        {


        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        singleton1 = Singleton1.getInstance(getContext());
        String  dataModel = getItem(position);
        String[] datas = dataModel.split(" ");
        System.out.println("Tunde data pro "+datas[0]);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.detailsrow_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.nametxtd);
            viewHolder.txttime = (TextView) convertView.findViewById(R.id.timetxtd);
            viewHolder.txttype = (TextView) convertView.findViewById(R.id.typed);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
//        String[] timeOnly = datas[2].split(" ");
        //1 Jan 2018 11:55:15

//        System.out.println("Tunde time test "+datas[2]);
        Timestamp timestamp = new Timestamp(Long.parseLong(datas[1]));
        System.out.println("Timestamp  "+timestamp.toLocaleString());
        String[] tt = timestamp.toLocaleString().split(" ");



        viewHolder.txtName.setText(singleton1.getNameidList(Integer.parseInt(datas[0])));
        viewHolder.txtName.setTag(position);

        if (tt.length==5) {
            viewHolder.txttime.setText(tt[3] + " " + tt[4]);
        }else {
            viewHolder.txttime.setText(tt[3]);

        }
       // viewHolder.txttime.setText(datas[1]);
        viewHolder.txttime.setTag(position);
        viewHolder.txttype.setTag(position);


        // Return the completed view to render on screen
        return convertView;
    }
}
