package com.mindhubweb.salvo;

import com.mindhubweb.salvo.model.*;
import com.mindhubweb.salvo.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static final String DESTROYER = "destroyer";
	public static final String PATROL_BOAT = "patrol";
	public static final String SUBMARINE = "submarine";
	public static final float WIN = 1;
	public static final float LOSE = 0;
	public static final float TIE = 0.5f;


	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of Players
			Player player1 = new Player("j.bauer@ctu.gov", "24");
			Player player2 = new Player("c.obrian@ctu.gov", "42");
			Player player3 = new Player("kim_bauer@gmail.com", "kb");
			Player player4 = new Player("t.almeida@ctu.gov", "mole");

			Game game1 = new Game();
			Game game2 = new Game(1);
			Game game3 = new Game(2);
			Game game4 = new Game();
			Game game5 = new Game(4);
			Game game6 = new Game(7);
			Game game7 = new Game(2);
			Game game8 = new Game(9);

			// Create Players on DB
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

            // Create Games on DB
			gameRepository.save(game1);
			gameRepository.save(game2);
           	gameRepository.save(game3);
           	gameRepository.save(game4);
           	gameRepository.save(game5);
           	gameRepository.save(game6);
           	gameRepository.save(game7);
           	gameRepository.save(game8);

           	// Game 1
			// Ships for Gamer A
			Set<Ship> tempShipsA = new HashSet<>();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("H2","H3","H4")));
			tempShipsA.add(new Ship(SUBMARINE, Arrays.asList("E1", "F1", "G1")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("B4","B5")));
			Set<Salvo> tempSalvosA = new HashSet<>();
			tempSalvosA.add(new Salvo(1, Arrays.asList("B5", "C5", "F1")));
			tempSalvosA.add(new Salvo(2, Arrays.asList("F2", "D5")));
			Score tempScoreA = new Score(game1, player1, WIN, game1.getCreationDate().plusMinutes(30));

			// Ships for Gamer B
			Set<Ship> tempShipsB= new HashSet<>();
			tempShipsB.add(new Ship(DESTROYER, Arrays.asList("B5","C5","D5")));
			tempShipsB.add(new Ship(PATROL_BOAT, Arrays.asList("F1","F2")));
			Set<Salvo> tempSalvosB = new HashSet<>();
			tempSalvosB.add(new Salvo(1, Arrays.asList("B4", "B5", "B6")));
			tempSalvosB.add(new Salvo(2, Arrays.asList("E1", "H3", "A2")));
			Score tempScoreB = new Score(game1, player2, LOSE, game1.getCreationDate().plusMinutes(30));

			scoreRepository.save(tempScoreA);
			GamePlayer tempGPA = new GamePlayer(game1, player1, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);
			scoreRepository.save(tempScoreB);
			GamePlayer tempGPB = new GamePlayer(game1, player2, tempShipsB, tempSalvosB);
			gamePlayerRepository.save(tempGPB);

			// Game 2

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));
			tempSalvosA.add(new Salvo(1, Arrays.asList("A2", "A4", "G6")));
			tempSalvosA.add(new Salvo(2, Arrays.asList("A3", "H6")));
			tempScoreA = new Score(game2, player1, TIE, game2.getCreationDate().plusMinutes(30));

			tempShipsB.clear();
			tempSalvosB.clear();
			tempShipsB.add(new Ship(SUBMARINE, Arrays.asList("A2", "A3", "A4")));
			tempShipsB.add(new Ship(PATROL_BOAT, Arrays.asList("G6", "H6")));
			tempSalvosB.add(new Salvo(1, Arrays.asList("B5", "D5", "C7")));
			tempSalvosB.add(new Salvo(2, Arrays.asList("C5", "C6")));
			tempScoreB = new Score(game2, player2, TIE, game2.getCreationDate().plusMinutes(30));

			scoreRepository.save(tempScoreA);
			tempGPA = new GamePlayer(game2, player1, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);
			scoreRepository.save(tempScoreB);
			tempGPB = new GamePlayer(game2, player2, tempShipsB, tempSalvosB);
			gamePlayerRepository.save(tempGPB);

			// Game 3

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));
			tempSalvosA.add(new Salvo(1, Arrays.asList("G6", "H6", "A4")));
			tempSalvosA.add(new Salvo(2, Arrays.asList("A2", "A3", "D8")));
			tempScoreA = new Score(game3, player2, WIN, game3.getCreationDate().plusMinutes(30));

			tempShipsB.clear();
			tempSalvosB.clear();
			tempShipsB.add(new Ship(SUBMARINE, Arrays.asList("A2", "A3", "A4")));
			tempShipsB.add(new Ship(PATROL_BOAT, Arrays.asList("G6", "H6")));
			tempSalvosB.add(new Salvo(1, Arrays.asList("H1", "H2", "H3")));
			tempSalvosB.add(new Salvo(2, Arrays.asList("E1", "F2", "G3")));
			tempScoreB = new Score(game3, player4, LOSE, game3.getCreationDate().plusMinutes(30));

			scoreRepository.save(tempScoreA);
			tempGPA = new GamePlayer(game3, player2, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);
			scoreRepository.save(tempScoreB);
			tempGPB = new GamePlayer(game3, player4, tempShipsB, tempSalvosB);
			gamePlayerRepository.save(tempGPB);

			// Game 4

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));
			tempSalvosA.add(new Salvo(1, Arrays.asList("A3", "A4", "F7")));
			tempSalvosA.add(new Salvo(2, Arrays.asList("A2", "G6", "H6")));
			tempScoreA = new Score(game4, player2, TIE, game4.getCreationDate().plusMinutes(30));

			tempShipsB.clear();
			tempSalvosB.clear();
			tempShipsB.add(new Ship(SUBMARINE, Arrays.asList("A2", "A3", "A4")));
			tempShipsB.add(new Ship(PATROL_BOAT, Arrays.asList("G6", "H6")));
			tempSalvosB.add(new Salvo(1, Arrays.asList("B5", "C6", "H1")));
			tempSalvosB.add(new Salvo(2, Arrays.asList("C5", "C7", "D5")));
			tempScoreB = new Score(game4, player1, TIE, game4.getCreationDate().plusMinutes(30));

			scoreRepository.save(tempScoreA);
			tempGPA = new GamePlayer(game4, player2, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);
			scoreRepository.save(tempScoreB);
			tempGPB = new GamePlayer(game4, player1, tempShipsB, tempSalvosB);
			gamePlayerRepository.save(tempGPB);

			// Game 5

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));
			tempSalvosA.add(new Salvo(1, Arrays.asList("A1", "A2", "A3")));
			tempSalvosA.add(new Salvo(2, Arrays.asList("G6", "G7", "G8")));

			tempShipsB.clear();
			tempSalvosB.clear();
			tempShipsB.add(new Ship(SUBMARINE, Arrays.asList("A2", "A3", "A4")));
			tempShipsB.add(new Ship(PATROL_BOAT, Arrays.asList("G6", "H6")));
			tempSalvosB.add(new Salvo(1, Arrays.asList("B5", "B6", "C7")));
			tempSalvosB.add(new Salvo(2, Arrays.asList("C6", "D6", "E6")));
			tempSalvosB.add(new Salvo(3, Arrays.asList("H1", "H8")));


			tempGPA = new GamePlayer(game5, player4, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);
			tempGPB = new GamePlayer(game5, player1, tempShipsB, tempSalvosB);
			gamePlayerRepository.save(tempGPB);


			// Game 6

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));

			tempGPA = new GamePlayer(game6, player3, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);

			// Game 7

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));

			tempGPA = new GamePlayer(game7, player4, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);

			// Game 8

			tempShipsA.clear();
			tempSalvosA.clear();
			tempShipsA.add(new Ship(DESTROYER, Arrays.asList("B5", "C5", "D5")));
			tempShipsA.add(new Ship(PATROL_BOAT, Arrays.asList("C6", "C7")));

			tempShipsB.clear();
			tempSalvosB.clear();
			tempShipsB.add(new Ship(SUBMARINE, Arrays.asList("A2", "A3", "A4")));
			tempShipsB.add(new Ship(PATROL_BOAT, Arrays.asList("G6", "H6")));

			tempGPA = new GamePlayer(game8, player1, tempShipsA, tempSalvosA);
			gamePlayerRepository.save(tempGPA);
			tempGPB = new GamePlayer(game8, player4, tempShipsB, tempSalvosB);
			gamePlayerRepository.save(tempGPB);
		};
	}
}

