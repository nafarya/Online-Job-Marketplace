package com.highfive.highfive.model;

/**
 * Created by dan on 19.03.17.
 */

public class OrderType {
    private String name;
    private String difficultyLevel;
    private String id;

    public OrderType(String name, String difficultyLevel, String id) {
        this.name = name;
        this.difficultyLevel = difficultyLevel;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
