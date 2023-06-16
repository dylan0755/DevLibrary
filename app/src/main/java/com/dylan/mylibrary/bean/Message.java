package com.dylan.mylibrary.bean;

public class Message {
    public static final int TYPE_OTHER =1;
    public static final int TYPE_ME=2;
    private int type;
    private String content;
    public boolean isGuide;
    private String importPath;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content==null?"":content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }
}
