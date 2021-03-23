package com.niklasarndt.jlox.tests;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {

    public static String readResourceLoxFile(String filename) {
        try {
            return Files.readString(Paths.get(TestUtils.class.getClassLoader()
                    .getResource("loxsrc/" + filename).toURI()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Could not parse resource file");
        }
    }
}
