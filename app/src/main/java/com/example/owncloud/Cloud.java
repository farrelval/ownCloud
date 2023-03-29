package com.example.owncloud;

public class Cloud {
    private int id;
    private String name;
    private String format;
    private byte[] image;

    public Cloud(String name, String format, byte[] image, int id) {
        this.name = name;
        this.format = format;
        this.image = image;
        this.id = id;
    }

    public int getId() {

        return id;
    }
    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getFormat() {

        return format;
    }

    public void setFormat(String format) {

        this.format = format;
    }

    public byte[] getImage() {

        return image;
    }

    public void setImage(byte[] image) {

        this.image = image;
    }
}
