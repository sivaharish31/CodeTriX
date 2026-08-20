package com.codetrix.debugging.dto;

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
public class DebuggingRunResponse {

    private boolean success;
    private String status;
    private String output;
    private String error;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private String compileOutput;

    public static DebuggingRunResponse success(String output, Integer timeMs, Integer memoryKb) {
        return DebuggingRunResponse.builder()
                .success(true)
                .status("SUCCESS")
                .output(output)
                .executionTimeMs(timeMs)
                .memoryUsedKb(memoryKb)
                .build();
    }

    public static DebuggingRunResponse compileError(String error) {
        return DebuggingRunResponse.builder()
                .success(false)
                .status("COMPILE_ERROR")
                .compileOutput(error)
                .build();
    }

    public static DebuggingRunResponse runtimeError(String error) {
        return DebuggingRunResponse.builder()
                .success(false)
                .status("RUNTIME_ERROR")
                .error(error)
                .build();
    }
}
