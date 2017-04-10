package com.highfive.highfive.model;

/**
 * Created by dan on 19.03.17.
 */

public class Subject {
    private String name;
    private String science;
    private String id;
    private String difficultyLevel;

    public Subject(String name, String science, String difficultyLevel, String id) {
        this.name = name;
        this.science = science;
        this.id = id;
        this.difficultyLevel = difficultyLevel;
    }

    public String getScience() {
        return science;
    }

    public void setScience(String science) {
        this.science = science;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
