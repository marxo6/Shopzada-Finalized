package com.utcc.shopzada.Models;

public class BeepData {

    public String beepId, uploader, description, uploaderImage, beepUrl;
    public int views, likes;
    public boolean uploaderVerified;

    public BeepData() {

    }

    public BeepData(String beepId, String uploader, String description, String uploaderImage, String beepUrl, int views, int likes, boolean uploaderVerified) {
        this.beepId = beepId;
        this.uploader = uploader;
        this.description = description;
        this.uploaderImage = uploaderImage;
        this.beepUrl = beepUrl;
        this.views = views;
        this.likes = likes;
        this.uploaderVerified = uploaderVerified;
    }

    public String getBeepId() {
        return beepId;
    }

    public void setBeepId(String beepId) {
        this.beepId = beepId;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploaderImage() {
        return uploaderImage;
    }

    public void setUploaderImage(String uploaderImage) {
        this.uploaderImage = uploaderImage;
    }

    public String getBeepUrl() {
        return beepUrl;
    }

    public void setBeepUrl(String beepUrl) {
        this.beepUrl = beepUrl;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isUploaderVerified() {
        return uploaderVerified;
    }

    public void setUploaderVerified(boolean uploaderVerified) {
        this.uploaderVerified = uploaderVerified;
    }
}
