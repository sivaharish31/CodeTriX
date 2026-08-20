package com.codetrix.team.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TeamException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public TeamException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "TEAM_ERROR";
    }

    public TeamException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "TEAM_ERROR";
    }

    public TeamException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static TeamException teamNotFound(Long teamId) {
        return new TeamException(
            "Team not found with id: " + teamId,
            HttpStatus.NOT_FOUND,
            "TEAM_NOT_FOUND"
        );
    }

    public static TeamException teamNotFoundByCode(String teamCode) {
        return new TeamException(
            "Team not found with code: " + teamCode,
            HttpStatus.NOT_FOUND,
            "TEAM_NOT_FOUND"
        );
    }

    public static TeamException memberNotFound(Long memberId) {
        return new TeamException(
            "Team member not found with id: " + memberId,
            HttpStatus.NOT_FOUND,
            "MEMBER_NOT_FOUND"
        );
    }

    public static TeamException teamNameExists(String teamName) {
        return new TeamException(
            "Team name already exists: " + teamName,
            HttpStatus.CONFLICT,
            "TEAM_NAME_EXISTS"
        );
    }

    public static TeamException rollNumberExists(String rollNumber) {
        return new TeamException(
            "Roll number already registered: " + rollNumber,
            HttpStatus.CONFLICT,
            "ROLL_NUMBER_EXISTS"
        );
    }

    public static TeamException maxTeamsReached(int maxTeams) {
        return new TeamException(
            "Maximum number of teams (" + maxTeams + ") has been reached",
            HttpStatus.CONFLICT,
            "MAX_TEAMS_REACHED"
        );
    }

    public static TeamException eventStarted() {
        return new TeamException(
            "Cannot modify team after event has started",
            HttpStatus.FORBIDDEN,
            "EVENT_STARTED"
        );
    }

    public static TeamException invalidOperation(String message) {
        return new TeamException(
            message,
            HttpStatus.BAD_REQUEST,
            "INVALID_OPERATION"
        );
    }
}
