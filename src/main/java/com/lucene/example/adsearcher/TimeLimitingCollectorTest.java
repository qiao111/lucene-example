package com.lucene.example.adsearcher;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TimeLimitingCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TimeLimitingCollector.TimeExceededException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Counter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 停止时间较长的搜索
 * @author qiaolin
 *
 */
public class TimeLimitingCollectorTest {
		
	@Before
	public void init() throws IOException{
		TestUtil.index();
	}
	
	@After
	public void after(){
		TestUtil.deleteIndexFiles();
	}
	
	@Test
	public void testTimeLimitingCollector() throws IOException{
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new MatchAllDocsQuery();
		//使用时间限制
		TopScoreDocCollector topDocCollector = TopScoreDocCollector.create(1);
		Collector collector =  new TimeLimitingCollector(topDocCollector, Counter.newCounter(), 1000);
		try{
			searcher.search(query, collector);
			System.out.println(topDocCollector.getTotalHits());
		}catch(TimeExceededException e){
			e.printStackTrace();
		}
	}
}
