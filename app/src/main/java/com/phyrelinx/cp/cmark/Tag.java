package com.phyrelinx.cp.cmark;

import java.io.Serializable;

/**
 * Created by olatunde on 8/30/17.
 */

public class Tag implements Serializable{
    String id,phone,id_device;
    String toilet_out,toilet_in,age;
    String parent,classroom,teacher;
    String tag_in,tag_out,tag_id;
//    upload status;
    Boolean status = false;
    String gender;

    Boolean checkstatus;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }


    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTag_in() {
        return tag_in;
    }

    public void setTag_in(String tag_in) {
        this.tag_in = tag_in;
    }

    public String getTag_out() {
        return tag_out;
    }

    public void setTag_out(String tag_out) {
        this.tag_out = tag_out;
    }

    public String getToilet_out() {
        return toilet_out;
    }

    public void setToilet_out(String toilet_out) {
        this.toilet_out = toilet_out;
    }

    public String getToilet_in() {
        return toilet_in;
    }

    public void setToilet_in(String toilet_in) {
        this.toilet_in = toilet_in;
    }

    public String getId_device() {
        return id_device;
    }

    public void setId_device(String id_device) {
        this.id_device = id_device;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(Boolean checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Tag(String tagin,String tagid,String id,String parenttag,String device,String teacher,String telephone ,String age,String gender){
        this.tag_in=tagin;
        this.tag_out = "0";
        this.toilet_in ="0";
        this.toilet_out ="0";
        //registered id of the child
        this.id = id;
        this.tag_id = tagid;
        this.parent =parenttag;
        this.id_device = device;
        this.teacher = teacher;
        this.phone = telephone;
        this.age =age;
        this.gender = gender.toUpperCase();


    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", id_device='" + id_device + '\'' +
                ", toilet_out='" + toilet_out + '\'' +
                ", toilet_in='" + toilet_in + '\'' +
                ", age='" + age + '\'' +
                ", parent='" + parent + '\'' +
                ", classroom='" + classroom + '\'' +
                ", teacher='" + teacher + '\'' +
                ", tag_in='" + tag_in + '\'' +
                ", tag_out='" + tag_out + '\'' +
                ", tag_id='" + tag_id + '\'' +
                ", status=" + status +
                ", gender='" + gender + '\'' +
                ", checkstatus=" + checkstatus +
                '}';
    }
}
