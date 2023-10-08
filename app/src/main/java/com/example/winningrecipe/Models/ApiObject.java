package com.example.winningrecipe.Models;

public class ApiObject {
    public String dataImage;
    public String dataName;
    public String dataIngredients;
    public String dataInstructions;
    public String dataCategory;

    public ApiObject(String dataImage, String dataName, String dataIngredients, String dataInstructions, String dataCategory) {
        this.dataImage = dataImage;
        this.dataName = dataName;
        this.dataIngredients = dataIngredients;
        this.dataInstructions = dataInstructions;
        this.dataCategory = dataCategory;
    }
    public ApiObject() {}

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataIngredients() {
        return dataIngredients;
    }

    public void setDataIngredients(String dataIngredients) {
        this.dataIngredients = dataIngredients;
    }

    public String getDataInstructions() {
        return dataInstructions;
    }

    public void setDataInstructions(String dataInstructions) {
        this.dataInstructions = dataInstructions;
    }

    public String getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(String dataCategory) {
        this.dataCategory = dataCategory;
    }

}