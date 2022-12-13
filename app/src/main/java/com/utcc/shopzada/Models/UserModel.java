package com.utcc.shopzada.Models;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class UserModel {

    private String username, email, password, phone, permission;
    private String imageUrl, address;
    private int credits, level, levelpoints, luckypoints;
    private boolean emailVerified, phoneVerified, verified;

    public UserModel() {

    }

    public UserModel(String username, String email, String password, String phone, String imageUrl, String permission, String address, int credits, int level, int levelpoints, int luckypoints, boolean emailVerified, boolean phoneVerified, boolean verified) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.permission = permission;
        this.address = address;
        this.credits = credits;
        this.level = level;
        this.levelpoints = levelpoints;
        this.luckypoints = luckypoints;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.verified = verified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelpoints() {
        return levelpoints;
    }

    public void setLevelpoints(int levelpoints) {
        this.levelpoints = levelpoints;
    }

    public int getLuckypoints() {
        return luckypoints;
    }

    public void setLuckypoints(int luckypoints) {
        this.luckypoints = luckypoints;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", username);
        dataMap.put("email", email);
        dataMap.put("emailVerified", emailVerified);
        dataMap.put("password", password);
        dataMap.put("permission", permission);
        dataMap.put("phone", phone);
        dataMap.put("phoneVerified", phoneVerified);
        dataMap.put("verified", verified);
        dataMap.put("credits", credits);
        dataMap.put("level", level);
        dataMap.put("levelpoints", levelpoints);
        dataMap.put("luckypoints", luckypoints);
        dataMap.put("imageUrl", imageUrl);
        dataMap.put("address", "");

        return dataMap;
    }
}
