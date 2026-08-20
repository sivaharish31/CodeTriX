package com.codetrix.team.service;

import com.codetrix.team.dto.*;
import com.codetrix.team.entity.Team;
import com.codetrix.team.entity.TeamMember;
import com.codetrix.team.entity.TeamStatus;
import com.codetrix.team.exception.TeamException;
import com.codetrix.team.repository.TeamMemberRepository;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private static final String TEAM_CODE_PREFIX = "CTX";
    private static final int PIN_LENGTH = 6;
    private static final String PIN_CHARACTERS = "0123456789";

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${codetrix.max-teams:27}")
    private int maxTeams;

    @Transactional
    public TeamCredentialsResponse createTeam(CreateTeamRequest request) {
        log.info("Creating team with name: {}", request.getTeamName());

        long currentTeamCount = teamRepository.countAllTeams();
        if (currentTeamCount >= maxTeams) {
            throw TeamException.maxTeamsReached(maxTeams);
        }

        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw TeamException.teamNameExists(request.getTeamName());
        }

        String teamCode = generateUniqueTeamCode();
        String plainPin = generateSecurePin();
        String hashedPin = passwordEncoder.encode(plainPin);

        Team team = Team.builder()
                .teamCode(teamCode)
                .teamName(request.getTeamName())
                .loginPinHash(hashedPin)
                .status(TeamStatus.REGISTERED)
                .build();

        team = teamRepository.save(team);
        log.info("Created team: {} with code: {}", team.getTeamName(), team.getTeamCode());

        return TeamCredentialsResponse.of(
                team.getId(),
                team.getTeamCode(),
                team.getTeamName(),
                plainPin
        );
    }

    @Transactional(readOnly = true)
    public TeamListResponse getAllTeams() {
        List<Team> teams = teamRepository.findAllWithMembers();
        List<TeamResponse> teamResponses = teams.stream()
                .map(TeamResponse::fromEntityWithMembers)
                .toList();
        return TeamListResponse.of(teamResponses, maxTeams);
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long teamId) {
        Team team = teamRepository.findByIdWithMembers(teamId)
                .orElseThrow(() -> TeamException.teamNotFound(teamId));
        return TeamResponse.fromEntityWithMembers(team);
    }

    @Transactional
    public TeamResponse updateTeam(Long teamId, UpdateTeamRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> TeamException.teamNotFound(teamId));

        validateTeamModifiable(team);

        if (request.getTeamName() != null && !request.getTeamName().isBlank()) {
            if (!team.getTeamName().equals(request.getTeamName()) &&
                teamRepository.existsByTeamNameAndIdNot(request.getTeamName(), teamId)) {
                throw TeamException.teamNameExists(request.getTeamName());
            }
            team.setTeamName(request.getTeamName());
        }

        if (request.getStatus() != null) {
            team.setStatus(request.getStatus());
        }

        team = teamRepository.save(team);
        log.info("Updated team: {}", team.getTeamCode());

        return TeamResponse.fromEntity(team);
    }

    @Transactional
    public TeamCredentialsResponse regenerateCredentials(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> TeamException.teamNotFound(teamId));

        String plainPin = generateSecurePin();
        String hashedPin = passwordEncoder.encode(plainPin);

        team.setLoginPinHash(hashedPin);
        teamRepository.save(team);

        log.info("Regenerated credentials for team: {}", team.getTeamCode());

        return TeamCredentialsResponse.of(
                team.getId(),
                team.getTeamCode(),
                team.getTeamName(),
                plainPin
        );
    }

    @Transactional
    public MemberResponse addMember(Long teamId, AddMemberRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> TeamException.teamNotFound(teamId));

        validateTeamModifiable(team);

        if (teamMemberRepository.existsByRollNumber(request.getRollNumber())) {
            throw TeamException.rollNumberExists(request.getRollNumber());
        }

        TeamMember member = TeamMember.builder()
                .team(team)
                .name(request.getName())
                .rollNumber(request.getRollNumber())
                .college(request.getCollege())
                .email(request.getEmail())
                .build();

        member = teamMemberRepository.save(member);
        log.info("Added member {} to team {}", member.getRollNumber(), team.getTeamCode());

        return MemberResponse.fromEntity(member);
    }

    @Transactional
    public MemberResponse updateMember(Long teamId, Long memberId, UpdateMemberRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> TeamException.teamNotFound(teamId));

        validateTeamModifiable(team);

        TeamMember member = teamMemberRepository.findByIdAndTeamId(memberId, teamId)
                .orElseThrow(() -> TeamException.memberNotFound(memberId));

        if (request.getName() != null && !request.getName().isBlank()) {
            member.setName(request.getName());
        }

        if (request.getRollNumber() != null && !request.getRollNumber().isBlank()) {
            if (!member.getRollNumber().equals(request.getRollNumber()) &&
                teamMemberRepository.existsByRollNumberAndIdNot(request.getRollNumber(), memberId)) {
                throw TeamException.rollNumberExists(request.getRollNumber());
            }
            member.setRollNumber(request.getRollNumber());
        }

        if (request.getCollege() != null && !request.getCollege().isBlank()) {
            member.setCollege(request.getCollege());
        }

        if (request.getEmail() != null) {
            member.setEmail(request.getEmail().isBlank() ? null : request.getEmail());
        }

        member = teamMemberRepository.save(member);
        log.info("Updated member {} in team {}", member.getId(), team.getTeamCode());

        return MemberResponse.fromEntity(member);
    }

    @Transactional
    public void removeMember(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> TeamException.teamNotFound(teamId));

        validateTeamModifiable(team);

        TeamMember member = teamMemberRepository.findByIdAndTeamId(memberId, teamId)
                .orElseThrow(() -> TeamException.memberNotFound(memberId));

        teamMemberRepository.delete(member);
        log.info("Removed member {} from team {}", memberId, team.getTeamCode());
    }

    private String generateUniqueTeamCode() {
        String teamCode;
        int attempts = 0;
        do {
            teamCode = TEAM_CODE_PREFIX + String.format("%03d", secureRandom.nextInt(1000));
            attempts++;
            if (attempts > 100) {
                teamCode = TEAM_CODE_PREFIX + System.currentTimeMillis() % 10000;
                break;
            }
        } while (teamRepository.existsByTeamCode(teamCode));
        return teamCode;
    }

    private String generateSecurePin() {
        StringBuilder pin = new StringBuilder(PIN_LENGTH);
        for (int i = 0; i < PIN_LENGTH; i++) {
            pin.append(PIN_CHARACTERS.charAt(secureRandom.nextInt(PIN_CHARACTERS.length())));
        }
        return pin.toString();
    }

    private void validateTeamModifiable(Team team) {
        if (team.getStatus() == TeamStatus.ACTIVE ||
            team.getStatus() == TeamStatus.COMPLETED ||
            team.getStatus() == TeamStatus.DISQUALIFIED) {
            throw TeamException.eventStarted();
        }
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public long getCurrentTeamCount() {
        return teamRepository.countAllTeams();
    }
}
