package com.naptechlabs.skypeclone;

public class Contacts {
    String bio,image,name,uid;

    public Contacts() {
    }

    public Contacts(String bio, String image, String name, String uid) {
        this.bio = bio;
        this.image = image;
        this.name = name;
        this.uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
