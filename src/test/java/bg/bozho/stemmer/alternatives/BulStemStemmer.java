package bg.bozho.stemmer.alternatives;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BulStemStemmer {

    private Map<String, String> stemmingRules = new HashMap<>();

    private static final int STEM_BOUNDARY = 1;

    private static final Pattern vocals = Pattern.compile("[^аъоуеиюя]*[аъоуеиюя]");

    public static final Pattern p = Pattern.compile("([а-я]+)\\s==>\\s([а-я]+)\\s([0-9]+)");

    public void loadStemmingRules() throws Exception {
        stemmingRules.clear();
        try (InputStream is = BulStemStemmer.class.getResourceAsStream("/bulstem/stem_rules_context_1.txt")) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                Matcher m = p.matcher(s);
                if (m.matches()) {
                    int j = m.groupCount();
                    if (j == 3) {
                        if (Integer.parseInt(m.group(3)) > STEM_BOUNDARY) {
                            stemmingRules.put(m.group(1), m.group(2));
                        }
                    }
                }
            }
        }
    }

    public String stem(String word) {
        Matcher m = vocals.matcher(word);
        if (!m.lookingAt()) {
            return word;
        }
        for (int i = m.end() + 1; i < word.length(); i++) {
            String suffix = word.substring(i);
            if ((suffix = (String) stemmingRules.get(suffix)) != null) {
                return word.substring(0, i) + suffix;
            }
        }
        return word;
    }
}