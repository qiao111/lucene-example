package com.lucene.example.adsearcher;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 多域搜索
 * @author qiaolin
 *
 */
public class MultiFieldQueryParserTest {
	
	@Before
	public void init() throws IOException{
		TestUtil.index();
	}
	
	@After
	public void after(){
		TestUtil.deleteIndexFiles();
	}
	
	/**
	 * 默认操作是OR的关系
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void testDefaultOperatorQuery() throws ParseException, IOException{
		Query query = new MultiFieldQueryParser(new String[]{"title","subject"}, 
				new StandardAnalyzer()).parse("development*");
		System.out.println(query.toString());
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("title:" + doc.get("title") + 
					",subject:" + doc.get("subject"));
		}
	}
	
	/**
	 * 指定操作符 AND 查询title和subject中都有lucene的文档
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void testSpecialOperatorQuery()throws ParseException, IOException{
		Query query = MultiFieldQueryParser.parse("lucene", new String[]{"title","subject"},
					new Occur[]{Occur.MUST,Occur.MUST}, new StandardAnalyzer());
		System.out.println(query.toString());
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("title:" + doc.get("title") + 
					",subject:" + doc.get("subject"));
		}
	}
	
	/**
	 * 指定操作符AND 查询title中含有action并且subject中含有lucene的文档
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void testSpecialOperatorQueryTwo() throws ParseException, IOException{
		Query query = MultiFieldQueryParser.parse(new String[]{"action","lucene"}, new String[]{"title","subject"},
				new Occur[]{Occur.MUST,Occur.MUST}, new StandardAnalyzer());
		System.out.println(query.toString());//搜索表达式
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("title:" + doc.get("title") + 
					",subject:" + doc.get("subject"));
		}
	}
}	
