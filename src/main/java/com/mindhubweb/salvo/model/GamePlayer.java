package com.mindhubweb.salvo.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    private LocalDateTime joiningDate;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Salvo> salvos = new HashSet<>();

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.joiningDate = LocalDateTime.now();
    }

    public GamePlayer(Game game, Player player, Set<Ship> ships, Set<Salvo> salvos) {
        this.game = game;
        this.player = player;
        this.joiningDate = LocalDateTime.now();
        ships.forEach(this::addShip);

        salvos.stream().forEach(this::addSalvo);
    }

    public void addSalvo(Salvo salvo){
        salvos.add(salvo);
        salvo.setGamePlayer(this);
    }

    public void addShip(Ship ship) {
       ships.add(ship);
       ship.setGamePlayer(this);
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        Score tempScore;
        dto.put("id", this.getId());
        dto.put("player", this.player.makePlayerDTO());
        tempScore = this.player.getScore(this.game);
        if (tempScore != null){
            dto.put("score", tempScore.getResult());
        }
        return dto;
    }

    public Map<String, Object> makeGameViewDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.game.getCreationDate());
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer::makeGameViewPlayerDTO).collect(Collectors.toList()));
        dto.put("ships", this.ships.stream().map(Ship::makeGameViewShipDTO).collect(Collectors.toList()));
        dto.put("salvoes", this.game.getGamePlayers()
                .stream().flatMap(gamePlayer -> gamePlayer.getSalvos().stream().map(Salvo::makeGameViewSalvoDTO)));
       return dto;
   }

    public Map<String, Object> makeGameViewPlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gamePlayerID", this.getId());
        dto.put("player", this.player.makePlayerDTO());
        return dto;
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDateTime joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships){
        this.ships = ships;
        ships.forEach(this::addShip);
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }
}
