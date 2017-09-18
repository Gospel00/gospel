package com.dxc.mycollector.model;

import java.io.Serializable;

/**
 * Created by gospel on 2017/8/18.
 * About User infomation
 */

public class User  implements Serializable {
    private int id;
    private String uName;
    private String uPwd;
    private String uRePwd;
    private String uAge;
    private String uSex;
    private String uPhone;
    private String uAddress;
    private String urealName;
    private String idCard;
    private String gongDian;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuPwd() {
        return uPwd;
    }

    public void setuPwd(String uPwd) {
        this.uPwd = uPwd;
    }

    public String getuRePwd() {
        return uRePwd;
    }

    public void setuRePwd(String uRePwd) {
        this.uRePwd = uRePwd;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getuAddress() {
        return uAddress;
    }

    public void setuAddress(String uAddress) {
        this.uAddress = uAddress;
    }

    public String getuAge() {
        return uAge;
    }

    public void setuAge(String uAge) {
        this.uAge = uAge;
    }

    public String getuSex() {
        return uSex;
    }

    public void setuSex(String uSex) {
        this.uSex = uSex;
    }

    public void setgongDian(String gongDian) {
        this.gongDian = gongDian;
    }

    public void seturealName(String urealName) {
        this.urealName = urealName;
    }

    public void setidCard(String idCard) {
        this.idCard = idCard;
    }

    public String geturealName() {
        return urealName;
    }

    public String getidCard() {
        return idCard;
    }

    public String getgongDian() {
        return gongDian;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uName='" + uName + '\'' +
                ", uPwd='" + uPwd + '\'' +
                ", uRePwd='" + uRePwd + '\'' +
                ", uAge='" + uAge + '\'' +
                ", uSex='" + uSex + '\'' +
                ", uPhone='" + uPhone + '\'' +
                ", uAddress='" + uAddress + '\'' +
                ", urealName='" + urealName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", gongDian='" + gongDian + '\'' +
                '}';
    }
}
