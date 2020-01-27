package com.phyrelinx.cp.cmark;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.View;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

class Singleton1 {
    SharedPreferences mSharedPrefrence;
    private Context context;
    private static  Singleton1 ourInstance ;
    private String studenttable;
    boolean eventkey = false;
    String eventkeytext ="";
    String proStatus = "0";
    private boolean loginstatus =false;
    String userDetails ="";
    String count="0";
    String id;
    Tag tagtochange;
    String fnameforscan,lnameforscan;
    ArrayList<NameId> nameidList;
    ArrayList<DataModel> dataModelArrayList;
    Boolean datalist = false;
    String staff,pass,unitId;
    Boolean canedit =false;
    JSONObject jsonObject;

    String server,port,organisation,pin,unit,staffname;



    public static Singleton1 getInstance(Context context) {
        if (ourInstance == null){

            ourInstance = new Singleton1(context);
        }
        return ourInstance;
    }

    private Singleton1(Context mContext) {
        this.context = mContext;
    }

    public void setmSharedPrefrence(){
        //https://stackoverflow.com/questions/30806342/pendingintent-cause-error
        //mSharedPrefrence = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPrefrence = context.getSharedPreferences(Constants.MYPREF, Activity.MODE_PRIVATE);
    }


    public boolean isPrefCreated(String name){
        mSharedPrefrence = context.getSharedPreferences(Constants.MYPREF, Activity.MODE_PRIVATE);
        boolean created = (mSharedPrefrence.contains(name))? true:false;
        return created;
    }


    public void addStringSharedPreff(String key, String value){
        if (isPrefCreated(key)){
            SharedPreferences.Editor editor = mSharedPrefrence.edit();
            editor.putString(key,value);
            editor.commit();

        }else {
            //setmSharedPrefrence();
            SharedPreferences.Editor editor = mSharedPrefrence.edit();
            editor.putString("test","init");
            editor.putString(key,value);
            editor.commit();
        }

    }

    public void getAllpref(){
        System.out.println("Tunde...prefkeys   "+mSharedPrefrence.getAll().toString());
    }

    public String getPrefKey(String key){
        //setmSharedPrefrence();
        String value = mSharedPrefrence.getString(key,"{}");
        return value;
    }

    public void setStudentTable(String js){
        this.studenttable = js;
    }

    public String getStudenttable(){
        return studenttable;
    }
    public File getPhoto(String id){
        File filedir = new File(context.getFilesDir(), Constants.PHOTODIRR);

        File f = new File(filedir.toString(),id);
        return f;
    }

    public  void setCount(String count){
        this.count = count;
    }

    public String getCount(){
        return  count;
    }
   public void setUserDetails(String detailsJson){
        this.userDetails = detailsJson;
   }
    //THis must be converted to js
    public String getUserDetails() {
        return userDetails;
    }
    //check if session exist

    public boolean sessionExist(){
        String an = this.getPrefKey(Constants.SESSION);
        return  (an.equals(Constants.CLOSE) || an.equals("{}"))?false:true;
    }

    public int sessionExist(String st){
        String an = this.getPrefKey(Constants.SESSION);
        return  (an.equals(Constants.CLOSE) || an.equals("{}"))?View.GONE:View.VISIBLE;

    }


    public String getFnameforscan() {
        return fnameforscan;
    }

    public void setFnameforscan(String fnameforscan) {
        this.fnameforscan = fnameforscan;
    }

    public String getLnameforscan() {
        return lnameforscan;
    }

    public void setLnameforscan(String lnameforscan) {
        this.lnameforscan = lnameforscan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void closeSessino(){
        this.addStringSharedPreff(Constants.SESSION,Constants.CLOSE);

    }

    public String getEventkeytext(){
        return eventkeytext;
    }

    public boolean istimeset(){
        return eventkey;
    }
    public void setEventkey(boolean event,String k){
        if (k.contains(":00")){
            eventkeytext = CARUtil.getEventKey(k);
            eventkey = event;
        }else {
            eventkey = false;
        }

    }



    public String getServer() {
        return server;
    }

    public void setServer() {
        String an = this.getPrefKey(Constants.SERVER);
        if (an.equals("{}")){
            this.server = Constants.HTTP_SERVER;
        }else {

            this.server = an;
        }
    } public String getPin() {
        String an = this.getPrefKey(Constants.DEFAULTPINTABLE);
        if (an.equals("{}")){
            this.pin = Constants.DEFAULTPIN;

        }else {

            this.pin = an;

        }

        return pin;
    }

    public void setPin(String pin) {
        this.addStringSharedPreff(Constants.DEFAULTPINTABLE,pin);

    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public String getPort() {
        return port;
    }

    public void setPort() {
        String an = this.getPrefKey(Constants.PORT);

        if (an.equals("{}")){
            this.port = Constants.HTTP_PORT;

        }else {

            this.port = an;

        }
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation() {
        String an = this.getPrefKey(Constants.ORGANISATION);
        if (an.equals("{}")){
            this.organisation = Constants.ORGANISATION;
        }else {

            this.organisation = an;
        }
    }

    public Tag getTagtochange() {
        return tagtochange;
    }

    public void setTagtochange(Tag tagtochange) {
        this.tagtochange = tagtochange;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    //Get name of id from list


    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    public void setCanEdit(boolean status){
        canedit = status;

    }

    public boolean getCanEdit(){
        return  canedit;

    }
    //Get name of id from list

    public String getNameidList(String id) {
        String name ="Deleted! ";
        for(int i=0;i<nameidList.size();i++){
            NameId nid = nameidList.get(i);
            if (nid.getId().equals(id)){
                name = nid.getName();
                break;
            }
        }
        return name;
    }

    public void setNameidList() {
        this.nameidList = new Jasonparse(context).buildNamefromid();
    }

    public File getPhotoFile(String name){
        File externalFileDir = new File(context.getFilesDir(),Constants.MAPPHOTODIRR);
        if (!externalFileDir.exists())externalFileDir.mkdir();
//        if(externalFileDir == null){
//            return null;
//        }
        return  new File(externalFileDir.toString(),name);
    }
//  Retrive todays attendance
    public JSONObject getJsonObject() {
        /**
         * @param
         * @return todays attendace
         */
        return jsonObject;
    }
//    Load todays attendance
    public void setJsonObject(JSONObject jsonObject) {
        /**
         * @param todays attendance
         */
        this.jsonObject = jsonObject;
    }

    public ArrayList<DataModel> getDataModelArrayList() {
        return dataModelArrayList;
    }



    public void setDataModelArrayList(ArrayList<DataModel> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
    }


    public Boolean getDatalist() {
        return datalist;
    }

    public void setDatalist(Boolean datalist) {

        this.datalist = datalist;
    }
}
