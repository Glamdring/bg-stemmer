package bg.bozho.stemmer;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class BulgarianStemFilterFactory extends TokenFilterFactory {
  
  /** Creates a new BulgarianStemFilterFactory */
  public BulgarianStemFilterFactory(Map<String,String> args) {
    super(args);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }
  
  @Override
  public TokenStream create(TokenStream input) {
    return new BulgarianStemFilter(input);
  }
}
