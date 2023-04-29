package com.example.hospitalandroidapp.database;

public class ProfessionModel {

    private int profession_id;
    private String name;

    public ProfessionModel() {
    }

    public ProfessionModel(int specialization_id, String name) {
        this.profession_id = specialization_id;
        this.name = name;
    }

    public int getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(int profession_id) {
        this.profession_id = profession_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
