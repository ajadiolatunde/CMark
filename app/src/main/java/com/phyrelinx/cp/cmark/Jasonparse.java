package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Jasonparse {
    Context context;
    Singleton1 singleton1;

    public Jasonparse(Context context){
        this.context = context;
        singleton1 = Singleton1.getInstance(context);

    }


    //called before mark attendance
    public boolean canTakeAttendance(String id){
        String data = singleton1.getPrefKey(Constants.REGISTER);
        boolean status = false;
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            status = false;
        }else{
            try{
                JSONObject body = new JSONObject(data);
                if (body.has(id)){
                    status = true;
                    singleton1.setUserDetails(body.get(id).toString());
                    System.out.println("Tunde userdetails "+body.get(id));
                }


            }catch (JSONException io){

            }
        }



        return status;
    }
    //Non event base called after if cantakeattednance is true
    public void markAttendnance(String id){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();
        System.out.println("Tunde today "+today);
        String time = String.valueOf(System.currentTimeMillis());
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            JSONObject body_fortoday= new JSONObject();
            try{
                body_fortoday.put(id,time);
                body.put(today,body_fortoday);
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,body.toString());
                Toast.makeText(context,"Done bye!  1",Toast.LENGTH_SHORT).show();

            }catch (JSONException io){
                io.printStackTrace();

            }
            //check if timestamp exist
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                if (oldbody.has(today)){
                    String js = oldbody.get(today).toString();
                    JSONObject today_body = new JSONObject(js);
                    today_body.put(id,time);
                    oldbody.put(today,today_body);
                    System.out.println("Tunde len "+today_body.names().toString());
                    singleton1.setCount(String.valueOf(today_body.names().length()));

                }else{
                    JSONObject today_body = new JSONObject();
                    today_body.put(id,time);
                    oldbody.put(today,today_body);
                    System.out.println("Tunde len "+today_body.names().toString());


                }
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                Toast.makeText(context,"Done bye!  2",Toast.LENGTH_SHORT).show();




            }catch (JSONException io){
                io.printStackTrace();
            }


        }



    }

    public ArrayList<String> getEventsStudentsDetails(JSONObject obj){
        ArrayList<String> ar = new ArrayList<>();
        JSONObject bd = obj;

        for (int x=0;x<bd.names().length();x++){
            try {
                String id = (String)bd.names().get(x);
                String time = bd.getString(id);
                ar.add(id+" "+time);

            }catch (JSONException io){
                io.printStackTrace();
            }

        }
        return ar;
    }


    public void getAllevent(ArrayList<Event> arrayList, ToolsCustomAdapter adapter){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        arrayList.clear();
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            System.out.println("Nothing ..");
        }else {
            try {
                JSONObject body = new JSONObject(data);
                int len = body.names().length();
                for(int i = 0; i<body.names().length(); i++){
                    String day = body.names().getString(i);

                    JSONObject studentsdata = new JSONObject(body.get(body.names().getString(i)).toString());
                    //value is the name key is time
                    String  lenofStudent = String.valueOf(studentsdata.names().length());

                    //Timestamp timestamp = new Timestamp(Long.parseLong(key));
                    //Event event = new Event(timestamp.toLocaleString(),key,value,lofStu);
                    Event event = new Event(day,studentsdata,lenofStudent);
                    arrayList.add(event);//add the hashmap into arrayList

                    //System.out.println( "Tunde  Time = " + timestamp.toLocaleString() + " value = " +timestamp.getTime()+"  "+timestamp.getDate() );
                }

                Collections.sort(arrayList, new Comparator<Event>(){
                    public int compare(Event one, Event two) {
                        return two.getDay().compareTo(one.getDay());
                    }
                });

                adapter.notifyDataSetChanged();
                System.out.println("array  size " +arrayList.size() );

            }catch (JSONException io){
                io.printStackTrace();
            }
        }

    }

    public ArrayList<NameId> buildNamefromid(){
        String data = singleton1.getPrefKey(Constants.REGISTER);
        System.out.println("Tunde test empty "+data);
        ArrayList<NameId> nameIdslist = new ArrayList<>();
        if (!data.equals("{}") || !data.equals(Constants.CLOSE)){
            try{
                JSONObject body = new JSONObject(data);
                if (body.toString().equals("{}")){

                }else{
                    for (int i=0;i<body.names().length();i++) {
                        String id = body.names().getString(i);
                        JSONObject conbondy = body.getJSONObject(id);
                        String fn = conbondy.getString("firstname");
                        String ln = conbondy.getString("lastname");
                        NameId nameId = new NameId(Integer.parseInt(id), fn + " " + ln);
                    nameIdslist.add(nameId);
                    }
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }


        return nameIdslist;
    }


    //User details
    public boolean register(String id,String fname,String mname,String lname,String gender){
        String data = singleton1.getPrefKey(Constants.REGISTER);
        boolean status = false;

        // First stage
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            JSONObject key_body = new JSONObject();

            try{

                key_body.put("firstname",fname);
                key_body.put("middle",mname);
                key_body.put("lastname",lname);
                key_body.put("gender",gender);
                body.put(id,key_body);
                singleton1.addStringSharedPreff(Constants.REGISTER,body.toString());
                Toast.makeText(context,"Done bye!  1",Toast.LENGTH_SHORT).show();
                status = true;

            }catch (JSONException io){
                io.printStackTrace();

            }
            //check if timestamp exist
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                JSONObject key_body = new JSONObject();
                boolean suc = true;
                if (suc){
                    key_body.put("firstname",fname);
                    key_body.put("middle",mname);
                    key_body.put("lastname",lname);
                    key_body.put("gender",gender);
                    oldbody.put(id,key_body);
                    singleton1.addStringSharedPreff(Constants.REGISTER,oldbody.toString());

                    Toast.makeText(context,"Done bye! 2",Toast.LENGTH_SHORT).show();
                    status = true;
                    System.out.println("Tunde done by 2 ");

                }else{
                    Toast.makeText(context,"Duplicate  please change id 2",Toast.LENGTH_SHORT).show();

                    System.out.println("Tunde it has 3");

                }

            }catch (JSONException io){
                io.printStackTrace();
            }


        }

        return status;


    }

    public void deleteAccount(String id){
        String regdata = singleton1.getPrefKey(Constants.REGISTER);
        try{
            JSONObject body = new JSONObject(regdata);
            body.remove(id);
            singleton1.addStringSharedPreff(Constants.REGISTER,body.toString());

        }catch (JSONException io){
            io.printStackTrace();

        }


    }

    public void loadDatatoList(ArrayList<DataModel> arrayList){
        arrayList.clear();
        String regdata = singleton1.getPrefKey(Constants.REGISTER);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);
                for(int i = 0 ;i<body.names().length();i++){
                    String id = (String)body.names().get(i);

                    JSONObject ct = body.getJSONObject(id);
                    String name = ct.getString("firstname")+" "+ct.getString("middle")+" "+ct.getString("lastname");
                    String gender =ct.getString("gender");
                    DataModel dataModel = new DataModel(ct.getString("firstname"),ct.getString("middle"),ct.getString("lastname"),id,gender);
                    arrayList.add(dataModel);
                    System.out.println("Tunde list "+id+" "+name);
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }



    public void getUserdetailsbyid(String id){
        String regdata = singleton1.getPrefKey(Constants.REGISTER);

    }

    public void modifyUsergroup(String id,String group){
        String regdata = singleton1.getPrefKey(Constants.REGISTER);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);

                JSONObject ct = body.getJSONObject(id);
                ct.put("group",group);
                body.put(id,ct);
                singleton1.addStringSharedPreff(Constants.REGISTER,body.toString());
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }

    public Map<String,Integer> attendaceReport(String today){
        System.out.println("Tunde today "+today);

        Map<String,Integer> hashMap = new HashMap<>();
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String regdata = singleton1.getPrefKey(Constants.REGISTER);

        if (data.equals("{}") || data.equals(Constants.CLOSE)){
           // hashMap = null;
        }else{
            try{
                JSONObject body = new JSONObject(data);
                JSONObject regbody = new JSONObject(regdata);


                if (body.has(today)){
                    String today_body = body.get(today).toString();
                    //student level
                    JSONObject tjs = new JSONObject(today_body);
                    System.out.println("Tunde stude "+tjs.toString());
                    JSONObject stud = tjs.getJSONObject("students");
                    System.out.println("Tunde stud array  "+stud.toString());

                    JSONArray array = stud.names();
                    System.out.println("Tunde array "+array);

                    for (int g =0;g<stud.names().length();g++){
                        String id = stud.names().get(g).toString();

                        String details = regbody.get(id).toString();

                        JSONObject det = new JSONObject(details);
                        String fn = det.getString("firstname");
                        String mn = det.getString("middle");
                        String ln = det.getString("lastname");
                        String grp = det.getString("group");

                        System.out.println("Tunde report "+id);
                        if (hashMap.containsKey(grp)){
                            hashMap.put(grp,hashMap.get(grp)+1);
                        }else{
                            hashMap.put(grp,1);
                        }
                    }


                }
                System.out.println("Tunde hasm "+hashMap.entrySet().toString());

            }catch (JSONException io){
                io.printStackTrace();
            }

        }


        return hashMap;


    }

    public String getGroupLocation(String grp){
        String data = singleton1.getPrefKey(Constants.GROUP);
        String loc = "N";
        try {
            JSONObject bd = new JSONObject(data);
            loc = bd.getString(grp);


        }catch (JSONException io){
            io.printStackTrace();
        }

        return loc;

    }

    public void addGroupLoc(String name,String table){
        String data = singleton1.getPrefKey(table);
        if (data.equals("{}")|| data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            try {
                body.put(name,Constants.CLOSE);
                singleton1.addStringSharedPreff(table,body.toString());
                Toast.makeText(context,"Added !",Toast.LENGTH_SHORT).show();


            }catch (JSONException io){
                io.printStackTrace();
            }


        }else{
            try {
                JSONObject body = new JSONObject(data);
                if (body.has(name)) {
                    Toast.makeText(context,"Failed,it Exist",Toast.LENGTH_SHORT).show();

                }else{
                    body.put(name,Constants.CLOSE);
                    singleton1.addStringSharedPreff(table,body.toString());
                    Toast.makeText(context,"Added !",Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }

    public void loadDatatoList(ArrayList<String> arrayList,String table){
        arrayList.clear();
        String regdata = singleton1.getPrefKey(table);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);
                for(int i = 0 ;i<body.names().length();i++){
                    String name = (String)body.names().get(i);

                    String loc= body.getString(name);
                    arrayList.add(name.toUpperCase()+" "+loc);
                    System.out.println("Tunde list "+name+" "+loc);
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }
    public String getgroupvalue(String grp){
        String grpdata = singleton1.getPrefKey(Constants.GROUP);
        String res = " ";

        try {
            JSONObject gbody = new JSONObject(grpdata);
            res = gbody.getString(grp);

        }catch (JSONException io){
            io.printStackTrace();
        }
        return res;
    }

    public void modifyTable(String grp,String gloc,String loc,String locg){
        String grpdata = singleton1.getPrefKey(Constants.GROUP);
        String locdata = singleton1.getPrefKey(Constants.LOCATION);
        try {
            JSONObject gbody = new JSONObject(grpdata);
            JSONObject lbody = new JSONObject(locdata);
            gbody.put(grp,gloc);
            lbody.put(loc,locg);

            singleton1.addStringSharedPreff(Constants.LOCATION,lbody.toString());
            singleton1.addStringSharedPreff(Constants.GROUP,gbody.toString());

        }catch (JSONException io){
            io.printStackTrace();
        }


    }

    public void getListof(ArrayList<String> arrayList,String table){
        arrayList.clear();
        String regdata = singleton1.getPrefKey(table);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);
                for(int i = 0 ;i<body.names().length();i++){
                    String name = (String)body.names().get(i);
                    String value = body.getString(name);

                    arrayList.add(name);
                    System.out.println("Tunde list "+name+" "+value);
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }

    public void getListof(ArrayList<String> arrayList,String table,Boolean filter){
        arrayList.clear();
        String regdata = singleton1.getPrefKey(table);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);
                for(int i = 0 ;i<body.names().length();i++){
                    String name = (String)body.names().get(i);
                    String value = body.getString(name);
                    if (value.equals(Constants.CLOSE)) arrayList.add(name);
                    System.out.println("Tunde list "+name+" "+value);
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }



    //obtain current session js
    public  String currentSessiontimeStamp(){
        String data  = singleton1.getPrefKey(Constants.SESSION);


        String cs = String.valueOf(System.currentTimeMillis());;

        try {
            JSONObject jsonObject = new JSONObject(data);
            cs = jsonObject.get(Constants.OPENTIME).toString();
        }catch (JSONException io){
            io.printStackTrace();
        }

        return cs;
    }

    public  String currentSessionkey(){
        String data  = singleton1.getPrefKey(Constants.SESSION);


        String cs = String.valueOf(System.currentTimeMillis());;

        try {
            JSONObject jsonObject = new JSONObject(data);
            cs = jsonObject.get(Constants.SESSION_KEY).toString();
        }catch (JSONException io){
            io.printStackTrace();
        }

        return cs;
    }

    //obtain current session js
    public  String currentSessionEvent(){
        String data  = singleton1.getPrefKey(Constants.SESSION);
        String cs = Constants.CLOSE;
        try {
            JSONObject jsonObject = new JSONObject(data);
            cs = jsonObject.get(Constants.EVENTNAME).toString();
        }catch (JSONException io){
            io.printStackTrace();
        }
        return cs;
    }

    //To select from list to push
    public String getthisEventbyTime(String j,Context ct){
        String res ="N";
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        try {
            JSONObject bd = new JSONObject(data);
            String names =bd.get(j).toString();
            JSONObject tosend = new JSONObject(names);
            String android_id = Settings.Secure.getString(ct.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            tosend.put("optime",j);
            tosend.put("id",android_id);
            res=tosend.toString();


        }catch (JSONException ik){
            ik.printStackTrace();
        }
        return res;
    }


    public  void markAttendnance(String matric,String staff,String type,String time,Context context){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        // First stage
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            JSONObject ct = new JSONObject();
            JSONObject cstu = new JSONObject();
            JSONObject ctype = new JSONObject();

            try{
                cstu.put(matric,time);
                ct.put("students",cstu);
                ctype.put(matric,type);
                ct.put("type",ctype);
                ct.put("staff",staff);
                ct.put(Constants.SESSION_KEY,currentSessionkey());

                ct.put(Constants.EVENTNAME,currentSessionEvent());
                body.put(currentSessiontimeStamp(),ct);
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,body.toString());
                singleton1.addStringSharedPreff(Constants.COUNT,"1");
                Toast.makeText(context,"Done bye!  1",Toast.LENGTH_SHORT).show();
            }catch (JSONException io){

            }
            //check if timestamp exist
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                if (oldbody.has(currentSessiontimeStamp())){
                    System.out.println("Tunde it has 2");
                    System.out.println("Tunde it has 2 "+oldbody.toString());
                    String currentSes = currentSessiontimeStamp();
                    JSONObject cujs = new JSONObject(oldbody.get(currentSes).toString());
                    System.out.println("Tunde it has key 2 "+cujs);



                    JSONObject cstu = new JSONObject(cujs.get("students").toString());
                    System.out.println("Tunde it has 2 stu "+cstu.toString());

                    cstu.put(matric,time);
                    System.out.println("Tunde it has 2 put student len "+cstu.length());
                    singleton1.addStringSharedPreff(Constants.COUNT,String.valueOf(cstu.length()));



                    cujs.put("students",cstu);
                    // System.out.println("Tunde it has 2 put cujs  "+cujs.toString());

                    JSONObject ctyp = new JSONObject(cujs.get("type").toString());
                    ctyp.put(matric,type);
                    cujs.put("type",ctyp);

                    oldbody.put(currentSessiontimeStamp(),cujs);
                    //System.out.println("Tunde it has 2 put body  "+oldbody.toString());


                    singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                    Toast.makeText(context,"Done bye! 2",Toast.LENGTH_SHORT).show();


                }else{
                    JSONObject cstu = new JSONObject();
                    System.out.println("Tunde it has 3");

                    cstu.put(matric,time);

                    JSONObject ctypee = new JSONObject();
                    ctypee.put(matric,type);

                    JSONObject ct = new JSONObject();
                    ct.put("students",cstu);
                    ct.put("staff",staff);
                    ct.put("type",ctypee);
                    ct.put(Constants.SESSION_KEY,currentSessionkey());
                    ct.put(Constants.EVENTNAME,currentSessionEvent());
                    oldbody.put(currentSessiontimeStamp(),ct);
                    singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                    singleton1.addStringSharedPreff(Constants.COUNT,"1");

                    Toast.makeText(context,"Done bye! 3",Toast.LENGTH_SHORT).show();


                }

            }catch (JSONException io){
                io.printStackTrace();
            }


        }



    }

    public void removeAadd(String j,Context ct){
        //singleton1.addStringSharedPreff(Constants.UPTABLE_ATTENDANCE,Constants.CLOSE);

        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String archives = singleton1.getPrefKey(Constants.UPATTENDANCE);

        try {
            JSONObject bd = new JSONObject(data);
            String names =bd.get(j).toString();
            System.out.println("TUNDE name to reomve  "+j+","+names);
            JSONObject tosend = new JSONObject(names);
            String android_id = Settings.Secure.getString(ct.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            tosend.put("optime",j);
            tosend.put("id",android_id);
            //test if that key is available
            if (bd.has(j)){
                if (archives.equals("{}")|| archives.equals(Constants.CLOSE)){
                    JSONObject arbd = new JSONObject();
                    arbd.put(j,tosend.toString());
                    singleton1.addStringSharedPreff(Constants.UPATTENDANCE,arbd.toString());
                    System.out.println("TUNDE len 1"+arbd.names().length());


                }else {
                    JSONObject arbd = new JSONObject(archives);
                    arbd.put(j,tosend.toString());
                    singleton1.addStringSharedPreff(Constants.UPATTENDANCE,arbd.toString());
                    System.out.println("TUNDE len 2 "+arbd.names().length());
                    System.out.println("TUNDE len 2 "+arbd.names().toString());

                }
                bd.remove(j);
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,bd.toString());

            }else{
                System.out.println("TUNDE cant find to remove ");
            }




        }catch (JSONException ik){
            ik.printStackTrace();
        }

    }
    //response from upload

    public String getResponseTypes(String rjs){
        String res = "Error";
        try {
            JSONObject js = new JSONObject(rjs);
            res = js.getString(Constants.MESSAGE);
        }catch (JSONException je){
            je.printStackTrace();
        }
        return res;
    }

//    public void getAllevent(ArrayList<Event> arrayList, ToolsCustomAdapter adapter){
//        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
//        arrayList.clear();
//        if (data.equals("{}") || data.equals(Constants.CLOSE)){
//            System.out.println("Nothing ..");
//        }else {
//            try {
//                JSONObject body = new JSONObject(data);
//                int len = body.names().length();
//                for(int i = 0; i<body.names().length(); i++){
//
//                    JSONObject vl = new JSONObject(body.get(body.names().getString(i)).toString());
//                    //value is the name key is time
//                    String value =vl.getString(Constants.EVENTNAME);
//                    JSONObject lenofStudent = vl.getJSONObject("students");
//                    String lofStu = String.valueOf(lenofStudent.names().length());
//                    String key  = body.names().getString(i);
//
//                    Timestamp timestamp = new Timestamp(Long.parseLong(key));
//                    Event event = new Event(timestamp.toLocaleString(),key,value,lofStu);
//                    arrayList.add(event);//add the hashmap into arrayList
//
//                    //System.out.println( "Tunde  Time = " + timestamp.toLocaleString() + " value = " +timestamp.getTime()+"  "+timestamp.getDate() );
//                }
//
//                Collections.sort(arrayList, new Comparator<Event>(){
//                    public int compare(Event one, Event two) {
//                        return two.getKey().compareTo(one.getKey());
//                    }
//                });
//
//                adapter.notifyDataSetChanged();
//                System.out.println("array  size " +arrayList.size() );
//
//            }catch (JSONException io){
//                io.printStackTrace();
//            }
//        }
//
//    }


    //load fingers

    public ArrayList<String> timeUserDetails(String gf){
        String g = gf;
        ArrayList<String> al = new ArrayList<>();
        System.out.println("Tunde details ---- "+g);

        try{
            JSONObject bd = new JSONObject(g);
            JSONObject st = bd.getJSONObject("students");
            JSONObject ty = bd.getJSONObject("type");


            for (int i=0;i<st.names().length();i++){
                String id = st.names().getString(i);
                String  timeIn = st.getString(id);

                Timestamp ts = new Timestamp(Long.parseLong(timeIn));
                String type = ty.getString(id);
                String all = id+"_"+type+"_"+ts.toLocaleString();

                al.add(all);

            }



        }catch (JSONException io){
            io.printStackTrace();
        }
        return al;
    }



    //restore reg file




}
