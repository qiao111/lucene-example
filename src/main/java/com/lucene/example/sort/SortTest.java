package com.lucene.example.sort;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 搜索结果的排序操作
 * @author qiaolin
 *
 */
public class SortTest {
	
	@Before
	public void init() throws IOException{
		TestUtil.index();
	}
	
	@After
	public void delete() throws IOException{
		TestUtil.deleteIndexFiles();
	}
	
	/**
	 * 测试域排序字段
	 * @throws IOException
	 */
	@Test
	public void testFieldSort() throws IOException{
		Directory directory = TestUtil.getIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		Query allBooks = new MatchAllDocsQuery();
		//搜索 对于要搜索的字段必须以使用可用于搜索的字段进行存储类似于LongPoint的存储
		TopFieldDocs topFieldDocs = searcher.search(allBooks, 10, 
				new Sort(new SortField("title", SortField.Type.STRING,false)));//true 降序 false 升序
//		TopFieldDocs topFieldDocs = searcher.search(allBooks,10,new Sort().INDEXORDER);
		ScoreDoc[] scoreDocs = topFieldDocs.scoreDocs;
		System.out.println(topFieldDocs.fields.length);
		for(int i = 0; i<scoreDocs.length;i++){
			System.out.println(searcher.doc(scoreDocs[i].doc).get("title"));
		}
		
	}
}
