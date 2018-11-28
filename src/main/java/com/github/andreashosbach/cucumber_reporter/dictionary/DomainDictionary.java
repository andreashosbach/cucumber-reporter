package com.github.andreashosbach.cucumber_reporter.dictionary;

import java.util.HashSet;
import java.util.Set;

public class DomainDictionary
{
    private Set<String> domainWords = new HashSet<>();

    public static DomainDictionary create(DictionaryWordProvider provider){
        DomainDictionary glossary = new DomainDictionary();
        while(true){
            String word = provider.getNextWord();
            if(word == null){
                break;
            }else{
                glossary.domainWords.add(word.trim().toLowerCase());
            }
        }
        return glossary;
    }

    public boolean containsWord(String word){
        return domainWords.contains(word.trim().toLowerCase());
    }
}
