package com.learn.model;

public class AppLink {
    private String name;
    private String description;
    private String url;
    private String icon;

    public AppLink() {
    }

    public AppLink(String name, String description, String url, String icon) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}