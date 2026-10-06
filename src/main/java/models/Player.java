package models;

//ATRIBUTS

public class Player {
    private String name;
    private int shirtNumber;
    private Team team;


//CONSTRUCTORS


 public Player(String name, int shirtNumber, Team team) {
     this.name = name;
     this.shirtNumber = shirtNumber;
     this.team = team;
 }


 //GETTERS


    public String getName() {
        return name;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public Team getTeam() {
        return team;
    }
}


