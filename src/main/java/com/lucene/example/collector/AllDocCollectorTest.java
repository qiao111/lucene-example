package com.lucene.example.collector;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

public class AllDocCollectorTest {
	
	@Before
	public void init() throws IOException{
		TestUtil.index();
	}
	
	@After
	public void after(){
		TestUtil.deleteIndexFiles();
	}
	
	/**
	 * 测试自定义的collector
	 * @throws IOException
	 */
	@Test
	public void testAllDocCollector() throws IOException{
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new TermQuery(new Term("content","action"));
		AllDocCollector collector = new AllDocCollector();
		searcher.search(query, collector);
		List<ScoreDoc>  scoreDocs = collector.getHits();
		for(ScoreDoc scoreDoc:scoreDocs){
			Document document = searcher.doc(scoreDoc.doc);
			System.out.println("title:" + document.get("title") + "," + scoreDoc.doc + "," + scoreDoc.score);
		}
		directory.close();
	}
	
}
