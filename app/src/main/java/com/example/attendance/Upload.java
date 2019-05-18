package com.example.attendance;

public class Upload {
    private String mImageUrl;
    private String mName;
    private Student student;

    public Upload(){

    }
    public Upload(String name,String imageUrl){
        mName=name;
        mImageUrl=imageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
