package com.lucene.example.indexing;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

public class LockTest {

	private Directory dir;
	
	@Before
	public void init() throws IOException{
		String indexDir = System.getProperty("java.io.tmpdir","tmp")
				+ System.getProperty("file.separator") + "index";
		dir = FSDirectory.open(new File(indexDir).toPath());
	}
	
	@Test
	public void testWriteLock() throws IOException{
		IndexWriter writer = new IndexWriter(dir,new IndexWriterConfig(new SimpleAnalyzer()));
		IndexWriter writer2 = null;
		try{
			writer2 = new IndexWriter(dir,new IndexWriterConfig(new SimpleAnalyzer()));
			fail("we should never reache this point ");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			writer.close();
			assertNull(writer2);
		}
		
	}
	
}
