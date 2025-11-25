package trieprogram;

public class Trie {
    private TrieNode root; //Root node of the trie
    private String[] allWords; //Array buffer for storing all words (for persistence)
    private int wordCount; //Current number of words in the trie

    /**
     * Constructs a new Trie and loads existing data from storage.
     */
    public Trie() {
        this.root = new TrieNode();
        this.allWords = DataPersistence.loadTrieData();
        this.wordCount = allWords.length;

        for (int i = 0; i < wordCount; i++) {
            insertWithoutSave(allWords[i]);
        }
    }

    /**
     * Inserts a new word into the trie.
     *
     * @param word the word to insert
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        if (contains(word)) {
            return;
        }

        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = Validator.getCharIndex(c);

            if (current.getChildren()[index] == null) {
                current.getChildren()[index] = new TrieNode();
            }
            current = current.getChildren()[index];
        }

        current.setEndOfWord(true);
        addWordToArray(word);
        DataPersistence.saveTrieData(getAllWords());
    }

    /**
     * Checks if the trie contains the specified word.
     *
     * @param word the word to search for
     * @return true if word exists, false otherwise
     */
    public boolean contains(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        if (Validator.isInvalidString(word)) {
            return false;
        }

        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = Validator.getCharIndex(c);
            TrieNode node = current.getChildren()[index];

            if (node == null) {
                return false;
            }
            current = node;
        }

        return current.isEndOfWord();
    }

    /**
     * Checks if any word in the trie starts with the given prefix.
     *
     * @param prefix the prefix to check
     * @return true if prefix exists, false otherwise
     */
    public boolean startsWith(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return false;
        }

        if (Validator.isInvalidString(prefix)) {
            return false;
        }

        TrieNode current = root;

        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            int index = Validator.getCharIndex(c);
            TrieNode node = current.getChildren()[index];

            if (node == null) {
                return false;
            }

            current = node;
        }

        return true;
    }

    /**
     * Removes a word from the trie.
     *
     * @param word the word to remove
     * @return true if word was removed, false if not found
     */
    public boolean removeWord(String word) {
        if (word == null || word.isEmpty() || !contains(word)) {
            return false;
        }

        int removeIndex = -1;

        for (int i = 0; i < wordCount; i++) {
            if (allWords[i].equals(word)) {
                removeIndex = i;
                break;
            }
        }

        if (removeIndex == -1) {
            return false;
        }

        for (int i = removeIndex; i < wordCount - 1; i++) {
            allWords[i] = allWords[i + 1];
        }

        allWords[wordCount - 1] = null;
        wordCount--;

        this.root = new TrieNode();

        for (int i = 0; i < wordCount; i++) {
            insertWithoutSave(allWords[i]);
        }

        DataPersistence.saveTrieData(getAllWords());
        return true;
    }

    /**
     * Checks if the trie contains any words.
     *
     * @return true if trie is empty, false otherwise
     */
    public boolean isEmpty() {
        return wordCount == 0;
    }

    /**
     * Gets the number of words in the trie.
     *
     * @return word count
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Finds all words that start with the given prefix.
     *
     * @param prefix the prefix to search for
     * @return array of matching words
     */
    public String[] getByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new String[0];
        }

        if (Validator.isInvalidString(prefix)) {
            return new String[0];
        }

        TrieNode prefixNode = findPrefixNode(prefix);
        if (prefixNode == null) {
            return new String[0];
        }

        int foundCount = countWords(prefixNode);
        String[] result = new String[foundCount];
        int[] index = new int[1];
        collectWordsToArrayDFS(prefixNode, prefix, result, index);
        return result;
    }

    /**
     * Prints all words in the trie to console.
     */
    public void printAllWords() {
        if (Validator.isDictionaryEmpty(this, "print")) {
            return;
        }

        System.out.println("\n======== All words ========");
        String[] allWords = new String[wordCount];
        int[] currentIndex = new int[1];
        collectWordsToArrayDFS(root, "", allWords, currentIndex);

        for (int i = 0; i < allWords.length; i++) {
            System.out.println((i + 1) +". " + allWords[i]);
        }
    }

    /**
     * Deletes all words from the trie and persistent storage.
     */
    public void deleteAllData() {
        this.root = new TrieNode();
        this.allWords = new String[10];
        this.wordCount = 0;
        DataPersistence.deleteSavedData();
    }

    /**
     * Inserts word without saving to persistence (for initialization)
     */
    private void insertWithoutSave(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = Validator.getCharIndex(c);

            if (current.getChildren()[index] == null) {
                current.getChildren()[index] = new TrieNode();
            }

            current = current.getChildren()[index];
        }

        current.setEndOfWord(true);
    }

    /**
     * Adds a word to the internal words array with dynamic capacity expansion.
     * Doubles the array size when capacity is exceeded.
     *
     * @param word the word to add to the array
     */
    private void addWordToArray(String word) {
        if (wordCount >= allWords.length) {
            int newSize = Math.max(allWords.length * 2, 10);
            String[] newArray = new String[newSize];
            System.arraycopy(allWords, 0, newArray, 0, wordCount);
            allWords = newArray;
        }

        allWords[wordCount] = word;
        wordCount++;
    }

    /**
     * Performs depth-first search to collect all words from the trie subtree.
     * Traverses all child nodes and adds complete words to the result array.
     *
     * @param node the starting node for traversal
     * @param currentPrefix the accumulated prefix for current path
     * @param result the array to store found words
     * @param index current position in result array (as array for mutability)
     */
    private void collectWordsToArrayDFS(TrieNode node, String currentPrefix, String[] result, int[] index) {
        if (node.isEndOfWord()) {
            result[index[0]] = currentPrefix;
            index[0]++;
        }

        for (char c = 'a'; c <= 'z'; c++) {
            int charIndex = Validator.getCharIndex(c);
            TrieNode child = node.getChildren()[charIndex];
            if (child != null) {
                collectWordsToArrayDFS(child, currentPrefix + c, result, index);
            }
        }
    }

    /**
     * Recursively counts all complete words in the trie subtree.
     * Includes words ending at the current node and all descendant nodes.
     *
     * @param node the starting node for counting
     * @return total number of complete words in the subtree
     */
    private int countWords(TrieNode node) {
        int count = 0;

        if (node.isEndOfWord()) {
            count++;
        }

        for (char c = 'a'; c <= 'z'; c++) {
            int index = Validator.getCharIndex(c);
            TrieNode child = node.getChildren()[index];
            if (child != null) {
                count += countWords(child);
            }
        }

        return count;
    }

    /**
     * Creates a copy of the current words array with exact size.
     * Returns only the portion of the array that contains actual words.
     *
     * @return new array containing all words without null padding
     */
    private String[] getAllWords() {
        String[] result = new String[wordCount];
        System.arraycopy(allWords, 0, result, 0, wordCount);
        return result;
    }

    /**
     * Traverses the trie to find the node corresponding to the given prefix.
     * Follows the character path through child nodes.
     *
     * @param prefix the prefix to search for
     * @return the node at the end of prefix path, or null if prefix not found
     */
    private TrieNode findPrefixNode(String prefix) {
        TrieNode current = root;

        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            int charIndex = Validator.getCharIndex(c);
            TrieNode next = current.getChildren()[charIndex];
            if (next == null) {
                return null;
            }

            current = next;
        }

        return current;
    }
}