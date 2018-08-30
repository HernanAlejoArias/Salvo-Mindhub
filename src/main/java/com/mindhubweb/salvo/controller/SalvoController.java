package com.mindhubweb.salvo.controller;

import com.mindhubweb.salvo.model.Game;
import com.mindhubweb.salvo.model.GamePlayer;
import com.mindhubweb.salvo.model.Player;
import com.mindhubweb.salvo.repository.GamePlayerRepository;
import com.mindhubweb.salvo.repository.GameRepository;
import com.mindhubweb.salvo.repository.PlayerRepository;
import com.mindhubweb.salvo.util.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private String getloggedUserName(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    };

    @PostMapping("/players")
    public ResponseEntity createNewPlayer(@RequestParam("username") String username, @RequestParam("password") String password){

        if ( username == "" || password == "" ) {

            return new ResponseEntity<>(Consts.ERROR_EMPTY_VALUE, HttpStatus.BAD_REQUEST);
        } else {
            Player player = playerRepository.findByUserName(username);

            if (player == null){
                Player createdPlayer = new Player(username, password);
                playerRepository.save(createdPlayer);

                return new ResponseEntity<>(createdPlayer.getUserName(), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(Consts.ERROR_NAME_TAKEN, HttpStatus.FORBIDDEN);
            }
        }
    }

    @GetMapping("/games")
    public Map<String, Object> getGames(){

        //return gameRepository.findAll().stream().map(Game::makeGameDTO).collect(Collectors.toList());

        Player player = playerRepository.findByUserName(getloggedUserName());

        return makePlayerGamesDTO(player);
    }

    private Map<String, Object> makePlayerGamesDTO(Player loggedPlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        if (loggedPlayer == null){
            dto.put("player", null);
        }else{
            dto.put("player", loggedPlayer.makePlayerDTO());
        }
        dto.put("games", gameRepository.findAll().stream().map(Game::makeGameDTO).collect(Collectors.toList()) );
        return dto;
    };

    @GetMapping("/game_view/{gamePlayer}")
    public Map<String, Object> getGameView(@PathVariable("gamePlayer") long gamePlayerID){

        GamePlayer gp = gamePlayerRepository.findById(gamePlayerID).orElse(null);

        return gp.makeGameViewDTO();

    }
}
