package com.example.sebas.trade;

/**
 * Created by vikrach on 4/30/17.
 */

public class Post {

    private String image;
    private String title;
    private String desc;
    private String username;

    public Post() {

    }

    public Post(String image, String title, String desc, String username) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUsername() {
        return username;
    }

    public void setUser(String user) {
        this.username = username;
    }


}
