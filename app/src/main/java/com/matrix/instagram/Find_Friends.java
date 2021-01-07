package com.matrix.instagram;

public class Find_Friends {
    String Country, FUllName , Status, Username, DOB,Gender,RelationShipStatus,ProfileImage;

    public Find_Friends() {
    }

    public Find_Friends(String country, String FUllName, String status, String username, String DOB, String gender, String relationShipStatus, String profileImage) {
        Country = country;
        this.FUllName = FUllName;
        Status = status;
        Username = username;
        this.DOB = DOB;
        Gender = gender;
        RelationShipStatus = relationShipStatus;
        ProfileImage = profileImage;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getFUllName() {
        return FUllName;
    }

    public void setFUllName(String FUllName) {
        this.FUllName = FUllName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getRelationShipStatus() {
        return RelationShipStatus;
    }

    public void setRelationShipStatus(String relationShipStatus) {
        RelationShipStatus = relationShipStatus;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
