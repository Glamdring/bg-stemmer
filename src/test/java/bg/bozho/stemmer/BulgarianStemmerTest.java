package bg.bozho.stemmer;

import junit.framework.Assert;

import org.junit.Test;

public class BulgarianStemmerTest {

    @Test
    public void stemmerTest() throws Exception {
        BulgarianStemmer stemmer = new BulgarianStemmer();
        Assert.assertEquals("крав", stemmer.stem("кравите"));
        Assert.assertEquals("открив", stemmer.stem("откривахме"));
        Assert.assertEquals("безиде", stemmer.stem("безидейните"));
        Assert.assertEquals("черв", stemmer.stem("червен"));
    }
}
