package bg.bozho.stemmer;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Test;

import bg.bozho.stemmer.alternatives.BulStemStemmer;
import bg.bozho.stemmer.alternatives.DefaultLuceneStemmer;

public class PerformanceTest {

    public static String[] WORDS = {"кравите", "обезверяват", "невероятните", "червена", "президенти", "съюзници"};
    
    @Test
    public void testStemmers() {
        BulgarianStemmer bgStemmer = new BulgarianStemmer();
        BulStemStemmer bulStemStemmer = new BulStemStemmer();
        DefaultLuceneStemmer luceneStemmer = new DefaultLuceneStemmer();
        
        testStemmer(s -> bgStemmer.stem(s), "bg-stemmer");
        testStemmer(s -> bulStemStemmer.stem(s), "BulStem");
        testStemmer(s -> luceneStemmer.stem(s), "Lucene stemmer");
    }
    
    public void testStemmer(Consumer<String> stemFunction, String stemmerName) {
        
        long start = System.nanoTime();
        for (int i = 0; i < 60000; i++) {
            stemFunction.accept(WORDS[i % WORDS.length]);
        }
        long time = System.nanoTime() - start;
        System.out.println("Time for " + stemmerName + ": " + TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS));
    }
}
