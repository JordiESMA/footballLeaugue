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
    private String competitionGroup;

    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    //CONSTRUCTORS
    public Team() {}

    public Team(String name, String competitionGroup) {
        this.name = name;
        this.competitionGroup = competitionGroup;
    }

    //GETTERS
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompetitionGroup() {
        return competitionGroup;
    }

    public List<Player> getPlayers() {
        return players;
    }



}
