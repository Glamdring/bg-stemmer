package bg.bozho.stemmer;

/**
 * We are using the original Lucene stemmer to turn base forms into stems to
 * have best effort handling of unknown words
 *
 * Light Stemmer for Bulgarian.
 * <p>
 * Implements the algorithm described in: <i> Searching Strategies for the
 * Bulgarian Language </i> http://members.unine.ch/jacques.savoy/Papers/BUIR.pdf
 */
public class LuceneStemmer {

    /**
     * Stem an input buffer of Bulgarian text.
     * 
     * @param s
     *            input buffer
     * @param len
     *            length of input buffer
     * @return length of input buffer after normalization
     */
    public int stem(final char s[], int len, boolean removeArticleAndPlural) {
        if (len < 4) // do not stem
            return len;

        if (len > 5 && endsWith(s, len, "ища"))
            return len - 3;

        if (removeArticleAndPlural) {
            len = removeArticle(s, len);
            len = removePlural(s, len);
        }

        if (len > 3) {
            if (endsWith(s, len, "я"))
                len--;
            if (endsWith(s, len, "а") || endsWith(s, len, "о") || endsWith(s, len, "е"))
                len--;
            // additional rule to the paper, to account for 1st person sg.
            if (endsWith(s, len, "ам") || endsWith(s, len, "ям")) {
                len -= 2;
            }
        }

        // the rule to rewrite ен -> н in the paper is not justified.
        // it works for "мощен", "силен" where the stem may be seen as "мощн", "силн", but
        // it doesn't work for colors ("червен", "зелен"). Since the stem can be "мощ" and "сил" as well
        // we just erase the whole ending
        if (len > 4 && endsWith(s, len, "ен")) {
            len -= 2;
        }

        if (len > 5 && s[len - 2] == 'ъ') {
            s[len - 2] = s[len - 1]; // replace ъN with N
            len--;
        }

        return len;
    }

    /**
     * Mainly remove the definite article
     * 
     * @param s
     *            input buffer
     * @param len
     *            length of input buffer
     * @return new stemmed length
     */
    private int removeArticle(final char s[], final int len) {
        if (len > 6 && endsWith(s, len, "ият"))
            return len - 3;

        if (len > 5) {
            if (endsWith(s, len, "ът") || endsWith(s, len, "то") || endsWith(s, len, "те")
                    || endsWith(s, len, "та") || endsWith(s, len, "ия"))
                return len - 2;
        }

        if (len > 4 && endsWith(s, len, "ят"))
            return len - 2;

        return len;
    }

    private int removePlural(final char s[], final int len) {
        if (len > 6) {
            if (endsWith(s, len, "овци"))
                return len - 3; // replace with о
            if (endsWith(s, len, "ове"))
                return len - 3;
            if (endsWith(s, len, "еве")) {
                s[len - 3] = 'й'; // replace with й
                return len - 2;
            }
        }

        if (len > 5) {
            if (endsWith(s, len, "ища"))
                return len - 3;
            if (endsWith(s, len, "та"))
                return len - 2;
            if (endsWith(s, len, "ци")) {
                s[len - 2] = 'к'; // replace with к
                return len - 1;
            }
            if (endsWith(s, len, "зи")) {
                s[len - 2] = 'г'; // replace with г
                return len - 1;
            }

            if (s[len - 3] == 'е' && s[len - 1] == 'и') {
                s[len - 3] = 'я'; // replace е with я, remove и
                return len - 1;
            }
        }

        if (len > 4) {
            if (endsWith(s, len, "си")) {
                s[len - 2] = 'х'; // replace with х
                return len - 1;
            }
            if (endsWith(s, len, "и"))
                return len - 1;
        }

        return len;
    }

    /**
     * Returns true if the character array ends with the suffix.
     * 
     * @param s
     *            Input Buffer
     * @param len
     *            length of input buffer
     * @param suffix
     *            Suffix string to test
     * @return true if <code>s</code> ends with <code>suffix</code>
     */
    public static boolean endsWith(char s[], int len, String suffix) {
        final int suffixLen = suffix.length();
        if (suffixLen > len)
            return false;
        for (int i = suffixLen - 1; i >= 0; i--)
            if (s[len - (suffixLen - i)] != suffix.charAt(i))
                return false;

        return true;
    }
}
