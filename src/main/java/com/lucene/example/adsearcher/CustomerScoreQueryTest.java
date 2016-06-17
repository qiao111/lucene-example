package com.lucene.example.adsearcher;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 使用查询功能对最近修改过的文档进行加权
 * @author qiaolin
 *
 */
public class CustomerScoreQueryTest {
	
	
	/**
	 * 自定义评分
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testRecency() throws IOException, ParseException{
		TestUtil.index();
		Directory directory = TestUtil.getIndexDirectory();
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser("content",new StandardAnalyzer());
		Query query = new RecencyBoostingQuery(parser.parse("action"), 3, 365*10, "pubmonthAsDay");
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc score:topDocs.scoreDocs){
			System.out.println("score:" + score.score + ",content:" 
		+ searcher.doc(score.doc).get("content"));
		}
		TestUtil.deleteIndexFiles();
	}
	
}
