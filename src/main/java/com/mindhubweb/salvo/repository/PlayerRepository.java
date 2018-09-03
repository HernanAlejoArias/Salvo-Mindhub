package com.mindhubweb.salvo.repository;

import com.mindhubweb.salvo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByUserName(String email);

}
