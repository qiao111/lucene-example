package com.lucene.example.payload;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.payloads.AveragePayloadFunction;
import org.apache.lucene.queries.payloads.PayloadScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

public class TestPayloads {
	
	private String[] titles = new String[]{"Hurricane warning","Warning label maker","Tornado warning"};
	private String[] contents = new String[]{"Bulletin: A hurricane warning was issued at 6 AM for the outer great banks",
				"The warning label maker is a delightful toy for your precocious seven year old's warning needs",
				"Bulletin: There is a tornado warning for Worcester county until 6 PM today"};

	@Test
	public void testBulletinPayloads() throws IOException{
		Directory directory = new RAMDirectory();
		BulletinPayloadsAnalyzer analyzer = new BulletinPayloadsAnalyzer(new BytesRef("5".getBytes()));
		IndexWriter writer = new IndexWriter(directory,new IndexWriterConfig(analyzer));
		for(int i = 0; i<titles.length;i++){
			Document document = new Document();
			document.add(new TextField("title",titles[i],Store.YES));
			document.add(new TextField("content",contents[i],Store.YES));
		    analyzer.setIsBulletin(contents[i].startsWith("Bulletin:"));
			writer.addDocument(document);
		}
		writer.close();
		Term warning = new Term("content","warning");
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new TermQuery(warning);
		TopDocs topDoc = searcher.search(query, 10);
		for(ScoreDoc score:topDoc.scoreDocs){
			System.out.println(searcher.doc(score.doc).get("title") + "," + score.score);
		}
		System.out.println(topDoc.totalHits);
		for(int i = 0; i<titles.length;i++){
			displayToken(analyzer, "title",new StringReader( titles[i]));
			displayToken(analyzer, "content",new StringReader( contents[i]));
		}
		SpanQuery spanQuery = new SpanTermQuery(warning);
		query = new PayloadScoreQuery(spanQuery, new AveragePayloadFunction());
		topDoc = searcher.search(query, 10);
		System.out.println(topDoc.totalHits);
		for(ScoreDoc score:topDoc.scoreDocs){
			System.out.println(searcher.doc(score.doc).get("title") + "," + score.score);
		}
	}
	
	public void displayToken(Analyzer analyzer,String fieldName,Reader reader) throws IOException{
		TokenStream stream = analyzer.tokenStream(fieldName, reader);
		CharTermAttribute charTerm = stream.addAttribute(CharTermAttribute.class);
		PayloadAttribute payload = stream.addAttribute(PayloadAttribute.class);
		stream.reset();
		while(stream.incrementToken()){
			System.out.print(charTerm.toString() + " ");
		}
		System.out.println();
		stream.close();
	}
}
