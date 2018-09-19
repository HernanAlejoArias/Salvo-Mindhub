package com.mindhubweb.salvo.model;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turn;

    @ElementCollection
    List<String> locations;

    public Salvo() {
    }

    public Salvo(int turn, List<String> locations) {
        this.turn = turn;
        this.locations = locations;
    }

    public Map<String, Object> makeGameViewSalvoDTO() {
        Map<String, Object> dto = new HashMap<>();
        GamePlayer opponent = this.gamePlayer.getOpponent();

        dto.put("player", this.gamePlayer.getPlayer().getId());
        dto.put("locations", locations);
        dto.put("turn", this.turn);
        dto.put("hits", getHits(opponent.getShips(), this));

        return dto;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    private List<String> getHits(Set<Ship> ships, Salvo salvo) {

        List<String> hits = new ArrayList<>();

        for (String salvoLocation : salvo.getLocations()) {
            for (Ship ship : ships) {
                List<String> shipLocations = ship.getLocations();

                if (shipLocations.indexOf(salvoLocation) != -1) {
                    hits.add(salvoLocation);
                }
            }
        }

        return hits;
    }
}
