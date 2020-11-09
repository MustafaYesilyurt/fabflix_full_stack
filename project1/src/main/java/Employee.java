package main.java;

public class Employee {
    private final String email;
    private final String name;

    public Employee(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {return this.email;}
    public String getName() {return this.name;}
}
