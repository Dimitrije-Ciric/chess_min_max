package org.example.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemCommandExecutor {

    public String execute(String command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("cmd", "/c", command).start();

        System.out.println(process.waitFor());

        return new BufferedReader(new InputStreamReader(process.getInputStream())).lines().reduce("", (acc, str) -> { return acc + "\n" + str; });
    }

}
