package com.mindhubweb.salvo.model;

import com.mindhubweb.salvo.model.Game;
import com.mindhubweb.salvo.model.Player;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private float result;

    private LocalDateTime finishDate;

    public Score() {
    }

    public Score(Game game, Player player, float result, LocalDateTime finishDate) {
        this.game = game;
        this.player = player;
        this.result = result;
        this.finishDate = finishDate;
    }

    public Map<String, Object> makeScoreDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("game_id", this.getGame().getId());
        dto.put("player_id", this.getPlayer().getId());
        dto.put("player_userName", this.getPlayer().getUserName());
        dto.put("points", this.result);
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

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }
}
