package ie.atu.sw;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;

record Item(String base, String code) implements Comparable<Item> {
    public int compareTo(Item i) {
        if (this.base().length() != i.base().length()) {
            return i.base().length() - this.base().length();
        }

        return this.base().compareTo(i.base());
    }
}

public class Engine {
    private static final String SEPARATOR = ", ";
    private static String mappingFileName = "./encodings-10000.csv";
    private static String workFileName = "./lord.txt";
    private static String outputFileName = "./out.txt";
    private static Item[] words;
    private static Item[] suffixes;
    private static String[] workDocument;
    private static String[] plainVocabulary;

    public static boolean validateStringFilenameUsingIO(String filename) {
        File file = new File(filename);
        boolean created = false;
        try {
            created = file.createNewFile();
            return created;
        } catch (Exception e) {
            return created;
        } finally {
            if (created) {
                file.delete();
            }
        }
    }

    private static String[] readFile(String fileName) throws Exception {
        try {
            return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8).toArray(new String[0]);
        } catch (Exception e) {
            throw new Exception("Error reading file: " + fileName);
        }
    }

    private static void writeFile(String fileName, String[] lines) throws Exception {
        try (var fileWriter = new FileWriter(fileName)) {
            for (var i = 0; i < lines.length; i++) {
                fileWriter.write(lines[i] + "\n");
            }
        } catch (Exception e) {
            throw new Exception("Error writing file: " + fileName);
        }
    }

    private static String[] transformToLowCase(String lines[]) {
        for (var i = 0; i < lines.length; i++) {
            lines[i] = lines[i].toLowerCase();
        }

        return lines;
    }

    private static void parseVocabulary(String lines[]) {
        var suffixesCounter = 0;
        var wordsCounter = 0;

        for (var line : lines) {
            var parts = line.split(",");

            if (parts.length != 2) {
                continue;
            }

            if (parts[0].startsWith("@@")) {
                suffixesCounter += 1;
            } else {
                wordsCounter += 1;
            }
        }

        Engine.words = new Item[wordsCounter];
        Engine.suffixes = new Item[suffixesCounter];
        Engine.plainVocabulary = new String[wordsCounter + suffixesCounter];
        wordsCounter = 0;
        suffixesCounter = 0;

        for (var line : lines) {
            var parts = line.split(",");

            if (parts.length != 2) {
                continue;
            }

            Engine.plainVocabulary[wordsCounter + suffixesCounter] = parts[0];

            if (parts[0].startsWith("@@")) {
                Engine.suffixes[suffixesCounter] = new Item(parts[0].substring(2), parts[1]);
                suffixesCounter += 1;
            } else {
                Engine.words[wordsCounter] = new Item(parts[0], parts[1]);
                wordsCounter += 1;
            }
        }

        Arrays.sort(Engine.words, (a, b) -> a.compareTo(b));
        Arrays.sort(Engine.suffixes, (a, b) -> a.compareTo(b));
    }

    public static void readVocabulary() throws Exception {
        var lines = Engine.readFile(Engine.mappingFileName);

        if (lines.length == 0) {
            throw new Exception("Empty vocabulary!!!");
        }

        Engine.parseVocabulary(lines);
    }

    public static void uploadVocabulary() {
        // Using Scanner for Getting Input from User
        System.out.print("Input Mapping File Name [Default - " + Engine.mappingFileName + "]> ");
        Scanner s = new Scanner(System.in);
        var fileName = s.nextLine();

        if (fileName.length() != 0) {
            Engine.mappingFileName = fileName;
        }

        System.out.println("Current mapping file: " + Engine.mappingFileName);

        try {
            Engine.readVocabulary();
            System.out.println("Was Uploaded:");
            System.out.println("Words - " + Engine.words.length);
            System.out.println("Words Suffixes - " + Engine.suffixes.length);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void readWorkFile() throws Exception {
        var lines = Engine.readFile(Engine.workFileName);
        lines = Engine.transformToLowCase(lines);

        if (lines.length == 0) {
            throw new Exception("Empty work file!!!");
        }

        Engine.workDocument = lines;
    }

    public static void uploadWorkFile() {
        System.out.print("Input Work File Name [Default - " + Engine.workFileName + "]> ");
        Scanner s = new Scanner(System.in);
        var fileName = s.nextLine();

        if (fileName.length() != 0) {
            Engine.workFileName = fileName;
        }

        System.out.println("Current work file: " + Engine.workFileName);

        try {
            Engine.readWorkFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        if (Engine.workDocument != null) {
            System.out.println("Lines uploaded: " + Engine.workDocument.length);
        }

    }

    public static void specifyOutputFile() {
        System.out.print("Specify Output File Name [Default - " + Engine.outputFileName + "]> ");
        Scanner s = new Scanner(System.in);
        var fileName = s.nextLine();

        if (fileName.length() != 0) {
            if (Engine.validateStringFilenameUsingIO(fileName)) {
                Engine.outputFileName = fileName;
            } else {
                System.out.println("Invalid file name!!!");
            }
        }

        System.out.println("Current output file: " + Engine.outputFileName);
    }

    public static void showSystemStatus() {
        System.out.println("Current mapping file: " + Engine.mappingFileName);
        if (Engine.words != null && Engine.suffixes != null) {
            System.out.println("Was Uploaded:");
            System.out.println("Words - " + Engine.words.length);
            System.out.println("Words Suffixes - " + Engine.suffixes.length);
        } else {
            System.out.println("Mapping file is not uploaded.");
        }
        System.out.println();

        System.out.println("Current work file: " + Engine.workFileName);
        if (Engine.workDocument != null) {
            System.out.println("Lines uploaded: " + Engine.workDocument.length);
        } else {
            System.out.println("Work file is not uploaded.");
        }
        System.out.println();

        System.out.println("Current output file: " + Engine.outputFileName);
    }

    private static String decodeLine(String line) throws Exception {
        var decodedLine = "";
        var codes = line.split(Engine.SEPARATOR);

        try {
            for (var i = 0; i < codes.length; i++) {
                var code = codes[i];

                if (code.length() == 0)
                    continue;

                var codeInt = Integer.parseInt(code);
                var textItem = Engine.plainVocabulary[codeInt];

                if (textItem.startsWith("@@")) {
                    decodedLine = decodedLine.concat(textItem.substring(2));
                } else {
                    decodedLine = decodedLine.concat(i == 0 ? textItem : " ".concat(textItem));
                }
            }
        } catch (Exception e) {
            throw new Exception("Decoding error!!!");
        }

        return decodedLine;
    }

    public static void decodeTextFile() {
        if (Engine.plainVocabulary == null) {
            System.out.println("Mapping file is not uploaded.");
            return;
        }

        if (Engine.workDocument == null) {
            System.out.println("Work file is not uploaded.");
            return;
        }
        String[] outputDocument = new String[Engine.workDocument.length];

        System.out.println("Start processing...");
        System.out.println();

        try {
            for (var i = 0; i < workDocument.length; i++) {
                outputDocument[i] = Engine.decodeLine(workDocument[i]);
                Runner.printProgress(i + 1, workDocument.length);
            }

            Engine.writeFile(Engine.outputFileName, outputDocument);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String[] encodeToken(String token) {
        if (token.equals(" "))
            return new String[0];

        for (var word : Engine.words) {
            if (token.startsWith(word.base())) {
                if (token.length() == word.base().length()) {
                    return new String[] { word.code() };
                }

                var wordReminder = token.substring(word.base().length());

                for (var suffix : suffixes) {
                    if (wordReminder.length() > suffix.base().length()) {
                        break;
                    }

                    if (wordReminder.equals(suffix.base())) {
                        return new String[] { word.code(), suffix.code() };
                    }
                }
            }
        }

        return new String[] { "0" };
    }

    private static String encodeLine(String line) throws Exception {
        if (line.length() == 0)
            return line;

        var tokens = new String[line.length()];
        var tokenCounter = 0;
        var isNewWord = true;
        var currentToken = "";

        for (var ch : line.toCharArray()) {
            if (isNewWord) {
                if (Character.isLetterOrDigit(ch)) {
                    currentToken = currentToken.concat(Character.toString(ch));
                    isNewWord = false;
                    continue;
                } else {
                    tokens[tokenCounter] = Character.toString(ch);
                    tokenCounter += 1;
                    continue;
                }
            } else {
                if (Character.isLetterOrDigit(ch)) {
                    currentToken = currentToken.concat(Character.toString(ch));
                    continue;
                } else {
                    tokens[tokenCounter] = currentToken;
                    currentToken = "";
                    isNewWord = true;
                    tokenCounter += 1;
                    tokens[tokenCounter] = Character.toString(ch);
                    tokenCounter += 1;
                }
            }
        }

        if (!currentToken.equals("")) {
            tokens[tokenCounter] = currentToken;
            tokenCounter += 1;
        }

        var result = "";

        for (var i = 0; i < tokenCounter; i++) {
            var token = tokens[i];
            var encoded = Engine.encodeToken(token);

            for (var code : encoded) {
                result = result.concat(result.isEmpty() ? code : ", ".concat(code));
            }
        }

        return result;
    }

    public static void encodeTextFile() {
        if (Engine.plainVocabulary == null) {
            System.out.println("Mapping file is not uploaded.");
            return;
        }

        if (Engine.workDocument == null) {
            System.out.println("Work file is not uploaded.");
            return;
        }
        String[] outputDocument = new String[Engine.workDocument.length];

        System.out.println("Start processing...");
        System.out.println();

        try {
            var startTime = System.currentTimeMillis();

            for (var i = 0; i < workDocument.length; i++) {
                outputDocument[i] = Engine.encodeLine(workDocument[i]);
                Runner.printProgress(i + 1, workDocument.length);
            }

            var endTime = System.currentTimeMillis();
            System.out.println();
            System.out.println(endTime - startTime);

            Engine.writeFile(Engine.outputFileName, outputDocument);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
