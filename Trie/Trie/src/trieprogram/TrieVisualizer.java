package trieprogram;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrieVisualizer extends JFrame {
    private final Trie trie;
    private final DrawPanel drawPanel; //Main visualization panel
    private final JLabel statusLabel; //Status display label
    private String currentHighlightedWord = "";
    private String currentHighlightedPrefix = "";
    private List<String> currentHighlightedWords = new ArrayList<>(); //List of highlighted words

    /**
     * Constructs a new TrieVisualizer with the specified trie
     * @param trie the trie data structure to visualize
     */
    public TrieVisualizer(Trie trie) {
        super("Trie Visualizer");
        this.trie = trie;

        //Main window configuration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        //Control panel with buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearHighlightBtn = new JButton("Clear Highlight");
        JButton refreshBtn = new JButton("Refresh");
        controlPanel.add(clearHighlightBtn);
        controlPanel.add(refreshBtn);

        //Status label with word count
        statusLabel = new JLabel("Words: " + trie.getWordCount() + " | Ready");
        controlPanel.add(statusLabel);

        //Drawing panel with scroll
        drawPanel = new DrawPanel();
        JScrollPane scroll = new JScrollPane(drawPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        //Layout setup
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(controlPanel, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);

        //Button event handlers
        clearHighlightBtn.addActionListener(e -> {
            clearHighlights();
            drawPanel.repaint();
            updateStatus("Highlight cleared");
        });

        refreshBtn.addActionListener(e -> {
            drawPanel.rebuild();
            drawPanel.repaint();
            updateStatus("Refreshed");
        });

        //Initial visualization build
        SwingUtilities.invokeLater(() -> {
            drawPanel.rebuild();
        });
    }

    /**
     * Clears all current highlighting from the visualization
     * including highlighted words, prefixes, and search results
     */
    public void clearHighlights() {
        currentHighlightedWord = "";
        currentHighlightedPrefix = "";
        currentHighlightedWords.clear();
    }

    /**
     * Highlights a specific word in the trie visualization
     * @param word the word to highlight in the trie
     */
    public void highlightWord(String word) {
        clearHighlights();
        currentHighlightedWord = word;
        updateStatus("Highlighting word: " + word);
        drawPanel.repaint();
    }

    /**
     * Highlights specified prefix
     * @param prefix the prefix to highlight in the trie
     */
    public void highlightPrefix(String prefix) {
        clearHighlights();
        currentHighlightedPrefix = prefix;
        updateStatus("Highlighting prefix: " + prefix);
        drawPanel.repaint();
    }

    /**
     * Highlights all words that start with the specified prefix
     * @param prefix the prefix used to find matching words
     */
    public void highlightWordsByPrefix(String prefix) {
        clearHighlights();
        String[] words = trie.getByPrefix(prefix);
        currentHighlightedWords.addAll(Arrays.asList(words));
        updateStatus("Found " + words.length + " words with prefix: " + prefix);
        drawPanel.repaint();
    }

    /**
     * Refreshes the trie visualization by rebuilding the visual
     * representation from the underlying trie data
     */
    public void refreshTrie() {
        drawPanel.rebuild();
        updateStatus("Trie updated");
        drawPanel.repaint();
    }

    /**
     * Updates the status label with the current word count and message
     * @param message the status message to display
     */
    private void updateStatus(String message) {
        statusLabel.setText("Words: " + trie.getWordCount() + " | " + message);
    }

    /**
     * Custom JPanel that handles the drawing and visualization
     * of the trie structure including nodes, edges, and highlighting
     */
    private class DrawPanel extends JPanel {
        private static final int LEVEL_GAP = 80;
        private static final int NODE_RADIUS = 20;
        private VisualNode rootVisual;

        /**
         * Constructs a new DrawPanel with default white background
         * and preferred size for trie visualization
         */
        DrawPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(1200, 700));
        }

        /**
         * Rebuilds the entire visual tree structure from the underlying trie data
         * by processing all words and creating corresponding visual nodes
         */
        void rebuild() {
            //Create new root and fetch all words
            rootVisual = new VisualNode('\0');
            List<String> allWords = getAllWordsFromTrie();

            //Build visual nodes for each word
            for (String word : allWords) {
                VisualNode current = rootVisual;
                StringBuilder currentPath = new StringBuilder();

                for (int i = 0; i < word.length(); i++) {
                    char ch = word.charAt(i);
                    currentPath.append(ch);

                    VisualNode child = current.getChild(ch);

                    if (child == null) {
                        child = new VisualNode(ch);
                        child.parent = current;
                        current.children.add(child);
                    }

                    current = child;
                    current.fullWord = currentPath.toString();
                }

                current.isEndOfWord = true;
            }

            //Calculate node positions and refresh display
            computeLayout();
            revalidate();
        }

        /**
         * Retrieves all unique words from the trie by querying
         * each letter of the alphabet and collecting results
         * @return List of all words in the trie
         */
        private List<String> getAllWordsFromTrie() {
            List<String> words = new ArrayList<>();

            //Query words for each letter of alphabet
            for (char c = 'a'; c <= 'z'; c++) {
                String[] wordsByPrefix = trie.getByPrefix(String.valueOf(c));

                //Add unique words to collection
                for (String word : wordsByPrefix) {
                    if (!words.contains(word)) {
                        words.add(word);
                    }
                }
            }

            return words;
        }

        /**
         * Computes the layout coordinates for all nodes in the visual tree
         * by calculating subtree widths and assigning x,y positions
         */
        private void computeLayout() {
            if (rootVisual == null) {
                return;
            }

            //Compute required width for each subtree
            computeSubtreeWidth(rootVisual);
            int panelWidth = getWidth();

            if (panelWidth <= 0) {
                panelWidth = 1200;
            }

            //Assign coordinates starting from center top
            assignCoordinates(rootVisual, panelWidth / 2, NODE_RADIUS + 50);
        }

        /**
         * Recursively calculates the width required by each subtree
         * for balanced tree layout distribution
         * @param node the root node of the subtree to calculate
         * @return the calculated width of the subtree
         */
        private int computeSubtreeWidth(VisualNode node) {
            if (node.children.isEmpty()) {
                node.subtreeWidth = 1;
                return node.subtreeWidth;
            }

            int totalWidth = 0;

            for (VisualNode child : node.children) {
                totalWidth += computeSubtreeWidth(child);
            }

            node.subtreeWidth = Math.max(totalWidth, 1);
            return node.subtreeWidth;
        }

        /**
         * Recursively assigns x,y coordinates to all nodes in the subtree
         * using a balanced tree layout algorithm
         * @param node the root node of the subtree
         * @param x the x-coordinate for the current node
         * @param y the y-coordinate for the current node
         */
        private void assignCoordinates(VisualNode node, int x, int y) {
            node.x = x;
            node.y = y;

            if (node.children.isEmpty()) {
                return;
            }

            int totalChildWidth = 0; //Calculate total width needed for children

            for (VisualNode child : node.children) {
                totalChildWidth += child.subtreeWidth;
            }

            //Distribute children evenly with spacing
            int spacing = 60;
            int currentX = x - (totalChildWidth * spacing) / 2 + spacing / 2;

            //Position each child recursively
            for (VisualNode child : node.children) {
                int childCenterX = currentX + (child.subtreeWidth * spacing - spacing) / 2;
                assignCoordinates(child, childCenterX, y + LEVEL_GAP);
                currentX += child.subtreeWidth * spacing;
            }
        }

        /**
         * Paints the trie visualization including empty state message,
         * node edges, nodes with labels, and highlighting effects
         * @param g the Graphics context to paint on
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //Show empty message if no data
            if (rootVisual == null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                String message = "Dictionary is empty. Add some words!";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(message)) / 2;
                int y = getHeight() / 2;
                g2.drawString(message, x, y);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawEdges(g2, rootVisual); //Draw edges then nodes
            drawNodes(g2, rootVisual);
            g2.dispose();
        }

        /**
         * Recursively draws the edges (connections) between parent and child nodes
         * @param g2 the Graphics2D context for drawing
         * @param node the starting node for edge drawing
         */
        private void drawEdges(Graphics2D g2, VisualNode node) {
            for (VisualNode child : node.children) {
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(node.x, node.y, child.x, child.y);

                drawEdges(g2, child);
            }
        }

        /**
         * Recursively draws the tree nodes with labels, borders, and
         * end-of-word indicators using the specified graphics context
         * @param g2 the Graphics2D context for drawing
         * @param node the starting node for node drawing
         */
        private void drawNodes(Graphics2D g2, VisualNode node) {
            //Determine node color based on highlighting
            Color nodeColor = determineNodeColor(node);

            g2.setColor(nodeColor);
            g2.fillOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2);

            g2.setColor(Color.BLACK);
            String label = node.label == '\0' ? "root" : String.valueOf(node.label);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();
            g2.drawString(label, node.x - textWidth / 2, node.y + textHeight / 4);

            //Draw red circle for end-of-word nodes
            if (node.isEndOfWord) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawOval(node.x - NODE_RADIUS + 3, node.y - NODE_RADIUS + 3,
                        NODE_RADIUS * 2 - 6, NODE_RADIUS * 2 - 6);
            }

            //Recursively draw child nodes
            for (VisualNode child : node.children) {
                drawNodes(g2, child);
            }
        }

        /**
         * Determines the appropriate color for a node based on current
         * highlighting state (word, prefix, or multiple words)
         * @param node the node to determine color for
         * @return the Color to use for the node background
         */
        private Color determineNodeColor(VisualNode node) {
            if (node.fullWord == null || node.fullWord.isEmpty()) {
                return Color.WHITE;
            }

            String nodePath = node.fullWord;

            //Highlight full word path
            if (!currentHighlightedWord.isEmpty()) {
                if (currentHighlightedWord.startsWith(nodePath)) {
                    return new Color(173, 216, 230);
                }
            }

            //Highlight prefix path
            if (!currentHighlightedPrefix.isEmpty()) {
                if (currentHighlightedPrefix.startsWith(nodePath)) {
                    return new Color(173, 216, 230);
                }
            }

            //Highlight multiple words by prefix
            if (!currentHighlightedWords.isEmpty()) {
                for (String fullWord : currentHighlightedWords) {
                    if (fullWord.startsWith(nodePath)) {
                        return new Color(173, 216, 230);
                    }
                }
            }

            return Color.WHITE;
        }
    }

    /**
     * Represents a single node in the visual trie structure
     * containing character data, positional information, and
     * relationships to parent and child nodes
     */
    private static class VisualNode {
        char label; //Character stored in this node
        List<VisualNode> children = new ArrayList<>(); //Child nodes
        VisualNode parent; //Reference to parent node
        boolean isEndOfWord = false;
        String fullWord = ""; //Full word path from root to this node

        int x, y; //Coordinates for visual positioning
        int subtreeWidth = 1;

        /**
         * Constructs a new VisualNode with the specified character label
         * @param label the character this node represents
         */
        VisualNode(char label) {
            this.label = label;
        }

        /**
         * Finds and returns a child node with the specified character
         * @param c the character to search for in child nodes
         * @return the child node with matching character, or null if not found
         */
        VisualNode getChild(char c) {
            for (VisualNode child : children) {
                if (child.label == c) {
                    return child;
                }
            }
            return null;
        }
    }
}