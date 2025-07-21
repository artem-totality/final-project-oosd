package ie.atu.sw;

import java.util.Arrays;
import java.util.Scanner;

// record Item has two properties base and code of type String
// base stores text part of the mapping file
// code stores text value of the corresponding code
// also Item implements interface Comparable
// it needs for sorting words and suffixes in order to optimisation process of encoding
record Item(String base, String code) implements Comparable<Item> {
    public int compareTo(Item i) {
        // firstly we sort words by length
        if (this.base().length() != i.base().length()) {
            return i.base().length() - this.base().length();
        }

        // and then by alphabet
        return this.base().compareTo(i.base());
    }
}

public class Engine {
    // predefine SEPARATOR that we use during encoding process to separate code
    // values
    private static final String SEPARATOR = ", ";
    // variable for storing mapping file name with predefined default value
    private static String mappingFileName = "./encodings-10000.csv";
    // variable for storing work file name with predefined default value
    private static String workFileName = "./test.txt";
    // variable for storing output file name with predefined default value
    private static String outputFileName = "./out.txt";
    // work document stored as array of String
    private static String[] workDocument;
    // array for storing mapped words with codes is used during encoding
    private static Item[] words;
    // array for storing mapped suffixes with codes is used during encoding
    private static Item[] suffixes;
    // array of mapped words uploaded as plain String array where every
    // code of the word or suffix is equal to its index
    // this array is used during decoding
    private static String[] plainVocabulary;

    // Method transforms all lines of the work document to lower case
    // cause mapping file consists of only low case words and suffixes
    private static String[] transformToLowerCase(String[] lines) {
        var result = new String[lines.length];

        for (var i = 0; i < lines.length; i++) {
            result[i] = lines[i].toLowerCase();
        }

        return result;
    }

    // Method parses mapping words
    // separate words and suffixes and sotrs them
    private static void parseVocabulary(String lines[]) throws Exception {
        // reset words and suffixes counters
        var suffixesCounter = 0;
        var wordsCounter = 0;

        // firstly define count of words and suffixes
        for (var line : lines) {
            // split line by coma
            var parts = line.split(",");

            // if a line consists of more or less than two parts (base, code)
            // then skip this line
            if (parts.length != 2) {
                continue;
            }

            // count separately words and suffixes
            if (parts[0].startsWith("@@")) {
                suffixesCounter += 1;
            } else {
                wordsCounter += 1;
            }
        }

        // initialize arrays with defined count of words and suffixes
        Engine.words = new Item[wordsCounter];
        Engine.suffixes = new Item[suffixesCounter];
        // initialize plain vocabulary with size words plus suffixes
        Engine.plainVocabulary = new String[wordsCounter + suffixesCounter];
        // reset words and suffixes counters
        wordsCounter = 0;
        suffixesCounter = 0;

        try {
            // for every line in array
            for (var line : lines) {
                // split line by coma
                var parts = line.split(",");

                // if a line consists of more or less than two parts (base, code)
                // then skip this line
                if (parts.length != 2) {
                    continue;
                }

                // save current word or suffix with corresponding index to the plain voacabulary
                Engine.plainVocabulary[wordsCounter + suffixesCounter] = parts[0];

                // check is it word or suffix and then add it to appropriate array
                // also increase corresponding counter
                if (parts[0].startsWith("@@")) {
                    Engine.suffixes[suffixesCounter] = new Item(parts[0].substring(2), parts[1]);
                    suffixesCounter += 1;
                } else {
                    Engine.words[wordsCounter] = new Item(parts[0], parts[1]);
                    wordsCounter += 1;
                }
            }

            // in case of error throw exception with appropriate message
        } catch (Exception e) {
            throw new Exception("Vocabulary parsing ERROR!!!");
        }

        // After all words and suffixes uploaded sort them for further work
        Arrays.sort(Engine.words, (a, b) -> a.compareTo(b));
        Arrays.sort(Engine.suffixes, (a, b) -> a.compareTo(b));
    }

    // method reads and parses vocabulary from mapping file
    public static void readVocabulary() throws Exception {
        // read file as String array
        var lines = FileIO.readFile(Engine.mappingFileName);

        // check if file is not emty
        if (lines.length == 0) {
            throw new Exception("Empty vocabulary!!!");
        }

        // tries to parse uploaded file
        Engine.parseVocabulary(lines);
    }

    // method sets mapping file name and invokes readVocabulary method
    public static void uploadVocabulary() {
        // using scanner for getting input from user
        System.out.print("Input Mapping File Name [Default - " + Engine.mappingFileName + "]> ");
        Scanner s = new Scanner(System.in);
        // read user input to the fileName variable
        var fileName = s.nextLine();

        // if fileName variable is not emty - set new mapping file name
        // else skip and use default (predefined) value
        if (fileName.length() != 0) {
            Engine.mappingFileName = fileName;
        }

        // print out current mapping file name
        System.out.println("Current mapping file: " + Engine.mappingFileName);

        // try to upload vocabulary from the mapping file
        try {
            Engine.readVocabulary();
            System.out.println("Was Uploaded:");
            System.out.println("Words - " + Engine.words.length);
            System.out.println("Words Suffixes - " + Engine.suffixes.length);

            // in case of exception - print error message
            // and reset all words, suffixes and plainVocabulary to null
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Engine.words = null;
            Engine.suffixes = null;
            Engine.plainVocabulary = null;
        }
    }

    // method reads work file and saves it to the workDocument variable
    // as String array
    public static void readWorkFile() throws Exception {
        // read work file as String array
        var lines = FileIO.readFile(Engine.workFileName);

        // check if file is empty then throw exception
        if (lines.length == 0) {
            throw new Exception("Empty work file!!!");
        }

        // transform all uploaded lines to lower case
        lines = Engine.transformToLowerCase(lines);

        // save all lines to workDocument variable for further performing
        Engine.workDocument = lines;
    }

    // method sets work file name and invokes readWorkFile method
    public static void uploadWorkFile() {
        // using scanner for getting input from user
        System.out.print("Input Work File Name [Default - " + Engine.workFileName + "]> ");
        Scanner s = new Scanner(System.in);
        // read user input to the fileName variable
        var fileName = s.nextLine();

        // if fileName variable is not emty - set new work file name
        // else skip and use default (predefined) value
        if (fileName.length() != 0) {
            Engine.workFileName = fileName;
        }

        // print out current work file name
        System.out.println("Current work file: " + Engine.workFileName);

        // try to upload work document from the specified file
        try {
            Engine.readWorkFile();
            System.out.println("Lines uploaded: " + Engine.workDocument.length);

            // in case of exception - print error message
            // and reset work document to null
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Engine.workDocument = null;
        }
    }

    // method sets ouitput file name
    // and validates it
    public static void specifyOutputFile() {
        // using scanner for getting input from user
        System.out.print("Specify Output File Name [Default - " + Engine.outputFileName + "]> ");
        Scanner s = new Scanner(System.in);
        // read user input to the fileName variable
        var fileName = s.nextLine();

        // validate output file name
        // (check is it possible to use such file name for output)
        if (fileName.length() != 0) {
            if (FileIO.validateStringFilenameUsingIO(fileName)) {
                Engine.outputFileName = fileName;
            } else {
                // if entered file name is invalid show appropriate message
                System.out.println("Invalid file name!!!");
            }
        }

        // print out current output file name
        System.out.println("Current output file: " + Engine.outputFileName);
    }

    // show current system status
    public static void showSystemStatus() {
        // print out current mappping file name
        System.out.println("Current mapping file: " + Engine.mappingFileName);
        // print out whether uploaded or not words and suffixes and statistic
        if (Engine.words != null && Engine.suffixes != null) {
            System.out.println("Was Uploaded:");
            System.out.println("Words - " + Engine.words.length);
            System.out.println("Words Suffixes - " + Engine.suffixes.length);
        } else {
            System.out.println("Mapping file is not uploaded.");
        }

        // print out current output file name
        System.out.println();
        System.out.println("Current work file: " + Engine.workFileName);
        // print out whether uploaded or not work document
        if (Engine.workDocument != null) {
            System.out.println("Lines uploaded: " + Engine.workDocument.length);
        } else {
            System.out.println("Work file is not uploaded.");
        }

        // print out current output file name
        System.out.println();
        System.out.println("Current output file: " + Engine.outputFileName);
    }

    // method decodes one line from codes to text
    private static String decodeLine(String line) throws Exception {
        // initialise resulting string
        var decodedLine = "";
        // split line into codes using predefined SEPARATOR
        var codes = line.split(Engine.SEPARATOR);

        // try to restore string using obtained codes
        try {
            // for every code in codes
            for (var code : codes) {
                // skip empty lynes
                if (code.length() == 0)
                    continue;

                // parse code as integer number
                var codeInt = Integer.parseInt(code);
                // get corresponding word or suffix from plainVocabulary using integer index
                var textItem = Engine.plainVocabulary[codeInt];

                // check wether it is word or suffix and concatinate it
                // to resulting string with corresponding spaces
                if (textItem.startsWith("@@")) {
                    decodedLine = decodedLine.concat(textItem.substring(2));
                } else {
                    decodedLine = decodedLine.concat(decodedLine.isEmpty() ? textItem : " ".concat(textItem));
                }
            }
            // in case of error throw new exception with appropriate message
        } catch (Exception e) {
            System.out.println();
            throw new Exception("Decoding error!!!");
        }

        // return result
        return decodedLine;
    }

    // method decodes uploaded work document
    public static void decodeTextFile() {
        // check if vocabulary is uploaded
        if (Engine.plainVocabulary == null) {
            System.out.println("Mapping file is not uploaded.");
            return;
        }

        // check if work document is uploaded
        if (Engine.workDocument == null) {
            System.out.println("Work file is not uploaded.");
            return;
        }
        String[] outputDocument = new String[Engine.workDocument.length];

        System.out.println("Start processing...");
        System.out.println();

        try {
            // try to decode uploaded work document line by line
            // also print current progress
            for (var i = 0; i < workDocument.length; i++) {
                outputDocument[i] = Engine.decodeLine(workDocument[i]);
                ConsoleIO.printProgress(i + 1, workDocument.length);
            }

            // in case of success save decoded document to output file
            FileIO.writeFile(Engine.outputFileName, outputDocument);

            // in case of error print corresponding error message
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // method encodes given token
    private static String[] encodeToken(String token) {
        // if a token equals to space then just skip it
        if (token.equals(" "))
            return new String[0];

        // then try to search appropirate word and suffix
        for (var word : Engine.words) {
            // if token start with given word
            if (token.startsWith(word.base())) {
                // if token and word length are equal then - match
                // return corresponding code [word]
                if (token.length() == word.base().length()) {
                    return new String[] { word.code() };
                }

                // if token longer than word - obtain reminder
                var wordReminder = token.substring(word.base().length());

                // try to pick up appropriate suffix
                for (var suffix : suffixes) {
                    // if on certain iteration suffix become shorter than reminder
                    // then break from the loop cause suffixes sorted from longer to shorter
                    // and there is no point in continuing the search
                    if (wordReminder.length() > suffix.base().length()) {
                        break;
                    }

                    // if we found exact suffix then return both codes
                    // [word, suffix]
                    if (wordReminder.equals(suffix.base())) {
                        return new String[] { word.code(), suffix.code() };
                    }
                }
            }
        }

        // if we get here - nothing was found
        // return code ["0"] - "[???]"
        return new String[] { "0" };
    }

    // method divides line on tokens and encodes it
    private static String encodeLine(String line) throws Exception {
        // if the line is empty just return it
        if (line.length() == 0)
            return line;

        // allocate for the tokens array with length equal
        // to numbers of string characters (atmost case)
        var tokens = new String[line.length()];
        // initialise variablles before starting tokenisation
        var tokenCounter = 0;
        var isNewWord = true;
        var currentToken = "";

        // in the loop split the line into tokens
        // word - continuous sequence letters or digits
        // all another characters consider as separate tokens
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

        // if we have last unadded word - add it to the tokens array
        if (!currentToken.equals("")) {
            tokens[tokenCounter] = currentToken;
            tokenCounter += 1;
        }

        // initialise resulting string
        var result = "";

        // in the loop encode every token and concatinate
        // codes into resullting string
        for (var i = 0; i < tokenCounter; i++) {
            var token = tokens[i];
            var encoded = Engine.encodeToken(token);

            for (var code : encoded) {
                result = result.concat(result.isEmpty() ? code : ", ".concat(code));
            }
        }

        // return resulting string
        return result;
    }

    // method encode work document
    public static void encodeTextFile() {
        // check if vocabulary uploaded
        if (Engine.plainVocabulary == null) {
            System.out.println("Mapping file is not uploaded.");
            return;
        }

        // check if work document uploaded
        if (Engine.workDocument == null) {
            System.out.println("Work file is not uploaded.");
            return;
        }
        String[] outputDocument = new String[Engine.workDocument.length];

        System.out.println("Start processing...");
        System.out.println();

        // try to encode work document
        try {
            // define start time
            var startTime = System.currentTimeMillis();

            // in the loop encode line by line and show progress
            for (var i = 0; i < workDocument.length; i++) {
                outputDocument[i] = Engine.encodeLine(workDocument[i]);
                ConsoleIO.printProgress(i + 1, workDocument.length);
            }

            // define end time
            var endTime = System.currentTimeMillis();
            // print out time spent
            System.out.println();
            System.out.println();
            System.out.println("File " + Engine.workFileName + "was encoded for: " + (endTime - startTime) + " ms");

            // write result to the output file
            FileIO.writeFile(Engine.outputFileName, outputDocument);

            // in case of error show appropriate message
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
