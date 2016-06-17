package com.lucene.example.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;


/**
 * Í¬Òå´Ê ·ÖÎöÆ÷
 * @author qiaolin
 *
 */
public class SynonymAnalyzer extends Analyzer{
	
	private SynonymEngine engine;
	public SynonymAnalyzer(SynonymEngine engine) {
		this.engine = engine;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new LowerCaseTokenizer();
		TokenStream result = new StopFilter(new SynonymFilter(source, engine),
					StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		return new TokenStreamComponents(source,result);
	}

}
