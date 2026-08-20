package com.codetrix.execution.docker;

import com.codetrix.execution.entity.ExecutionLanguage;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class ContainerConfig {

    private String containerId;
    private ExecutionLanguage language;
    private Path workDirectory;
    private long memoryLimitBytes;
    private long cpuPeriod;
    private long cpuQuota;
    private int timeLimitMs;
    private int pidsLimit;
    private String networkMode;
    private boolean readOnlyRootFs;
    private String user;
    private boolean dropAllCapabilities;
    private boolean noNewPrivileges;
}
