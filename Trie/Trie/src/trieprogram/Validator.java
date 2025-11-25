package trieprogram;
import java.util.Scanner;

public class Validator {
    private static final int ALPHABET_SIZE = 26;

    /**
     * Checks if the dictionary is empty and displays appropriate message.
     *
     * @param trie the dictionary to check
     * @param operation the name of operation being performed (for error message)
     * @return true if dictionary is empty, false otherwise
     */
    public static boolean isDictionaryEmpty(Trie trie, String operation) {
        if (trie.isEmpty()) {
            System.out.println("!Dictionary is empty! No words to " + operation);
            return true;
        }

        return false;
    }

    /**
     * Validates if character is a valid English lowercase letter.
     *
     * @param c the character to validate
     * @return true if character is invalid (not between 'a' and 'z'), false otherwise
     */
    public static boolean isInvalidChar(char c) {
        return c < 'a' || c > 'z';
    }

    /**
     * Validates if string contains only valid English lowercase letters.
     *
     * @param str the string to validate
     * @return true if string contains invalid characters, false if all characters are valid
     */
    public static boolean isInvalidString(String str) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (isInvalidChar(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts a character to its corresponding array index in the trie.
     * 'a' becomes 0, 'b' becomes 1, ..., 'z' becomes 25.
     *
     * @param c the character to convert
     * @return the array index for the character
     * @throws IllegalArgumentException if character is not a valid English letter
     */
    public static int getCharIndex(char c) {
        if (isInvalidChar(c)) {
            throw new IllegalArgumentException("Invalid character: " + c);
        }

        return c - 'a';
    }

    /**
     * Returns the size of the English alphabet.
     *
     * @return the number of letters in the alphabet (26)
     */
    public static int getAlphabetSize() {
        return ALPHABET_SIZE;
    }

    /**
     * Validates and normalizes user input for words or prefixes.
     * Trims whitespace, converts to lowercase, and validates characters.
     *
     * @param input the user input to validate
     * @param inputType the type of input ("Word" or "Prefix" for error messages)
     * @return the normalized and validated input string
     * @throws IllegalArgumentException if input is empty or contains invalid characters
     */
    public static String validateInput(String input, String inputType) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(inputType + " cannot be empty");
        }

        String trimmedInput = input.trim().toLowerCase();

        for (int i = 0; i < trimmedInput.length(); i++) {
            char c = trimmedInput.charAt(i);

            if (isInvalidChar(c)) {
                throw new IllegalArgumentException(inputType + " can only contain English letters (a-z)");
            }
        }

        return trimmedInput;
    }

    /**
     * Checks input for validation errors and returns appropriate error message.
     *
     * @param input the input to validate
     * @param inputType the type of input ("Word" or "Prefix")
     * @return error message if input is invalid, null if input is valid
     */
    public static String getValidationError(String input, String inputType) {
        if (input == null || input.trim().isEmpty()) {
            return inputType + " cannot be empty";
        }

        String trimmedInput = input.trim().toLowerCase();

        for (int i = 0; i < trimmedInput.length(); i++) {
            char c = trimmedInput.charAt(i);

            if (isInvalidChar(c)) {
                return inputType + " can only contain English letters (a-z)";
            }
        }

        return null;
    }

    /**
     * Processes the addition of a new word to the dictionary.
     * Validates input, checks for duplicates, and adds the word if valid.
     *
     * @param scanner the scanner for user input
     * @param trie the dictionary to add the word to
     */
    public static void addWord(Scanner scanner, Trie trie) {
        while (true) {
            System.out.print("Enter word to add (or 'back' to cancel): ");
            String word = scanner.nextLine().trim();

            if (word.equalsIgnoreCase("back")) {
                System.out.println("Operation cancelled");
                return;
            }

            try {
                String validatedWord = validateInput(word, "Word");

                if (trie.contains(validatedWord)) {
                    System.out.println("Error: Word '" + validatedWord + "' already exists in dictionary");
                    System.out.println("Please try again or enter 'back' to cancel\n");
                    continue;
                }

                trie.insert(validatedWord);
                System.out.println("Word '" + validatedWord + "' added successfully!");
                System.out.println("Total words in dictionary: " + trie.getWordCount() + "\n");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again or enter 'back' to cancel\n");
            }
        }
    }

    /**
     * Processes checking if a word exists in the dictionary.
     * Validates input and displays search results.
     * Returns the word if found for visualization purposes.
     *
     * @param scanner the scanner for user input
     * @param trie the dictionary to search in
     * @return the validated word if found, null if not found or operation cancelled
     */
    public static String checkWord(Scanner scanner, Trie trie) {
        if (isDictionaryEmpty(trie, "check")) {
            return null;
        }

        while (true) {
            System.out.print("Enter word to check (or 'back' to cancel): ");
            String word = scanner.nextLine().trim();

            if (word.equalsIgnoreCase("back")) {
                System.out.println("Operation cancelled");
                return null;
            }

            String error = getValidationError(word, "Word");
            if (error != null) {
                System.out.println("Error: " + error);
                System.out.println("Please try again or enter 'back' to cancel\n");
                continue;
            }

            String validatedWord = word.toLowerCase();
            boolean exists = trie.contains(validatedWord);

            if (exists) {
                System.out.println("Word '" + validatedWord + "' was found in dictionary!\n");
                return validatedWord;
            } else {
                System.out.println("Word '" + validatedWord + "' was not found in dictionary\n");
                return null;
            }
        }
    }

    /**
     * Processes checking if any words start with the given prefix.
     * Validates input and displays prefix search results.
     * Returns the prefix if found for visualization purposes.
     *
     * @param scanner the scanner for user input
     * @param trie the dictionary to search in
     * @return the validated prefix if found, null if not found or operation cancelled
     */
    public static String checkPrefix(Scanner scanner, Trie trie) {
        if (isDictionaryEmpty(trie, "check prefixes")) {
            return null;
        }

        while (true) {
            System.out.print("Enter prefix to check (or 'back' to cancel): ");
            String prefix = scanner.nextLine().trim();

            if (prefix.equalsIgnoreCase("back")) {
                System.out.println("Operation cancelled");
                return null;
            }

            String error = getValidationError(prefix, "Prefix");
            if (error != null) {
                System.out.println("Error: " + error);
                System.out.println("Please try again or enter 'back' to cancel\n");
                continue;
            }

            String validatedPrefix = prefix.toLowerCase();
            boolean exists = trie.startsWith(validatedPrefix);

            if (exists) {
                System.out.println("There are words starting with '" + validatedPrefix + "'!\n");
                return validatedPrefix;
            } else {
                System.out.println("No words starting with '" + validatedPrefix + "' found\n");
                return null;
            }
        }
    }

    /**
     * Finds and displays all words that start with the given prefix.
     * Validates input and displays matching words.
     * Returns the prefix if words found for visualization purposes.
     *
     * @param scanner the scanner for user input
     * @param trie the dictionary to search in
     * @return the validated prefix if words found, null if no words or operation cancelled
     */
    public static String findWordsByPrefix(Scanner scanner, Trie trie) {
        if (isDictionaryEmpty(trie, "find")) {
            return null;
        }

        while (true) {
            System.out.print("Enter prefix to find words (or 'back' to cancel): ");
            String prefix = scanner.nextLine().trim();

            if (prefix.equalsIgnoreCase("back")) {
                System.out.println("Operation cancelled");
                return null;
            }

            String error = getValidationError(prefix, "Prefix");
            if (error != null) {
                System.out.println("Error: " + error);
                System.out.println("Please try again or enter 'back' to cancel\n");
                continue;
            }

            String validatedPrefix = prefix.toLowerCase();
            String[] words = trie.getByPrefix(validatedPrefix);

            if (words.length == 0) {
                System.out.println("No words with prefix '" + validatedPrefix + "' found\n");
                return null;
            } else {
                System.out.println("Found " + words.length + " words with prefix '" + validatedPrefix + "':");
                for (int i = 0; i < words.length; i++) {
                    System.out.println((i + 1) + ". " + words[i]);
                }
                System.out.println();
                return validatedPrefix;
            }
        }
    }

    /**
     * Processes removal of a word from the dictionary.
     * Validates input and removes the word if it exists.
     *
     * @param scanner the scanner for user input
     * @param trie the dictionary to remove from
     */
    public static void removeWord(Scanner scanner, Trie trie) {
        if (isDictionaryEmpty(trie, "remove")) {
            System.out.println("!Dictionary is empty! No words to remove");
            return;
        }

        while (true) {
            System.out.print("Enter word to remove (or 'back' to cancel): ");
            String word = scanner.nextLine().trim();

            if (word.equalsIgnoreCase("back")) {
                System.out.println("Operation cancelled");
                return;
            }

            String error = getValidationError(word, "Word");
            if (error != null) {
                System.out.println("Error: " + error);
                System.out.println("Please try again or enter 'back' to cancel");
                continue;
            }

            String validatedWord = word.toLowerCase();
            boolean removed = trie.removeWord(validatedWord);

            if (removed) {
                System.out.println("Word '" + validatedWord + "' removed successfully!");
                System.out.println("Total words in dictionary: " + trie.getWordCount() + "\n");
            } else {
                System.out.println("Word '" + validatedWord + "' not found in dictionary\n");
            }
            break;
        }
    }

    /**
     * Processes complete deletion of all dictionary data.
     * Requires user confirmation before proceeding.
     *
     * @param scanner the scanner for user input
     * @param trie the dictionary to clear
     */
    public static void deleteAllData(Scanner scanner, Trie trie) {
        if (isDictionaryEmpty(trie, "delete")) {
            System.out.println("!Dictionary is empty! No data to delete");
            return;
        }

        System.out.print("Delete ALL dictionary data? This cannot be undone! (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            trie.deleteAllData();
            System.out.println("All dictionary data deleted successfully!\n");
        } else {
            System.out.println("Operation cancelled\n");
        }
    }
}