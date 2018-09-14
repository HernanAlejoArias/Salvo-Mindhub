package com.mindhubweb.salvo.controller;

import com.mindhubweb.salvo.model.*;
import com.mindhubweb.salvo.repository.GamePlayerRepository;
import com.mindhubweb.salvo.repository.GameRepository;
import com.mindhubweb.salvo.repository.PlayerRepository;
import com.mindhubweb.salvo.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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

        if ( username.isEmpty() || password.isEmpty() ) {

            return new ResponseEntity<>(ErrorMessages.ERROR_EMPTY_VALUE, HttpStatus.BAD_REQUEST);
        } else {
            Player player = playerRepository.findByUserName(username);

            if (player == null){
                Player createdPlayer = new Player(username, password);
                playerRepository.save(createdPlayer);

                return new ResponseEntity<>(createdPlayer.getUserName(), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(ErrorMessages.ERROR_NAME_TAKEN, HttpStatus.FORBIDDEN);
            }
        }
    }

    @GetMapping("/games")
    public Map<String, Object> getGames(){

        Player player = playerRepository.findByUserName(getloggedUserName());

        return makePlayerGamesDTO(player);
    }

    @PostMapping("/games")
    public ResponseEntity createGame(){
        Map<String, Long> newGamePlayer = new HashMap<>();

        Player loggedPlayer = playerRepository.findByUserName(getloggedUserName());

        if(loggedPlayer == null){
            return new ResponseEntity<>(ErrorMessages.NOT_LOGGED_IN , HttpStatus.FORBIDDEN);
        }else {
            Game newGame = new Game();
            GamePlayer tempGPA = new GamePlayer(newGame, loggedPlayer);
            gameRepository.save(newGame);
            gamePlayerRepository.save(tempGPA);

            newGamePlayer.put("gpid", tempGPA.getId() );

            return  new ResponseEntity<>(newGamePlayer , HttpStatus.OK);
        }

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

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity joinGame(@PathVariable("gameId") long gameId){

        Player loggedPlayer = playerRepository.findByUserName(getloggedUserName());
        Game selected_game = gameRepository.findById(gameId).orElse(null);
        Map<String, Long> new_gp = new HashMap<>();

        if(loggedPlayer == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (selected_game == null ){
            return new ResponseEntity<>(ErrorMessages.GAME_DOESNT_EXIST , HttpStatus.FORBIDDEN);
        }
        else {

            Set<GamePlayer> gamePlayersSelectedGame = gamePlayerRepository.findByGame(selected_game);

            if (gamePlayersSelectedGame.size() > 1){
                return new ResponseEntity<>(ErrorMessages.GAME_FULL , HttpStatus.FORBIDDEN);
            }else {
                GamePlayer tempGPA = new GamePlayer(selected_game, loggedPlayer);
                gamePlayerRepository.save(tempGPA);

                new_gp.put("gpid", tempGPA.getId() );

                return  new ResponseEntity<>(new_gp , HttpStatus.CREATED);

            }
        }
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity placeShips(@PathVariable("gamePlayerId") long gamePlayerId, @RequestBody Set<Ship> shipsToPlace){

        Player loggedPlayer = playerRepository.findByUserName(getloggedUserName());
        GamePlayer gamePlayersActive = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(loggedPlayer == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (gamePlayersActive == null ){
            return new ResponseEntity<>(ErrorMessages.GAME_DOESNT_EXIST , HttpStatus.FORBIDDEN);
        } else if (loggedPlayer.getId() != gamePlayersActive.getPlayer().getId()){
            return new ResponseEntity<>(ErrorMessages.THAT_IS_NOT_YOUR_PLAYER , HttpStatus.FORBIDDEN);
        } else {
            if (gamePlayersActive.getShips().isEmpty() ){

                gamePlayersActive.setShips(shipsToPlace);

                gamePlayerRepository.save(gamePlayersActive);

                return new ResponseEntity<>("Ok", HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(ErrorMessages.SHIPS_ALREADY_IN_PLACE , HttpStatus.FORBIDDEN);
            }
        }
    }

    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity shootedSalvos(@PathVariable("gamePlayerId") long gamePlayerId, @RequestBody Salvo salvosToPlace){

        Player loggedPlayer = playerRepository.findByUserName(getloggedUserName());
        GamePlayer gamePlayersActive = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(loggedPlayer == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (gamePlayersActive == null ){
            return new ResponseEntity<>(ErrorMessages.GAME_DOESNT_EXIST , HttpStatus.FORBIDDEN);
        } else if (loggedPlayer.getId() != gamePlayersActive.getPlayer().getId()){
            return new ResponseEntity<>(ErrorMessages.THAT_IS_NOT_YOUR_PLAYER , HttpStatus.FORBIDDEN);
        } else {
            if (gamePlayersActive.getSalvos().stream().anyMatch(salvo ->salvo.getTurn() == salvosToPlace.getTurn())){

                return new ResponseEntity<>(ErrorMessages.SALVOES_ALREADY_SHOOTED , HttpStatus.FORBIDDEN);
            }else{

                Salvo lastSalvo = gamePlayersActive.getSalvos().stream().filter(salvo -> salvo.getGamePlayer().equals(gamePlayersActive)).max((salvo1, salvo2) -> Integer.compare(salvo1.getTurn(), salvo2.getTurn())).orElse(null);
                if (lastSalvo != null){
                    salvosToPlace.setTurn(lastSalvo.getTurn() + 1);
                } else {
                    salvosToPlace.setTurn(1);
                }
                gamePlayersActive.addSalvo(salvosToPlace);

                gamePlayerRepository.save(gamePlayersActive);

                return new ResponseEntity<>("Ok", HttpStatus.CREATED);
            }

        }
    }

    @GetMapping("/game_view/{gamePlayer}")
    public ResponseEntity getGameView(@PathVariable("gamePlayer") long gamePlayerID){

        Player loggedPlayer = playerRepository.findByUserName(getloggedUserName());

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).orElse(null);

        if ( loggedPlayer.getId() != gamePlayer.getPlayer().getId() ){
            return new ResponseEntity<>(ErrorMessages.THAT_IS_NOT_YOUR_PLAYER , HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(gamePlayer.makeGameViewDTO() , HttpStatus.OK);
            //return gp.makeGameViewDTO();
        }
    }
}
