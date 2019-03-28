package com.phyrelinx.cp.cmark;

import java.io.Serializable;

public class DataModel implements Serializable{

    String fname,mname,lname;
    String id,group,gender,dob;
    String profession,address,phone;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public DataModel(String fname, String mname, String lname, String id, String gender) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.group = group;
        this.id = id;
        this.gender = gender;


    }
    @Override
    public String toString(){
        return this.id+" "+this.group+" "+this.fname+" "+this.mname+" "+this.lname;
    }
    public String getName(){
        return this.id+" "+this.fname+" "+this.mname+" "+this.lname;

    }


}
