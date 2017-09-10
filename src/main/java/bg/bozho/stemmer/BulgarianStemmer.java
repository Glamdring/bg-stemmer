package bg.bozho.stemmer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;

public class BulgarianStemmer {

    public Trie<String, Integer> formsDictionary;
    private String[] baseFormIndex;

    private LuceneStemmer luceneStemmer = new LuceneStemmer();
    
    public static Map<String, Multimap<String, String>> inflectionClasses = Maps.newHashMap();
    
    public static final Set<String> verbClasses = Sets.newHashSet("P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
    
    public BulgarianStemmer() {
        loadInflections();
        loadFormsDictionary();
    }
    
    public String stem(String word) {
        Integer idx = formsDictionary.get(word);
        if (idx == null) {
            return useDefaultStemmer(word);
        }
        String baseForm = baseFormIndex[idx];
        if (baseForm != null) {
            char[] chars = baseForm.toCharArray();
            int len = luceneStemmer.stem(chars, baseForm.length(), false);
            return new String(chars, 0, len);
        } else {
            return useDefaultStemmer(word);
        }
    }

    private String useDefaultStemmer(String word) {
        char[] chars = word.toCharArray();
        int len = luceneStemmer.stem(chars, word.length(), true);
        return new String(chars, 0, len);
    }
    
    private Trie<String, Set<String>> loadDictionary() {
        
        InputStream is = BulgarianStemmer.class.getResourceAsStream("/bg_BG.dic");
        List<String> lines = null;

        try (Reader reader = new InputStreamReader(is, "UTF-8")){
            lines = CharStreams.readLines(reader);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        Trie<String, Set<String>> dictionary = new PatriciaTrie<String, Set<String>>(StringKeyAnalyzer.CHAR);
        for (String line : lines) {
            int paradigmIdx = line.indexOf("/");
            if (paradigmIdx != -1) {
                String inflectionClasses = line.substring(paradigmIdx + 1);
                // /AK is possible, i.e. multiple infl. classes per word
                dictionary.put(line.substring(0, paradigmIdx).toLowerCase(),
                        Sets.newHashSet(charToStringArray(inflectionClasses.toCharArray())));
            } else {
                dictionary.put(line.toLowerCase(), Collections.<String>emptySet());
            }
        }
        return dictionary;
    }

    public void loadFormsDictionary() {
        formsDictionary = new PatriciaTrie<String, Integer>(StringKeyAnalyzer.CHAR);
        Trie<String, Set<String>> dictionary = loadDictionary();
        // we create a base form index, so that we can store just an integer in the Trie, rather than 
        // whole strings. If we intern the whole strings, the effect will be similar, but
        // we should not rely of the internal implementation of the JVM string pool
        // and its size. (And it also makes 'translating' the stemmer in other languages easier)
        baseFormIndex = new String[dictionary.size()];
        int baseFormIndexId = 0;
        for (Map.Entry<String, Set<String>> word : dictionary.entrySet()) {
            String baseForm = word.getKey();
            baseFormIndex[baseFormIndexId] = baseForm;
            formsDictionary.put(baseForm, baseFormIndexId);
            if (word.getValue().isEmpty()) {
                baseFormIndexId++;
                continue;
            }
            for (String inflectionClass : word.getValue()) {
                Multimap<String, String> inflections = inflectionClasses.get(inflectionClass);
                
                if (inflections == null) {
                    continue;
                }

                for (String ending : inflections.keySet()) {
                    int endingIdx = baseForm.lastIndexOf(ending);
                    if (!baseForm.endsWith(ending) || endingIdx == -1) {
                        continue;
                    }

                    for (String suffix : inflections.get(ending)) {
                        String inflectedWord = baseForm.substring(0, endingIdx) + suffix;
                        // if the inflected word coincides with a base form, do not include the inflected word
                        if (dictionary.containsKey(inflectedWord)) {
                            continue;
                        }
                        formsDictionary.put(inflectedWord, baseFormIndexId);
                    }
                }
            }
            baseFormIndexId++;
        }
    }
    
     public void loadInflections() {
         InputStream inputStreamAll = BulgarianStemmer.class.getResourceAsStream("/bg_BG.aff");
         fillInflectionClasses(inflectionClasses, inputStreamAll);
     }

     private void fillInflectionClasses(Map<String, Multimap<String, String>> map, InputStream is) {
         List<String> lines = null;
         try (Reader reader = new InputStreamReader(is, "UTF-8")){
             lines = CharStreams.readLines(reader);
         } catch (IOException ex) {
             throw new IllegalStateException(ex);
         }

         boolean newInflectionClass = false;
         for (String line : lines) {
             if (line.trim().isEmpty()) {
                 newInflectionClass = true;
                 continue;
             }
             if (!line.startsWith("SFX")) {
                 continue;
             }

             String inflectionClass = line.substring(4, 5);
             if (newInflectionClass) {
                 map.put(inflectionClass, HashMultimap.<String, String>create());
             } else {
                 String[] parts = line.split("\\p{Space}+");
                 String suffix = parts[3];
                 if (suffix.equals("0")) {
                     suffix = "";
                 }
                 String baseFormEnding = parts[2];
                 if (baseFormEnding.equals("0")) {
                     baseFormEnding = "";
                 }
                 // the inflection suffixes are the values of the multimap, with key=the base form ending
                 map.get(inflectionClass).put(baseFormEnding, suffix);
             }
             newInflectionClass = false;
         }
     }

     private static String[] charToStringArray(char[] array) {
         String[] result = new String[array.length];
         for (int i = 0; i < array.length; i ++) {
             result[i] = String.valueOf(array[i]);
         }
         return result;
     }
}
