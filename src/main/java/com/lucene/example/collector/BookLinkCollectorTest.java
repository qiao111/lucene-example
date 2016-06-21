package com.lucene.example.collector;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;



public class BookLinkCollectorTest {
	
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
	public void testBookLinkCollector() throws IOException{
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new TermQuery(new Term("content","action"));
		BookLinkCollector collector = new BookLinkCollector();
		searcher.search(query, collector);
		Map<String,String> linkMap = collector.getLinks();
		for(Entry<String, String> entry:linkMap.entrySet()){
			System.out.println("key:" + entry.getKey() + ",title:" + entry.getValue());
		}
		directory.close();
	}
	
}
