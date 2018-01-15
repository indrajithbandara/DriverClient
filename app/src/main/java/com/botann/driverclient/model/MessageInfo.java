package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/8/4.
 */
public class MessageInfo implements Serializable {
    private Integer id;
    private Long createDate;
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
