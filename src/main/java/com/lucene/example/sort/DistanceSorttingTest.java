package com.lucene.example.sort;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

public class DistanceSorttingTest {
	private Directory directory;
	
	private IndexSearcher searcher;
	
	private Query query;
	
	/**
	 * @throws IOException
	 */
	@Before
	public void init() throws IOException{
		directory = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriter writer = new IndexWriter(directory,new IndexWriterConfig(analyzer));
		addPoint(writer, "EL Charro", "restaurant", 1, 2);
		addPoint(writer, "Cafe Poca Cosa", "restaurant", 5, 9);
		addPoint(writer, "Los Betos", "restaurant", 9, 6);
		addPoint(writer, "Nico's Taco Shop", "restaurant", 3, 8);
		writer.close();
		searcher = new IndexSearcher(DirectoryReader.open(directory));
	}
	
	private void addPoint(IndexWriter writer,String name,String type,int x,int y) throws IOException{
		Document doc = new Document();
		doc.add(new TextField("name",name,Store.YES));
		doc.add(new TextField("type",type,Store.YES));
		doc.add(new TextField("location",x+"," + y,Store.YES));
		writer.addDocument(doc);
	}
	
	/**
	 * 测试离家最近的餐馆
	 */
	@Test
	public void testNearHome(){
		query = new TermQuery(new Term("type","restaurant"));
		Sort sort = new Sort(new SortField("location", new DistanceComparatorSource(10, 10)));
		try {
			TopDocs topDocs = searcher.search(query, 10, sort);
			for(ScoreDoc score:topDocs.scoreDocs){
				Document doc = searcher.doc(score.doc);
				System.out.println(doc.get("name"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试离工作地点最近的餐馆并显示其距离
	 */
	@Test
	public void testNearWorkAndDistance(){
		query = new TermQuery(new Term("type","restaurant"));
		Sort sort = new Sort(new SortField("location", new DistanceComparatorSource(0, 0)));
		try {
			TopFieldDocs topFieldDocs = searcher.search(query, 10, sort);
			for(ScoreDoc score:topFieldDocs.scoreDocs){
				Document doc = searcher.doc(score.doc);
				FieldDoc fieldDoc = (FieldDoc) score;
				System.out.println("name:" + doc.get("name") + 
						",distance:" + fieldDoc.fields[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
