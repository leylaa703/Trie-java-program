# Trie Java Program

A Java project implementing a Trie (prefix tree) data structure with advanced functionality:  
- work through the console menu  
- visualization of the tree in real time via the graphical interface (GUI) on Swing  
- search for words, prefixes, search for words by a given prefix, highlighting paths in the tree  
- saving data to a file and downloading it when the program starts

## Main features

### Working with words in a Trie
- Adding a new word  
- Checking the existence of a word  
- Checking the existence of a prefix  
- Search for all words by a given prefix  
- Deleting a word   
- Output of all words
- Complete dictionary cleanup  
- Automatic data saving between launches

## Graphical Interface (Trie Visualizer)

The program includes its own GUI visualizer, TrieVisualizer, which displays the Trie structure as a tree with nodes and links.

### Graphical interface features:

#### Displaying a Trie as a tree
- each node is a symbol  
- red ring around the node — end of the word  
- each node displays the path to it.  

#### The backlight:
- highlighting a specific word  
- prefix highlighting  
- highlighting of all words starting with a specific prefix  
- the ability to clear the backlight

#### Dynamic update:
- the tree is rebuilt every time the dictionary is changed  
- automatic recalculation of coordinates and redistribution of nodes  

#### Control Panel:
- Clear Highlight button to clear the current backlight
- Refresh button for manual redrawing
- status display: number of words + current operation
