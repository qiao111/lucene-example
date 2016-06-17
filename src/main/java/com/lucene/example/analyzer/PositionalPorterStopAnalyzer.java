package com.lucene.example.analyzer;

import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.util.CharArraySet;

/**
 * 词干合并 和移除停用词
 * @author qiaolin
 *
 */
@SuppressWarnings("rawtypes")
public class PositionalPorterStopAnalyzer extends Analyzer{
	
	private Set stopWords;
	
	public PositionalPorterStopAnalyzer() {
		this(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	}
	
	
	public PositionalPorterStopAnalyzer(Set stopWords){
		this.stopWords = stopWords;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new LowerCaseTokenizer();
		//使用Porter算法的过滤器
		StopFilter stopFilter = new StopFilter(source, 
				(CharArraySet) stopWords);
		TokenStream result = new PorterStemFilter(stopFilter);
		return new TokenStreamComponents(source,result);
	}

}
