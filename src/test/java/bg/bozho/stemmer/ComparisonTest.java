package bg.bozho.stemmer;

import org.junit.Test;

import bg.bozho.stemmer.alternatives.BulStemStemmer;

public class ComparisonTest {

    //@Test
    public void bulStemComparison() throws Exception {
        BulStemStemmer bulStem = new BulStemStemmer();
        bulStem.loadStemmingRules();
        
        BulgarianStemmer bgStemmer = new BulgarianStemmer();
        for (String key : bgStemmer.formsDictionary.keySet()) {
            String bgStemmerRoot = bgStemmer.stem(key.toLowerCase());
            String bulstemRoot = bulStem.stem(key.toLowerCase());
            
            if (!bgStemmerRoot.equals(bulstemRoot)) {
                System.out.println("Discrepancy: root="  + key + ". Stems: "+ bgStemmerRoot + ":" + bulstemRoot);
            }
        }
    }
    
    //@Test
    public void luceneComparison() throws Exception {
        org.apache.lucene.analysis.bg.BulgarianStemmer luceneStem = new org.apache.lucene.analysis.bg.BulgarianStemmer();
        
        BulgarianStemmer bgStemmer = new BulgarianStemmer();
        for (String key : bgStemmer.formsDictionary.keySet()) {
            String bgStemmerRoot = bgStemmer.stem(key.toLowerCase());
            char[] array = key.toLowerCase().toCharArray();
            int len = luceneStem.stem(array, array.length);
            String luceneRoot = new String(array, 0, len);
            
            if (!bgStemmerRoot.equals(luceneRoot)) {
                System.out.println("Discrepancy: root="  + key + ". Stems: "+ bgStemmerRoot + ":" + luceneRoot);
            }
        }
    }
}
