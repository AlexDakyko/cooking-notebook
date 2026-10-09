package com.cookingnotebook.model;

import java.io.Serializable;

public class Recipe implements Serializable {
    private int id;
    private String title;
    private String description;
    private String instructions;
    private int preparationTime;
    private int servings;
    private int categoryId;
    private int photoId;

    public Recipe() {
    }

    public Recipe(String title, String description, String instructions, int preparationTime, int servings, int categoryId, int photoId) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.preparationTime = preparationTime;
        this.servings = servings;
        this.categoryId = categoryId;
        this.photoId = photoId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photo, int photoId) {
        this.photoId = photoId;
    }
}