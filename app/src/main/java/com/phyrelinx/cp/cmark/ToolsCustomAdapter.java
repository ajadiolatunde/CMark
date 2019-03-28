package com.phyrelinx.cp.cmark;
//https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Map;

public class ToolsCustomAdapter extends ArrayAdapter<Event> implements View.OnClickListener{

    private ArrayList<Event> dataSet;
    Context mContext;
    ToolsCustomAdapter adapter;
    Activity activity;
    Singleton1 singleton1;


    // View lookup cache
    private static class ViewHolder {
        TextView txtdate;
        TextView txtlen;

    }

    public ToolsCustomAdapter(ArrayList<Event> data, Context context, Activity activity) {
        super(context, R.layout.tooscustom_row_item, data);
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
        Event dataModel=(Event) object;

        switch (v.getId())
        {



//Details
            case R.id.eventdatetxt:
                StringBuffer gg = new StringBuffer();

                System.out.println("Tunde test upload ");
                ArrayList<String> arr = new Jasonparse(getContext()).getEventsStudentsDetails(dataModel.getStudents());


//                final String item1 = (String) parent.getItemAtPosition(0);
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View convertView = (View) inflater.inflate(R.layout.viewdetails, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.myDialog));
                alertDialog.setTitle("Details");
                alertDialog.setView(convertView);

                final AlertDialog dialog = alertDialog.create();

                final ListView listView = (ListView)convertView.findViewById(R.id.viewlistuser);
                DetailsCustomAdapter adapter = new DetailsCustomAdapter(arr,getContext());
                listView.setAdapter(adapter);

                dialog.show();

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
            convertView = inflater.inflate(R.layout.tooscustom_row_item, parent, false);
            viewHolder.txtdate = (TextView) convertView.findViewById(R.id.eventdatetxt);
            viewHolder.txtlen = (TextView) convertView.findViewById(R.id.eventlen);


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

        viewHolder.txtlen.setText(dataModel.getLenofstudent());
        viewHolder.txtlen.setOnClickListener(this);
        viewHolder.txtlen.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}
