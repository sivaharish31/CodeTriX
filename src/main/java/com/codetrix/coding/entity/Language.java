package com.codetrix.coding.entity;

import lombok.Getter;

@Getter
public enum Language {
    C("c", "c", "main.c"),
    CPP("cpp", "cpp", "main.cpp"),
    JAVA("java", "java", "Main.java"),
    PYTHON("python", "py", "main.py");

    private final String name;
    private final String extension;
    private final String defaultFileName;

    Language(String name, String extension, String defaultFileName) {
        this.name = name;
        this.extension = extension;
        this.defaultFileName = defaultFileName;
    }

    public static Language fromString(String lang) {
        for (Language l : values()) {
            if (l.name.equalsIgnoreCase(lang) || l.name().equalsIgnoreCase(lang)) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unsupported language: " + lang);
    }
}
