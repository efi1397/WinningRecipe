package com.example.winningrecipe.Models;

public class DataClass {
    public String dataImage;
    public String dataName;
    public String dataIngredients;
    public String dataDescription;
    public String dataCategory;


    public DataClass(String dataImage, String dataName, String dataIngredients, String dataDescription, String dataCategory) {
        this.dataImage = dataImage;
        this.dataName = dataName;
        this.dataIngredients = dataIngredients;
        this.dataDescription = dataDescription;
        this.dataCategory = dataCategory;
    }

    public DataClass(){}

    public String getDataImage() {
        return dataImage;
    }

    public String getDataName() {
        return dataName;
    }

    public String getDataIngredients() {
        return dataIngredients;
    }

    public String getDataDescription() {
        return dataDescription;
    }

    public String getDataCategory() {
        return dataCategory;
    }
}