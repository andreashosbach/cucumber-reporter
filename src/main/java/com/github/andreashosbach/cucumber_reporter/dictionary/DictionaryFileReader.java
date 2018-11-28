package com.github.andreashosbach.cucumber_reporter.dictionary;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class DictionaryFileReader implements DictionaryWordProvider {

    private List<String> words = new LinkedList<>();
    int index;

    public DictionaryFileReader(String filename) {
        try (InputStream in = getClass().getResourceAsStream(filename)) {
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String word;
                while ((word = reader.readLine()) != null) {
                    words.add(word.trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        index = 0;
    }

    @Override
    public String getNextWord() {
        if (index < words.size()) {
            return words.get(index++);
        } else {
            return null;
        }
    }
}
