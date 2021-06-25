package com.dylan.library.bean;

/**
 * Created by Dylan on 2016/11/3.
 */

public class VersionBean {
    private String version_name;

    private int version_code;

    private String download_url;

    private String description;

    private String is_force_update;

    public void setVersion_name(String version_name){
        this.version_name = version_name;
    }
    public String getVersion_name(){
        return this.version_name;
    }
    public void setVersion_code(int version_code){
        this.version_code = version_code;
    }
    public int getVersion_code(){
        return this.version_code;
    }
    public void setDownload_url(String download_url){
        this.download_url = download_url;
    }
    public String getDownload_url(){
        return this.download_url;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setIs_force_update(String is_force_update){
        this.is_force_update = is_force_update;
    }
    public String getIs_force_update(){
        return this.is_force_update;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "version_name='" + version_name + '\'' +
                ", version_code=" + version_code +
                ", download_url='" + download_url + '\'' +
                ", description='" + description + '\'' +
                ", is_force_update='" + is_force_update + '\'' +
                '}';
    }
}
