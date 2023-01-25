package com.advanced.minhas.model;

import java.io.Serializable;

/**
 * Created by mentor on 22/11/17.
 */

public class RouteGroup implements Serializable {

    private int groupId;
    private String groupName;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    @Override
    public String toString() {
        return getGroupName();
    }
}
