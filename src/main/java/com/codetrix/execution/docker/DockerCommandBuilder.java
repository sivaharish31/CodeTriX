package com.codetrix.execution.docker;

import com.codetrix.execution.config.ExecutionConfig;
import com.codetrix.execution.entity.ExecutionLanguage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DockerCommandBuilder {

    private final ExecutionConfig config;

    public List<String> buildRunCommand(ContainerConfig containerConfig) {
        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("run");

        cmd.add("--name");
        cmd.add(containerConfig.getContainerId());

        cmd.add("--rm");

        cmd.add("--network");
        cmd.add(containerConfig.getNetworkMode());

        cmd.add("--memory");
        cmd.add(containerConfig.getMemoryLimitBytes() + "b");

        cmd.add("--memory-swap");
        cmd.add(containerConfig.getMemoryLimitBytes() + "b");

        cmd.add("--cpu-period");
        cmd.add(String.valueOf(containerConfig.getCpuPeriod()));

        cmd.add("--cpu-quota");
        cmd.add(String.valueOf(containerConfig.getCpuQuota()));

        cmd.add("--pids-limit");
        cmd.add(String.valueOf(containerConfig.getPidsLimit()));

        cmd.add("--ulimit");
        cmd.add("nproc=64:64");

        cmd.add("--ulimit");
        cmd.add("fsize=10485760:10485760");

        if (containerConfig.isReadOnlyRootFs()) {
            cmd.add("--read-only");
        }

        cmd.add("--tmpfs");
        cmd.add("/tmp:rw,noexec,nosuid,size=64m");

        if (containerConfig.getUser() != null) {
            cmd.add("--user");
            cmd.add(containerConfig.getUser());
        }

        if (containerConfig.isDropAllCapabilities()) {
            cmd.add("--cap-drop=ALL");
        }

        if (containerConfig.isNoNewPrivileges()) {
            cmd.add("--security-opt");
            cmd.add("no-new-privileges:true");
        }

        if (config.getSecurity().isEnableSeccomp()) {
            cmd.add("--security-opt");
            cmd.add("seccomp=unconfined");
        }

        cmd.add("-v");
        cmd.add(containerConfig.getWorkDirectory().toAbsolutePath() + ":/code:ro");

        cmd.add("-w");
        cmd.add("/code");

        cmd.add(containerConfig.getLanguage().getDockerImage());

        return cmd;
    }

    public ContainerConfig createConfig(ExecutionLanguage language, Path workDir, int timeLimitMs, int memoryLimitMb) {
        return ContainerConfig.builder()
            .containerId("exec-" + UUID.randomUUID().toString().substring(0, 12))
            .language(language)
            .workDirectory(workDir)
            .memoryLimitBytes((long) memoryLimitMb * 1024 * 1024)
            .cpuPeriod(config.getDocker().getCpuPeriod())
            .cpuQuota(config.getDocker().getCpuQuota())
            .timeLimitMs(timeLimitMs)
            .pidsLimit(config.getDocker().getPidsLimit())
            .networkMode(config.getDocker().getNetwork())
            .readOnlyRootFs(config.getDocker().isReadOnlyRootFs())
            .user(config.getDocker().getUser())
            .dropAllCapabilities(config.getDocker().isDropAllCapabilities())
            .noNewPrivileges(config.getSecurity().isNoNewPrivileges())
            .build();
    }

    public List<String> buildKillCommand(String containerId) {
        return List.of("docker", "kill", containerId);
    }

    public List<String> buildRemoveCommand(String containerId) {
        return List.of("docker", "rm", "-f", containerId);
    }

    public List<String> buildStatsCommand(String containerId) {
        return List.of("docker", "stats", "--no-stream", "--format", "{{.MemUsage}}", containerId);
    }
}
