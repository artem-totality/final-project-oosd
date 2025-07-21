package ie.atu.sw;

import java.util.Scanner;

public class Menu {
    // method prints out the menu in the loop
    public static void performMenu() {
        Scanner s = new Scanner(System.in);

        while (true) {
            ConsoleIO.clearConsole();
            System.out.println(ConsoleColour.WHITE);
            System.out.println("************************************************************");
            System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
            System.out.println("*                                                          *");
            System.out.println("*              Encoding Words with Suffixes                *");
            System.out.println("*                                                          *");
            System.out.println("************************************************************");
            System.out.println("(1) Upload Mapping File");
            System.out.println("(2) Upload Work Text File");
            System.out.println("(3) Specify Output File (default: ./out.txt)");
            System.out.println("(4) Current System Status");
            System.out.println("(5) Encode Text File");
            System.out.println("(6) Decode Text File");
            System.out.println("(7) Quit");

            // Output a menu of options and solicit text from the user
            System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
            System.out.print("Select Option [1-?]> ");

            // read user choice
            var choice = s.nextLine();
            ConsoleIO.clearConsole();

            // perform user choice
            switch (choice) {
                case "1":
                    Engine.uploadVocabulary();
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
                case "2":
                    Engine.uploadWorkFile();
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
                case "3":
                    Engine.specifyOutputFile();
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
                case "4":
                    Engine.showSystemStatus();
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
                case "5":
                    Engine.encodeTextFile();
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
                case "6":
                    Engine.decodeTextFile();
                    System.out.println();
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
                case "7":
                    System.out.println("Bye, bye!");
                    return;
                // if was entered wrong option ask to repeat the process
                default:
                    System.out.println("Please input number between 1 and 7!");
                    System.out.println();
                    System.out.println("Please press Enter to continue...");
                    s.nextLine();
                    break;
            }
        }
    }
}
