package com.lucene.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class TestUtil {
	//数据文件位置
	private static final String DATA_PATH ="src/main/resources/data";
	//索引后的文件位置
	private static final String INDEX_PATH = "src/main/resources/index";
	
	/**
	 * 查找指定文件下的所有属性文件
	 * @param results
	 * @param root
	 */
	public static void findFiles(List<File> results,File root){
		for(File file:root.listFiles()){
			if(file.isFile() && file.getName().endsWith(".properties")){
				results.add(file);
			}else if(file.isDirectory()){
				findFiles(results,file);
			}
		}
	}
	
	/**
	 * 
	 * @param rootDir 数据存放位置
	 * @param file 属性文件
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Document getDocument(String rootDir,File file) throws FileNotFoundException, IOException{
		Document document = new Document();
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));// 属性文件
		
		String title = prop.getProperty("title");       
	    String author = prop.getProperty("author");     
	    String url = prop.getProperty("url");           
	    String subject = prop.getProperty("subject");   

		
		document.add(new Field("isbn",prop.getProperty("isbn"),TextField.TYPE_STORED));
		document.add(new TextField("title",title,Store.YES));
		//此字段可以进行排序 但是并不存储
		document.add(new SortedDocValuesField("title",new BytesRef(title.getBytes())));
		document.add(new Field("title2",title.toLowerCase(),TextField.TYPE_STORED));
		//不进行存储
		Field field = new LongPoint("pubmonth",Long.parseLong(prop.getProperty("pubmonth")));
		document.add(field);//数值类型
		//存储起来
		field = new StoredField("pubmonth", Long.parseLong(prop.getProperty("pubmonth")));
		document.add(field);//数值类型 
		document.add(new Field("subject",subject,TextField.TYPE_STORED));
		document.add(new Field("url",url,TextField.TYPE_STORED));
		
		String authors[] = prop.getProperty("author").split(",");
		for(String au:authors){
			document.add(new Field("author",au,TextField.TYPE_STORED));
		}
		
		String category = file.getParent().substring( rootDir.length());
		category = category.replace(File.separator, "/");
		document.add(new Field("category",category,TextField.TYPE_STORED));
		
		Date date ;
		try{
			date = DateTools.stringToDate(prop.getProperty("pubmonth"));
		}catch(ParseException e){
			throw new RuntimeException(e);
		}
		int day = (int) (date.getTime()/(1000*24*3600));
		
		//只索引 
		document.add(new IntPoint("pubmonthAsDay", day));
	
		for(String content:new String[]{title, subject, author, category}){
			document.add(new Field("content",content,TextField.TYPE_STORED));
		}
		//进行存储
		document.add(new StoredField("pubmonthAsDay", day));
		return document;
	}
	
	/**
	 * 建立索引的过程
	 * @throws IOException
	 */
	public static void index() throws IOException{
		List<File> results = new ArrayList<File>();
		findFiles(results, new File(DATA_PATH));//属性文件的存放路径
		Directory directory = FSDirectory.open(new File(INDEX_PATH).toPath());
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(directory,config);
		for(File file:results){
			Document doc = getDocument(DATA_PATH, file);
			writer.addDocument(doc);
		}
		writer.close();
		directory.close();
	}
	
	/**
	 * 获取索引的存储目录
	 * @return
	 * @throws IOException
	 */
	public static Directory getIndexDirectory() throws IOException{
		return FSDirectory.open(new File(INDEX_PATH).toPath());
	}
	
	/**
	 * 删除生成的索引文件
	 */
	public static void deleteIndexFiles(){
		File file = new File(INDEX_PATH);
		for(File f:file.listFiles()){
			if(f.isFile())
				f.deleteOnExit();
		}
	}
}
