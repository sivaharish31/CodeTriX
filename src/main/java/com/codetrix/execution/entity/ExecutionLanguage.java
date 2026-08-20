package com.codetrix.execution.entity;

public enum ExecutionLanguage {
    C("c", "main.c", "gcc -o main main.c -lm", "./main"),
    CPP("cpp", "main.cpp", "g++ -o main main.cpp -std=c++17", "./main"),
    JAVA("java", "Main.java", "javac Main.java", "java Main"),
    PYTHON("python", "main.py", null, "python3 main.py");

    private final String dockerImage;
    private final String sourceFileName;
    private final String compileCommand;
    private final String runCommand;

    ExecutionLanguage(String dockerImage, String sourceFileName, String compileCommand, String runCommand) {
        this.dockerImage = dockerImage;
        this.sourceFileName = sourceFileName;
        this.compileCommand = compileCommand;
        this.runCommand = runCommand;
    }

    public String getDockerImage() {
        return "codetrix-" + dockerImage;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public String getCompileCommand() {
        return compileCommand;
    }

    public String getRunCommand() {
        return runCommand;
    }

    public boolean requiresCompilation() {
        return compileCommand != null;
    }
}
