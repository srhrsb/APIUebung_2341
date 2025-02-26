package com.brh.interpol_api;

public class Person {

    private String name;
    private String forename;

    private String dateOfBirth;
    private String imageURL;

    public Person(String name, String forename, String dateOfBirth, String imageURL) {
        this.name = name;
        this.forename = forename;
        this.dateOfBirth = dateOfBirth;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
