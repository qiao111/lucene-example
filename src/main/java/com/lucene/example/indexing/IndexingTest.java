package com.lucene.example.indexing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * 索引操作
 * @author qiaolin
 *
 */
public class IndexingTest{
	
	private Directory directory;
	
	protected String[] ids = {"1","2"};
	
	protected String[] unindexed = {"China ","USA "};
	
	protected String[] unstored = {"This is China content. ","This is USA content "};
	
	protected String[] text = {"BeiJing","LSA "};
	
	@Before
	public void init() throws Exception{
		directory = new RAMDirectory();//内存存储
//		directory = FSDirectory.open("d:index.txt");
		IndexWriter writer = getWriter();//创建IndexWriter对象
		for(int i = 0; i<ids.length; i++){
			Document doc = new Document();
			doc.add(new Field("id",ids[i],TextField.TYPE_STORED));
			doc.add(new Field("country",unindexed[i],TextField.TYPE_STORED));
			doc.add(new Field("contents",unstored[i],TextField.TYPE_STORED));
			doc.add(new Field("city",text[i],TextField.TYPE_STORED));
			writer.addDocument(doc);//将文档加入到索引中
		}
		writer.close();
	}
	
	private IndexWriter getWriter() throws IOException{
		// Lucene 4.0之后的做法
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		return new IndexWriter(directory,config);
	}
	
	/**
	 * 
	 * @param fieldName  搜索的域名
	 * @param searchString 搜索的内容
	 * @return
	 * @throws IOException
	 */
	protected int getHitCount(String fieldName,String searchString) throws IOException{
		IndexReader ireader = DirectoryReader.open(directory); //创建IndexReader对象
		IndexSearcher searcher = new IndexSearcher(ireader); //创建IndexSearcher对象
		// Term代表了文本的一个单词，是搜索的单元，由两部分组成：域和域值
		Term term = new Term(fieldName,searchString);  
		Query query = new TermQuery(term);
		int hitCount = searcher.search(query, 1).totalHits;
		return hitCount;
	}

	@Test
	public void textIndexWriter() throws IOException{
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());//写入文档
		writer.close();
	}
	
	@Test
	public void testHitCount() throws IOException{
		System.out.println(getHitCount("contents","usa"));
	}
	
	@Test
	public void testDeleteBeforeOptimize() throws IOException{
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());//索引的文件数量
		writer.deleteDocuments(new Term[]{new Term("id","1")});//删除ID的文档
		writer.commit();
		assertEquals(true, writer.hasDeletions());//确认有删除文档的标记
		assertEquals(2, writer.maxDoc());
		assertEquals(1, writer.numDocs()); // 删除一个文档
		writer.close();
	}
	
	@Test
	public void testDeleteAfterOptimize() throws IOException{
		IndexWriter writer = getWriter();
		writer.deleteDocuments(new Term("id","2"));
		writer.forceMerge(1);//删除后合并索引
		writer.commit();
		assertEquals(false, writer.hasDeletions());//没有删除文档的标记
		assertEquals(1, writer.maxDoc());
		assertEquals(1, writer.numDocs());
		writer.close();
	}
	
	@Test
	public void testUpdateIndex() throws IOException{
		IndexWriter writer = getWriter();
		Document doc = new Document();
		doc.add(new Field("id","2",TextField.TYPE_STORED));
		doc.add(new Field("country","UK",TextField.TYPE_STORED));
		doc.add(new Field("contents","This is a UK content ",TextField.TYPE_STORED));
		doc.add(new Field("city","Lodon",TextField.TYPE_STORED));
		writer.updateDocument(new Term("id","1"), doc);//将文档替换
		writer.commit();
		writer.close();
		assertEquals(0, getHitCount("country", "china"));//此处的搜索字段只能为小写
		assertEquals(1, getHitCount("country","uk"));//此处的搜索字段只能为小写
		assertEquals(2, getHitCount("id","2"));
		
	}
}
