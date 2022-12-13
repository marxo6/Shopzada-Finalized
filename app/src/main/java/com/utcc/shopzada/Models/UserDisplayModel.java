package com.utcc.shopzada.Models;

import java.util.HashMap;
import java.util.Map;

public class UserDisplayModel {

    private String id;
    private Boolean chatable;

    public UserDisplayModel() {

    }

    public UserDisplayModel(String id, Boolean chatable) {
        this.id = id;
        this.chatable = chatable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getChatable() {
        return chatable;
    }

    public void setChatable(Boolean chatable) {
        this.chatable = chatable;
    }
}
