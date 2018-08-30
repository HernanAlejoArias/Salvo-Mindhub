package com.mindhubweb.salvo.model;

import com.mindhubweb.salvo.model.GamePlayer;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> locations;

    public Ship() {
    }

    public Ship(String type) {
        this.type = type;
    }

    public Ship(String type, List<String> locations) {
        this.type = type;
        this.locations = locations;
    }

    public void addGamePlayer (GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }

    public Map<String, Object> makeGameViewShipDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getType());
        dto.put("locations", this.locations);
        return dto;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
