package com.optibe.android.beoptimist;

import java.util.Date;

public class BlogPost extends BlogPostId {

    public String image_url, user_id, desc;
    public Date timestamp;

    public BlogPost(){}

    public BlogPost(String image_url, String user_id, String desc,Date timestamp) {
        this.image_url = image_url;
        this.user_id = user_id;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public Date getTimerstamp() {
        return timestamp;
    }

    public String getDesc() {
        return desc;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTimerstamp(Date timerstamp) {
        this.timestamp = timerstamp;
    }
}