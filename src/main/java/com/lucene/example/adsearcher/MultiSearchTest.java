package com.lucene.example.adsearcher;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 针对多索引的搜索
 * @author qiaolin
 *
 */
public class MultiSearchTest {
	
	private Directory animalDirectory;
	
	private Directory directory;
	
	private String[] titles = new String[]{"dog","fox","cat"};
	
	/**
	 * 准备初始数据
	 * @throws IOException
	 */
	@Before
	public void init() throws IOException{
		directory = TestUtil.getIndexDirectory();
		animalDirectory = new RAMDirectory();
		IndexWriter writer = new IndexWriter(animalDirectory,
				new IndexWriterConfig(new StandardAnalyzer()));
		TestUtil.index();//书本的索引
		for(String title:titles){
			Document document = new Document();
			document.add(new TextField("title",title,Store.YES));
			document.add(new TextField("content",title + "in action",Store.YES));
			writer.addDocument(document);
		}
		writer.close();
	}
	
	/**
	 * 多索引搜索
	 * @throws IOException 
	 */
	@Test
	public void testMultiSearch() throws IOException{
		IndexReader reader = DirectoryReader.open(directory);
		IndexReader animalReader = DirectoryReader.open(animalDirectory);
		//使用MultiReader 进行多索引的查询
		IndexSearcher searcher = new IndexSearcher(new MultiReader(reader,animalReader));
		Query query = new TermQuery(new Term("content","action"));
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc score:topDocs.scoreDocs){
			Document document = searcher.doc(score.doc);
			System.out.println(document.get("title"));
		}
	}
	
	
	@After
	public void after() throws IOException{
		directory.close();
		animalDirectory.close();
		TestUtil.deleteIndexFiles();
	}
	
}
