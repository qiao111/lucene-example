package com.lucene.example.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * tika 索引操作
 * @author qiaolin
 *
 */
public class TikaIndexer{
	
	private boolean DEBUG = true;
	
	private IndexWriter writer;
	
	private static Set<String> textualMetadataFields = new HashSet<String>();
	
	static{
		textualMetadataFields.add(Property.externalTextBag("title").getName());
		textualMetadataFields.add(Property.externalTextBag("author").getName());
		textualMetadataFields.add(Property.externalTextBag("comments").getName());
		textualMetadataFields.add(Property.externalTextBag("keywords").getName());
		textualMetadataFields.add(Property.externalTextBag("description").getName());
	}
	
	public TikaIndexer(String indexDir) throws IOException {
		Directory directory = FSDirectory.open(new File(indexDir).toPath());
		writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
	}
	
	public int index(String dataDir) throws IOException{
		File file = new File(dataDir);
		File indexFiles[] = file.listFiles();
		for(File f:indexFiles){
			if(f.isDirectory()){
				index(f.getPath());
			}else if(f.isFile()){
				indexFile(f);
			}
		}
		return writer.numDocs();
	}

	private void indexFile(File file) throws IOException {
		Document document = getDocument(file);
		writer.addDocument(document);
	}

	private Document getDocument(File file) throws IOException {
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
		InputStream input = new FileInputStream(file);
		Parser parser = new AutoDetectParser();
		ContentHandler handler = new BodyContentHandler();
		ParseContext parseContext = new ParseContext();
		parseContext.set(Parser.class,parser);
		try {
			parser.parse(input, handler, metadata, parseContext);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}finally{
			input.close();
		}
		Document document = new Document();
		document.add(new TextField("content",handler.toString(),Store.YES));
		if(DEBUG){
			System.out.println("all text:" + handler.toString());
		}
		for(String name:metadata.names()){
			String value = metadata.get(name);
			
			if(textualMetadataFields.contains(name)){
				document.add(new TextField("content",value,Store.YES));
				document.add(new TextField(name,value,Store.YES));
				if(DEBUG){
					System.out.println("name:" + name + ",value:" + value);
				}
			}
			
		}
		if(DEBUG){
			System.out.println();
		}
		document.add(new TextField("filename",file.getCanonicalPath(),Store.YES));
		return document;
	}

	public static void main(String[] args) throws IOException {
		String indexDir = "src/main/resources/tikaIndex";
		String dataDir = "src/main/resources/tikaData";
		long start = System.currentTimeMillis();
		TikaIndexer indexer = new TikaIndexer(indexDir);
		int num = indexer.index(dataDir);
		long end = System.currentTimeMillis();
		System.out.println("num:" + num + ",时间:" + (end -start));
	}
}
