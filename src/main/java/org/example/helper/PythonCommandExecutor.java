package org.example.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonCommandExecutor {

    public String execute(String command) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{
                    "powershell", "-Command", "python -c", String.format("'%s'", command)
            });

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script failed with exit code: " + exitCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
