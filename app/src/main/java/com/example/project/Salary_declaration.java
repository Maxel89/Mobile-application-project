package com.example.project;

public class Salary_declaration {
    private String id;
    private String name;
    private String company;
    private String category;
    private int salary;

    public Salary_declaration(String id, String name, String company, String category, int salary) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.category = category;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getCategory() {
        return category;
    }

    public int getSalary() {
        return salary;
    }

    public String toString(){
        return name + " (" + company + ")";
    }
}
