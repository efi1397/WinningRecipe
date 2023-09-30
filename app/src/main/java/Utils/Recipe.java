package Utils;

import java.util.List;

public class Recipe {
    private String name;
    private List<String> ingredients;
    private String category;
    private int preparationTime;
    private String description;


    public Recipe() {
        // Default constructor required for Firebase
    }

    public Recipe(String name, List<String> ingredients, String category, int preparationTime, String description) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid name for Recipe");
        }
        if (!isValidIngredients(ingredients)) {
            throw new IllegalArgumentException("Invalid ingredients for Recipe");
        }
        if (!isValidCategory(category)) {
            throw new IllegalArgumentException("Invalid category for Recipe");
        }
        if (!isValidPreparationTime(preparationTime)) {
            throw new IllegalArgumentException("Invalid preparation time for Recipe");
        }
        if (!isValidDescription(description)) {
            throw new IllegalArgumentException("Invalid description for Recipe");
        }

        // If all data is valid, initialize the object
        this.name = name;
        this.ingredients = ingredients;
        this.category = category;
        this.preparationTime = preparationTime;
        this.description = description;
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isValidName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Invalid name for Recipe");
        }
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        if (isValidIngredients(ingredients)) {
            this.ingredients = ingredients;
        } else {
            throw new IllegalArgumentException("Invalid ingredients for Recipe");
        }
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (isValidCategory(category)) {
            this.category = category;
        } else {
            throw new IllegalArgumentException("Invalid category for Recipe");
        }
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        if (isValidPreparationTime(preparationTime)) {
            this.preparationTime = preparationTime;
        } else {
            throw new IllegalArgumentException("Invalid preparation time for Recipe");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (isValidDescription(description)) {
            this.description = description;
        } else {
            throw new IllegalArgumentException("Invalid description for Recipe");
        }
    }

    // Validation methods for individual fields
    public static boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    public static boolean isValidIngredients(List<String> ingredients) {
        return ingredients != null && !ingredients.isEmpty();
    }

    public static boolean isValidCategory(String category) {
        return category != null && !category.isEmpty();
    }

    public static boolean isValidPreparationTime(int preparationTime) {
        return preparationTime >= 0; // Ensure non-negative preparation time
    }

    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }
}
