package com.lucene.example.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.search.Weight;

/**
 * 获取文档的评分的简单信息
 * @author qiaolin
 *
 */
public class AllDocCollector extends SimpleCollector{
	
	private Scorer scorer;
	
	private List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
	
	public AllDocCollector(){
	}
	
	@Override
	public boolean needsScores() {
		return false;
	}

	@Override
	public void collect(int doc) throws IOException {
		scoreDocs.add(new ScoreDoc(doc, scorer.score()));
	}
	
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}
	
	public List<ScoreDoc> getHits(){
		return scoreDocs;
	}

}
