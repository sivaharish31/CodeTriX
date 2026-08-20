package com.codetrix.team.repository;

import com.codetrix.team.entity.Team;
import com.codetrix.team.entity.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamCode(String teamCode);

    Optional<Team> findByTeamName(String teamName);

    boolean existsByTeamCode(String teamCode);

    boolean existsByTeamName(String teamName);

    boolean existsByTeamNameAndIdNot(String teamName, Long id);

    List<Team> findByStatus(TeamStatus status);

    @Query("SELECT COUNT(t) FROM Team t")
    long countAllTeams();

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.id = :id")
    Optional<Team> findByIdWithMembers(Long id);

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.teamCode = :teamCode")
    Optional<Team> findByTeamCodeWithMembers(String teamCode);

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members")
    List<Team> findAllWithMembers();
}
