package com.mindhubweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userName;

    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    public Player() { }

    public Player(String email) {
        userName = email;
    }

    public Player(String email, String pass) {
        userName = email;
        password = pass;

    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("email", this.userName);
        return dto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){ this.id = id; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    @JsonIgnore
    public List<Game> getGames(){
        return gamePlayers.stream().map(GamePlayer::getGame).collect(toList());
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    @JsonIgnore
    public Set<Score> getScores() {
        return scores;
    }

    public Score getScore(Game game) {

        return scores.stream().filter(sc -> sc.getGame().getId().equals(game.getId())).findFirst().orElse(null);

    }
}