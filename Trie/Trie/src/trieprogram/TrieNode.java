package trieprogram;

public class TrieNode {
    private TrieNode[] children; //Child nodes array - index represents letter (0='a', 1='b', etc.)
    private boolean isEndOfWord; //True if this node completes a word

    /**
     * Constructs a new TrieNode with empty child nodes.
     * Initializes all children to null and end-of-word flag to false.
     */
    public TrieNode() {
        this.children = new TrieNode[Validator.getAlphabetSize()];
        this.isEndOfWord = false;
    }

    /**
     * Gets the array of child nodes for this node.
     * Each index in the array corresponds to a specific letter in the alphabet.
     *
     * @return array of child TrieNodes, may contain null values for missing letters
     */
    public TrieNode[] getChildren() {
        return children;
    }

    /**
     * Checks if this node marks the end of a complete word in the dictionary.
     *
     * @return true if this node represents the end of a valid word, false otherwise
     */
    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    /**
     * Sets the end-of-word flag for this node.
     * Used to mark when a complete word ends at this node.
     *
     * @param isEndOfWord true to mark as end of word, false to unmark
     */
    public void setEndOfWord(boolean isEndOfWord) {
        this.isEndOfWord = isEndOfWord;
    }
}