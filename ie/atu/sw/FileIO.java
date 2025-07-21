package ie.atu.sw;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileIO {
    public static String[] readFile(String fileName) throws Exception {
        try {
            return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8).toArray(new String[0]);
        } catch (Exception e) {
            throw new Exception("Error reading file: " + fileName);
        }
    }

    public static void writeFile(String fileName, String[] lines) throws Exception {
        try (var fileWriter = new FileWriter(fileName)) {
            for (var i = 0; i < lines.length; i++) {
                fileWriter.write(lines[i] + "\n");
            }
        } catch (Exception e) {
            throw new Exception("Error writing file: " + fileName);
        }
    }
}
