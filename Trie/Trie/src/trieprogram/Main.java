package trieprogram;
import java.util.Scanner;

public class Main {
    private static final Trie trie = new Trie(); //The main dictionary data structure
    private static final Scanner scanner = new Scanner(System.in); //Scanner for reading user input from console

    public static void main(String[] args) {
        TrieVisualizer visualizer = new TrieVisualizer(trie); //Initializing the GUI visualizer
        visualizer.setVisible(true);

        while (true) {
            printMenu();
            System.out.print("Please enter your choice(0-7): ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 0:
                        System.out.println("Goodbye!");
                        scanner.close();
                        visualizer.dispose();
                        return;
                    case 1:
                        Validator.addWord(scanner, trie);
                        visualizer.refreshTrie();
                        break;
                    case 2:
                        String checkWord = Validator.checkWord(scanner, trie);
                        if (checkWord != null) {
                            visualizer.highlightWord(checkWord);
                        }
                        break;
                    case 3:
                        String checkPrefix = Validator.checkPrefix(scanner, trie);
                        if (checkPrefix != null) {
                            visualizer.highlightPrefix(checkPrefix);
                        }
                        break;
                    case 4:
                        String wordPrefix = Validator.findWordsByPrefix(scanner, trie);
                        if (wordPrefix != null) {
                            visualizer.highlightWordsByPrefix(wordPrefix);
                        }
                        break;
                    case 5:
                        Validator.removeWord(scanner, trie);
                        visualizer.refreshTrie();
                        break;
                    case 6:
                        trie.printAllWords();
                        break;
                    case 7:
                        Validator.deleteAllData(scanner, trie);
                        visualizer.refreshTrie();
                        break;
                    default:
                        System.out.println("!Invalid choice! Please enter number (0-7)!");
                }
            } catch (NumberFormatException e) {
                System.out.println("!Invalid choice! Please enter a number (0-7), not text!!");
            }
        }
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void printMenu() {
        System.out.println("\n======== Trie Menu ========");
        System.out.println("0. Exit");
        System.out.println("1. Add word");
        System.out.println("2. Check if word exists");
        System.out.println("3. Check prefix");
        System.out.println("4. Find words by prefix");
        System.out.println("5. Remove word");
        System.out.println("6. Show all words");
        System.out.println("7. Delete all data");
    }
}