package com.ligafutbol.api.models;

import jakarta.persistence.*;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    //ATRIBUTES
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private String city;
    @Setter
    private int foundationYear;

    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    //CONSTRUCTORS
    public Team() {}

    public Team(String name, String city, int foundationYear) {
        this.name = name;
        this.city = city;
        this.foundationYear = foundationYear;
    }

    //GETTERS
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public int getFoundationYear() {
        return foundationYear;
    }

    public List<Player> getPlayers() {
        return players;
    }



}