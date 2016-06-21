package com.lucene.example.payload;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.BytesRef;

public class BulletinPayloadsAnalyzer extends Analyzer{
	
	 private boolean isBulletin;
	 
	 private BytesRef boost;
	 
	 public BulletinPayloadsAnalyzer(BytesRef boost) {
		 this.boost = boost;
	}
	 
	 public void setIsBulletin(boolean isBulletin){
		 this.isBulletin = isBulletin;
	 }

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new WhitespaceTokenizer();
		BulletinPayloadsFilter result = new BulletinPayloadsFilter(source, boost);
		result.setIsBulletin(isBulletin);
		return new TokenStreamComponents(source,result);
	}
}
