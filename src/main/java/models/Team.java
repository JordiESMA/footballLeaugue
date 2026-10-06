package models;

public class Team{

    //ATRIBUTES

    private String name;
    private String city;
    private int foundationYear;

    //CONSTRUCTOR
    public Team(String name, String city, int foundationYear) {
        this.name = name;
        this.city = city;
        this.foundationYear = foundationYear;
    }

    //GETTERS

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public int getFoundationYear() {
        return foundationYear;
    }
}
