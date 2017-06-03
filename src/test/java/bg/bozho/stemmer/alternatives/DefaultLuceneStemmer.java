package bg.bozho.stemmer.alternatives;

import org.apache.lucene.analysis.bg.BulgarianStemmer;

public class DefaultLuceneStemmer {

    private BulgarianStemmer stemmer = new BulgarianStemmer();
    
    public String stem(String word) {
        char[] array = word.toCharArray();
        int len = stemmer.stem(array, array.length);
        return new String(array, 0, len);
    }
}
