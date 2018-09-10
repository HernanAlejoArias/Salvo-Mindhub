package com.mindhubweb.salvo.repository;

import com.mindhubweb.salvo.model.Game;
import com.mindhubweb.salvo.model.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {

    Set<GamePlayer> findByGame(Game game);

}
