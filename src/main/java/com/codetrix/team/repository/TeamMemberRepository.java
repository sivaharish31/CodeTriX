package com.codetrix.team.repository;

import com.codetrix.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    Optional<TeamMember> findByIdAndTeamId(Long id, Long teamId);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByRollNumberAndIdNot(String rollNumber, Long id);

    Optional<TeamMember> findByRollNumber(String rollNumber);

    @Query("SELECT COUNT(m) FROM TeamMember m WHERE m.team.id = :teamId")
    long countByTeamId(Long teamId);

    void deleteByTeamId(Long teamId);
}
