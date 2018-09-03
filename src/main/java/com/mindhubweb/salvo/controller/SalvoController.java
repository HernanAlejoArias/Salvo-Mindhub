package com.mindhubweb.salvo.controller;

import com.mindhubweb.salvo.model.Game;
import com.mindhubweb.salvo.model.GamePlayer;
import com.mindhubweb.salvo.model.Player;
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



        // Falta validar que solo haya un jugador
        //checks that the game has only one player
        //if there are two players, it sends a Forbidden response with descriptive text, such as "Game is full"
    }

    @GetMapping("/game_view/{gamePlayer}")
    public ResponseEntity getGameView(@PathVariable("gamePlayer") long gamePlayerID){

        Player loggedPlayer = playerRepository.findByUserName(getloggedUserName());

        GamePlayer gp = gamePlayerRepository.findById(gamePlayerID).orElse(null);

        if ( loggedPlayer.getId() != gp.getPlayer().getId() ){
            System.out.println("PLAYER.GET_ID() = " + loggedPlayer.getId());
            System.out.println("GP.GET_PLAYER().GET_ID() = " + gp.getPlayer().getId());
            return new ResponseEntity<>(ErrorMessages.THAT_IS_NOT_YOUR_PLAYER , HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(gp.makeGameViewDTO() , HttpStatus.OK);
            //return gp.makeGameViewDTO();
        }
    }
}
