package com.lucene.example.analyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

public class AnalyzerTest {

	
	/**
	 * 不同分析器的分析的不同结果
	 * @throws IOException
	 */
	@Test
	public void testAnalyzerTokens() throws IOException{
		String[] examples = {"The quick brown fox jumped over the lazy dog",
					"XY&Z Corporation - xyz@example.com"};
		Analyzer[] analyzers = new Analyzer[]{
				new WhitespaceAnalyzer(),new SimpleAnalyzer(),
				new StopAnalyzer(),new StandardAnalyzer()};
		for(Analyzer analyzer :analyzers){
			System.out.println("========" + analyzer.getClass().getName()+ "=====");
			for(String text:examples){
				TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(text));
				CharTermAttribute token = tokenStream.addAttribute(CharTermAttribute.class);
				tokenStream.reset();//否则 IllegalStateException
				while(tokenStream.incrementToken()){//是否有后续的语汇
					System.out.print("[" + token.toString() + "]");
				}
				tokenStream.close();//必须关闭，下次循环重新打开
				System.out.println();
			}
		}
	}
	
	/**
	 * 深入解析分析器，语汇单元的属性：项、偏移量、类型、位置增量
	 * @throws IOException 
	 */
	@Test
	public void testTokensWithFullDetails() throws IOException{
		String[] examples = {"The quick brown fox jumped over the lazy dog",
		"XY&Z Corporation - xyz@example.com"};
		Analyzer[] analyzers = new Analyzer[]{
			new WhitespaceAnalyzer(),new SimpleAnalyzer(),
			new StopAnalyzer(),new StandardAnalyzer()};
		for(Analyzer analyzer:analyzers){
			System.out.println("========" + analyzer.getClass().getName()+ "=====");
			for(String text:examples){
				System.out.println("==============================");
				TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
				//项
				CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
				//位置增量
				PositionIncrementAttribute position = tokenStream.addAttribute(PositionIncrementAttribute.class);
				//类型
				TypeAttribute type = tokenStream.addAttribute(TypeAttribute.class);
				//偏移量
				OffsetAttribute offset = tokenStream.addAttribute(OffsetAttribute.class);
				tokenStream.reset();
				int posi = 0;
				while(tokenStream.incrementToken()){
					//设置位置的增量，默认为1。起始位置从增量开始，每次增加增量个步长
					position.setPositionIncrement(2);
					int increament = position.getPositionIncrement();
					posi += increament;
					System.out.print("[项:" + term.toString());
					System.out.print(",位置:" + posi);
					System.out.print(",类型:" + type.type());
					System.out.print(",偏移量起始:" + offset.startOffset());
					System.out.println(",偏移量结束:" + offset.endOffset() + "]");
				}
				tokenStream.close();
			}
		}
	}
	
	/**
	 * 使用Metaphone 完成近音词的搜索。 Metaphone同音算法
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testMetaphoneAnalyzer() throws IOException, ParseException{
		Directory directory = new RAMDirectory();
		Analyzer analyzer = new MetaphoneReplacementAnalyzer();
		IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
		Document doc = new Document();
		doc.add(new TextField("content","cool cat",Store.YES));
		writer.addDocument(doc);
		writer.close();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new QueryParser("content", analyzer).parse("kool kat");
		TopDocs topDoc = searcher.search(query, 1);
		assertEquals(1,topDoc.totalHits);//正确查询出
		directory.close();
		//看下词汇的过程
		TokenStream stream = analyzer.tokenStream("content", new StringReader("cool cat"));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		TypeAttribute type = stream.addAttribute(TypeAttribute.class);
		stream.reset();
		while(stream.incrementToken()){
			System.out.print("[" + term.toString() + "," + type.type() + "]");
		}
		stream.close();
	}
	
	/**
	 * 同义词的测试
	 * @throws IOException
	 * @throws ParseException 
	 */
	@Test
	public void testSynonymAnalyzer() throws IOException, ParseException{
		Directory directory = new RAMDirectory();
		SynonymEngine engine = new TestSynonymEngineImpl();
		Analyzer analyzer = new SynonymAnalyzer(engine);
		IndexWriter writer = new IndexWriter(directory,new IndexWriterConfig(analyzer));
		Document document = new Document();
		document.add(new TextField("content","The quick brown fox jumps over the lazy dog",Store.YES));
		writer.addDocument(document);
		writer.close();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query query = new TermQuery(new Term("content","fast"));
		//短语的测试 query:  content:"fox (jumps hops leaps)"
		query = new QueryParser("content",analyzer).parse("\"fox jumps\"");
		System.out.println(query.toString());
		TopDocs topDocs = searcher.search(query, 1);
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			System.out.println(searcher.doc(scoreDoc.doc));
		}
		directory.close();
		//看下词汇的过程
		TokenStream stream = analyzer.tokenStream("content", new StringReader("The quick brown fox jumps over the lazy dog"));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute positionIncrement = stream.addAttribute(PositionIncrementAttribute.class);
		stream.reset();
		int position = 0;
		while(stream.incrementToken()){
			position += positionIncrement.getPositionIncrement();
			System.out.print("[" + term.toString() + "," + position + "]");
		}
		stream.close();
	}
	
	/**
	 * 词干提取和停用词
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testPositionalPorterStopAnalyzer() throws IOException, ParseException{
		Directory directory = new RAMDirectory();
		Analyzer analyzer = new PositionalPorterStopAnalyzer();
		IndexWriter writer = new IndexWriter(directory,new IndexWriterConfig(analyzer));
		Document document = new Document();
		document.add(new TextField("content","The quickly brown fox jumps over the lazy dog",Store.YES));
		writer.addDocument(document);
		writer.close();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		//短语的测试 query:  laziness 解析成词干 lazi
		Query query = new QueryParser("content",analyzer).parse("laziness");
		System.out.println(query.toString());
		TopDocs topDocs = searcher.search(query, 1);
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			System.out.println(searcher.doc(scoreDoc.doc));
		}
		directory.close();
		//看下词汇的过程
		TokenStream stream = analyzer.tokenStream("content", new StringReader("The quick brown fox jumps over the lazy dog"));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute positionIncrement = stream.addAttribute(PositionIncrementAttribute.class);
		stream.reset();
		int position = 0;
		while(stream.incrementToken()){
			position += positionIncrement.getPositionIncrement();
			System.out.print("[" + term.toString() + "," + position + "]");
		}
		stream.close();
	}
}
