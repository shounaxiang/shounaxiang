package com.xhzh.shounaxiang.dataclass;

/**
 * Created by wjsay on 2018/10/24
 * Describe:
 */
public class Spaces {
    private String Space_id,
            Space_name,
            Space_img,
            Space_belong,
            Space_level,
            User_id;

    public String getSpace_id() {
        return Space_id;
    }

    public String getSpace_name() {
        return Space_name;
    }

    public String getSpace_img() {
        return Space_img;
    }

    public String getSpace_belong() {
        return Space_belong;
    }

    public String getSpace_level() {
        return Space_level;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setSpace_id(String space_id) {
        Space_id = space_id;
    }

    public void setSpace_name(String space_name) {
        Space_name = space_name;
    }

    public void setSpace_img(String space_img) {
        Space_img = space_img;
    }

    public void setSpace_belong(String space_belong) {
        Space_belong = space_belong;
    }

    public void setSpace_level(String space_level) {
        Space_level = space_level;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }
}
