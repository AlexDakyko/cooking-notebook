package com.cookingnotebook.model;

public class Ingredient {
    private int id;
    private int recipeId;
    private String name;
    private Double quantity;
    private String unit;

    public Ingredient() {
    }

    public Ingredient(int recipeId, String name, Double quantity, String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        String quantityText =
                quantity == null ? "" : quantity.toString();
        String unitText =
                unit == null || unit.isBlank() ? "" : " " + unit.trim();
        return name + (quantityText.isEmpty() ? "" : " - " + quantityText)
                + unitText;
    }
}
