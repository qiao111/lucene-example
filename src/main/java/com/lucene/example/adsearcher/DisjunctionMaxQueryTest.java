package com.lucene.example.adsearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 多值域测试
 * @author qiaolin
 *
 */
public class DisjunctionMaxQueryTest {
	@Before
	public void init() throws IOException{
		TestUtil.index();
	}
	
	@After
	public void after(){
		TestUtil.deleteIndexFiles();
	}
	
	/**
	 * 测试多值域的问题
	 * @throws IOException
	 */
	@Test
	public void testDisjunctionMaxQuery() throws IOException{
		Query termQuery = new TermQuery(new Term("title","action"));
		Builder builder = new BooleanQuery.Builder();
		builder.add(new TermQuery(new Term("subject","*java*")), Occur.MUST);
		Query booleanQuery = builder.build();
		List<Query> lists = new ArrayList<Query>();
		lists.add(termQuery);
		lists.add(booleanQuery);
		Query query = new DisjunctionMaxQuery(lists, 1f);
		System.out.println(query.toString());
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			Explanation explan = searcher.explain(query, scoreDoc.doc);
			System.out.println("title:" + doc.get("title") + 
					",subject:" + doc.get("subject") + ",score:" + scoreDoc.score);
			System.out.println("=======================");
			System.out.println(explan);
			System.out.println(doc.toString());
		}
	}
}
