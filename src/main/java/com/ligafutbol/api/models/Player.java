package com.ligafutbol.api.models;

import jakarta.persistence.*;

@Entity
public class Player {

    //ATRIBUTES
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int shirtNumber;

    @ManyToOne
    private Team team;

    //CONSTRUCTORS
    public Player() {}

    public Player(String name, int shirtNumber, Team team) {
        this.name = name;
        this.shirtNumber = shirtNumber;
        this.team = team;
    }

    //GETTERS
    public Long getId() {
        return id;
    }

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