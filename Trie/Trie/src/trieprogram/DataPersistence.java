package trieprogram;
import java.io.*;
import java.util.*;

public class DataPersistence {
    private static final String DATA_FILE = "trie_data.dat";

    public static void saveTrieData(String[] words) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(words);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public static String[] loadTrieData() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return new String[0];
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (String[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new String[0];
        }
    }

    public static void deleteSavedData() {
        File file = new File(DATA_FILE);

        if (file.exists()) {
            boolean deleted = file.delete();

            if (!deleted) {
                System.out.println("Warning! Could not delete data file");
            }
        }
    }
}