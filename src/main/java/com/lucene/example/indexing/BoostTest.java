package com.lucene.example.indexing;

import java.io.IOException;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * 加权操作
 * @author qiaolin
 *
 */
public class BoostTest {
	
	private final static String BAD_DOMAIN = "junyang.com";
	
	private final static String GOOD_DOMAIN = "126.com";
	
	private String domains[] = {"zhangone@junyang.com","zhangtwo@126.com","zhangsan@junyang.com"};
	
	private String contents[] = {"zhangone content ","zhangtwo content","zhangsan content "};
	
	private Directory directory;
	
	@Before
	public void init() throws IOException{
		directory = new RAMDirectory();//内存
		IndexWriter writer = getWriter();
		for(int i = 0; i<domains.length; i++){
			Document doc = new Document();
			Field field = new Field("senderEmail",domains[i],TextField.TYPE_STORED);
			Field contentField = new Field("content",contents[i],TextField.TYPE_STORED);
			if(domains[i].endsWith(BAD_DOMAIN)){
				field.setBoost(0.1f);
				contentField.setBoost(0.1f);
			}else if(domains[i].endsWith(GOOD_DOMAIN)){
				field.setBoost(15f);
				contentField.setBoost(15f);
			}
			doc.add(field);
			doc.add(contentField);
			writer.addDocument(doc);
		}
		writer.flush();
		writer.close();
	}
	
	private IndexWriter getWriter() throws IOException{
		Analyzer analyzer = new WhitespaceAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		return new IndexWriter(directory,config);
	}
	
	@Test
	public void testFieldBoost() throws IOException{
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		Term term = new Term("senderEmail","*com*");//模糊匹配
		Query query = new WildcardQuery(term);//实现模糊查询
		TopDocs topDocs = searcher.search(query, 10,new Sort(),true,true);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(ScoreDoc scoreDoc:scoreDocs){
			Document doc = searcher.doc(scoreDoc.doc);
			Explanation explan = searcher.explain(query, scoreDoc.doc);
			System.out.println(doc.get("senderEmail") + ";scoreDoc:" + scoreDoc);
			System.out.println(explan);
		}
	}
	
}
