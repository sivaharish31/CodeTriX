package com.codetrix.coding.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunCodeResponse {

    private boolean success;
    private String status;
    private String output;
    private String error;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private String compileOutput;

    public static RunCodeResponse success(String output, Integer timeMs, Integer memoryKb) {
        return RunCodeResponse.builder()
                .success(true)
                .status("SUCCESS")
                .output(output)
                .executionTimeMs(timeMs)
                .memoryUsedKb(memoryKb)
                .build();
    }

    public static RunCodeResponse compileError(String error) {
        return RunCodeResponse.builder()
                .success(false)
                .status("COMPILE_ERROR")
                .compileOutput(error)
                .build();
    }

    public static RunCodeResponse runtimeError(String error) {
        return RunCodeResponse.builder()
                .success(false)
                .status("RUNTIME_ERROR")
                .error(error)
                .build();
    }

    public static RunCodeResponse timeLimit() {
        return RunCodeResponse.builder()
                .success(false)
                .status("TIME_LIMIT")
                .error("Time limit exceeded")
                .build();
    }
}
