package com.codetrix.execution.service;

import com.codetrix.execution.config.ExecutionConfig;
import com.codetrix.execution.entity.ExecutionLanguage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkspaceManager {

    private final ExecutionConfig config;

    public Workspace createWorkspace(ExecutionLanguage language, String sourceCode) throws IOException {
        String workspaceId = UUID.randomUUID().toString();
        Path workDir = Paths.get(config.getTempDirectory(), workspaceId);

        Files.createDirectories(workDir);

        Path sourceFile = workDir.resolve(language.getSourceFileName());
        Files.writeString(sourceFile, sourceCode, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        log.debug("Created workspace {} for {}", workspaceId, language);
        return new Workspace(workspaceId, workDir, sourceFile);
    }

    public void writeInputFile(Path workDir, String input, String filename) throws IOException {
        Path inputFile = workDir.resolve(filename);
        Files.writeString(inputFile, input != null ? input : "",
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void cleanup(Workspace workspace) {
        if (!config.isCleanupEnabled()) {
            log.debug("Cleanup disabled, skipping workspace {}", workspace.getId());
            return;
        }

        try {
            deleteRecursively(workspace.getWorkDir());
            log.debug("Cleaned up workspace {}", workspace.getId());
        } catch (IOException e) {
            log.warn("Failed to cleanup workspace {}: {}", workspace.getId(), e.getMessage());
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class Workspace {
        private String id;
        private Path workDir;
        private Path sourceFile;
    }
}
