package com.lucene.example.analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;


/**
 * 
 * @author qiaolin
 *
 */
public class PreFieldAnalyzerWapperTest {
	
	/** 
	 * 索引创作期间
	 * @throws IOException
	 */
	@Test
	public void testPerFieldAnalyzerWapper() throws IOException{
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		Map<String, Analyzer> fieldAnalyzer = new HashMap<String,Analyzer>();
		fieldAnalyzer.put("title", new WhitespaceAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer,fieldAnalyzer);
		Directory directory = new RAMDirectory();
		IndexWriter writer = new IndexWriter(directory,new IndexWriterConfig(analyzer));
		Document document = new Document();
		//对title使用WhitespaceAnalyzer
		document.add(new TextField("title","The quick brown fox jumps over the lazy dog",Store.YES));
		//对content采用默认的analyzer
		document.add(new TextField("content","The quick brown fox jumps over the lazy dog",Store.YES));
		writer.addDocument(document);
		writer.close();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new MatchAllDocsQuery();//匹配所有文档
		TopDocs topDocs = searcher.search(query, 10);
		for(ScoreDoc score:topDocs.scoreDocs){
			System.out.println(displayAnalyzer(analyzer, searcher.doc(score.doc), "title"));
			System.out.println(displayAnalyzer(analyzer, searcher.doc(score.doc), "content"));
		}
	}
	
	/**
	 * 解析语汇单元
	 * @param analyzer
	 * @param doc
	 * @param field
	 * @return
	 * @throws IOException
	 */
	private String displayAnalyzer(Analyzer analyzer,Document doc,String field) throws IOException{
		TokenStream tokenStream = analyzer.tokenStream(field, new StringReader(doc.get(field)));
		CharTermAttribute charTerm = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		StringBuilder builder = new StringBuilder(field).append(":");
		while(tokenStream.incrementToken()){
			builder.append(charTerm.toString());
			builder.append(" ");
		}
		tokenStream.close();
		return builder.toString();
	}
}
