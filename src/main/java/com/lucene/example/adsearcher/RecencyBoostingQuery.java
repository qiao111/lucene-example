package com.lucene.example.adsearcher;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

public class RecencyBoostingQuery extends CustomScoreQuery{
	
	private String field;
	private double multiplier;
	private long today;
	private int maxDaysAgo;
	private final static int MESC_PER_DAY = 1000*3600*24;
	
	public RecencyBoostingQuery(Query subQuery,double multiplier,int maxDaysAgo,
				String field) {
		super(subQuery);
		this.field = field;
		this.multiplier = multiplier;
		this.maxDaysAgo = maxDaysAgo;
		today = new Date().getTime()/MESC_PER_DAY;
	}
	
	@Override
	protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
		return new RecencyBooster(context);
	}
	
	private class RecencyBooster extends CustomScoreProvider{
		
		public RecencyBooster(LeafReaderContext context) {
			super(context);
		}
		
		@Override
		public float customScore(int doc, float subQueryScore, float valSrcScores) throws IOException {
			Document document = context.reader().document(doc);//ªÒ»°document
			long daysAgo = (long) (today - Float.valueOf((String)document.get(field)));
			if(daysAgo < maxDaysAgo){
				float boost = (float) (multiplier*(maxDaysAgo - daysAgo)/maxDaysAgo);
				System.out.println("boost:" +boost + document.get("content"));
				return (float) (subQueryScore*(boost + 1.0));
			}else{
				return subQueryScore;
			}
		}
		
	}
}
