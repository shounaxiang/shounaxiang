package com.xhzh.shounaxiang.dataclass;

import android.os.Environment;

public class User {
    // "User":{"User_id":111,"User_password":"111","User_img":"http://112.74.109.111:8080/images/none_head_img.png",
    // "User_phone":"111","User_sex":"男","User_age":0}
    private String User_id,
            User_password,
            User_img,
            User_phone,
            User_sex,
            User_age,
            User_name,
            User_birthday;
    public static String LocalPritruesProfile = Environment.getExternalStorageDirectory()
            + "/xhzh/PicturesProfile/";

    public String getUser_name() {
        return User_name;
    }

    public String getUser_birthday() {
        return User_birthday;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public void setUser_birthday(String user_birthday) {
        User_birthday = user_birthday;
    }

    public String getUser_id() {
        return User_id;
    }

    public String getUser_password() {
        return User_password;
    }

    public String getUser_img() {
        return User_img;
    }

    public String getUser_phone() {
        return User_phone;
    }

    public String getUser_sex() {
        return User_sex;
    }

    public String getUser_age() {
        return User_age;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public void setUser_password(String user_password) {
        User_password = user_password;
    }

    public void setUser_img(String user_img) {
        User_img = user_img;
    }

    public void setUser_phone(String user_phone) {
        User_phone = user_phone;
    }

    public void setUser_sex(String user_sex) {
        User_sex = user_sex;
    }

    public void setUser_age(String user_age) {
        User_age = user_age;
    }
}
