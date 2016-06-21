package com.lucene.example.collector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

/**
 * 为每本书的url和title创建一个HashMap对象
 * @author qiaolin
 *
 */
public class BookLinkCollector extends SimpleCollector{
	
	private LeafReaderContext context;
	private Map<String,String> documents = new HashMap<String,String>();

	@Override
	public boolean needsScores() {
		return false;
	}

	@Override
	public void collect(int doc) throws IOException {
		//获取document
		Document document = context.reader().document(doc);
		documents.put(document.get("url"),document.get("title"));
	}
	
	@Override
	protected void doSetNextReader(LeafReaderContext context) throws IOException {
		this.context = context;
	}
	
	/**
	 * 返回不可修改的map集合
	 * @return
	 */
	public Map<String,String> getLinks(){
		return Collections.unmodifiableMap(documents);
	}
	
}
