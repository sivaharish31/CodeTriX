package com.codetrix.execution.docker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProcessRunner {

    public ProcessResult run(List<String> command, String input, int timeoutMs, int maxOutputSize) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(false);

        long startTime = System.currentTimeMillis();
        Process process = null;

        try {
            process = pb.start();

            if (input != null && !input.isEmpty()) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            } else {
                process.getOutputStream().close();
            }

            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();

            Thread stdoutReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    int totalRead = 0;
                    while ((line = reader.readLine()) != null && totalRead < maxOutputSize) {
                        if (stdout.length() > 0) stdout.append("\n");
                        stdout.append(line);
                        totalRead += line.length();
                    }
                } catch (IOException e) {
                    log.debug("Error reading stdout: {}", e.getMessage());
                }
            });

            Thread stderrReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    int totalRead = 0;
                    while ((line = reader.readLine()) != null && totalRead < maxOutputSize) {
                        if (stderr.length() > 0) stderr.append("\n");
                        stderr.append(line);
                        totalRead += line.length();
                    }
                } catch (IOException e) {
                    log.debug("Error reading stderr: {}", e.getMessage());
                }
            });

            stdoutReader.start();
            stderrReader.start();

            boolean completed = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            long executionTime = System.currentTimeMillis() - startTime;

            stdoutReader.join(1000);
            stderrReader.join(1000);

            if (!completed) {
                process.destroyForcibly();
                return ProcessResult.builder()
                    .timedOut(true)
                    .exitCode(-1)
                    .stdout(stdout.toString())
                    .stderr(stderr.toString())
                    .executionTimeMs(executionTime)
                    .build();
            }

            return ProcessResult.builder()
                .timedOut(false)
                .exitCode(process.exitValue())
                .stdout(stdout.toString())
                .stderr(stderr.toString())
                .executionTimeMs(executionTime)
                .build();

        } catch (IOException e) {
            log.error("Process execution failed: {}", e.getMessage());
            return ProcessResult.builder()
                .timedOut(false)
                .exitCode(-1)
                .stderr("Process execution failed: " + e.getMessage())
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            return ProcessResult.builder()
                .timedOut(true)
                .exitCode(-1)
                .stderr("Process interrupted")
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
        }
    }

    public ProcessResult runSimple(List<String> command, int timeoutMs) {
        return run(command, null, timeoutMs, 65536);
    }
}
