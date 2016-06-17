package com.lucene.example.analyzer;

/**
 * 获取同义词的接口
 * @author qiaolin
 *
 */
public interface SynonymEngine {
	public String[] getSynonyms(String word);
}
