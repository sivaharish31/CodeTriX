package com.codetrix.execution.docker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult {

    private boolean timedOut;
    private int exitCode;
    private String stdout;
    private String stderr;
    private long executionTimeMs;
    private Long memoryUsedKb;

    public boolean isSuccess() {
        return !timedOut && exitCode == 0;
    }

    public String getOutput() {
        return stdout != null ? stdout : "";
    }

    public String getError() {
        return stderr != null ? stderr : "";
    }
}
