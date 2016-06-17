package com.lucene.example.language;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

/**
 * 多语种分析测试
 * @author qiaolin
 *
 */
public class LanguageTest {
	
	/**
	 * 测试不同分析器对汉语的解析过程
	 * @throws IOException
	 */
	@Test
	public void testLanguageAnalyzer() throws IOException{
		Analyzer[] analyzers = new Analyzer[]{
				new SimpleAnalyzer(),
				new StandardAnalyzer(),
				new CJKAnalyzer()
		};
		for(Analyzer analyzer:analyzers){
			System.out.println("=========" + analyzer.getClass().getSimpleName() + "==========");
			TokenStream stream = analyzer.tokenStream("content", new StringReader("道德经"));
			CharTermAttribute charTerm = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			while(stream.incrementToken()){
				System.out.println("[" + charTerm.toString() + "]");
			}
			stream.close();
		}
	}
	
	/**
	 * 测试CJK分析器查询单个词
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void test() throws IOException, ParseException{
		Directory directory = new RAMDirectory();
		IndexWriter writer = new IndexWriter(directory,new IndexWriterConfig(new CJKAnalyzer()));
		Document doc = new Document();
		doc.add(new TextField("title","道德经",Store.YES));
		writer.addDocument(doc);
		writer.close();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new QueryParser("title",new CJKAnalyzer()).parse("道");
		System.out.println(query.toString());
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc score:topDocs.scoreDocs){
			System.out.println(searcher.doc(score.doc));
		}
		
		
	}
}
