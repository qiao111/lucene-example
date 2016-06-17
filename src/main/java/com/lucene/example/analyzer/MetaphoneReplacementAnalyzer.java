package com.lucene.example.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;

/**
 * ½üÒô´Ê ·ÖÎöÆ÷
 * @author qiaolin
 *
 */
public class MetaphoneReplacementAnalyzer extends Analyzer{

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new LetterTokenizer();
		return new TokenStreamComponents(source,new MetaphoneReplacementFilter(source));
	}

}
