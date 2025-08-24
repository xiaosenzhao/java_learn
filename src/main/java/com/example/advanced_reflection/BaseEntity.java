package com.example.advanced_reflection;

import java.util.Date;

class BaseEntity {
    protected String id;
    protected Date createdTime;
    protected String createdBy;

    public BaseEntity(String id, Date createdTime, String createdBy) {
        this.id = id;
        this.createdTime = createdTime;
        this.createdBy = createdBy;
    }
}