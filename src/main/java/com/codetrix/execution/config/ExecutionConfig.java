package com.codetrix.execution.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "execution")
public class ExecutionConfig {

    private int maxConcurrentExecutions = 4;
    private int queueCapacity = 100;
    private int defaultTimeLimitMs = 2000;
    private int defaultMemoryLimitMb = 256;
    private int maxTimeLimitMs = 10000;
    private int maxMemoryLimitMb = 512;
    private int compilationTimeoutMs = 30000;
    private String tempDirectory = "/tmp/codetrix";
    private boolean cleanupEnabled = true;
    private Docker docker = new Docker();
    private Security security = new Security();

    @Data
    public static class Docker {
        private String network = "none";
        private boolean privileged = false;
        private boolean readOnlyRootFs = true;
        private long cpuPeriod = 100000;
        private long cpuQuota = 50000;
        private int pidsLimit = 64;
        private String user = "nobody";
        private boolean dropAllCapabilities = true;
    }

    @Data
    public static class Security {
        private boolean enableSeccomp = true;
        private boolean enableAppArmor = true;
        private boolean noNewPrivileges = true;
        private int maxOutputSizeKb = 64;
        private int maxSourceCodeSizeKb = 128;
    }
}
