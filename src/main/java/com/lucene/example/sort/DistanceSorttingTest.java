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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;

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
}
