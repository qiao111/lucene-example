package com.lucene.example.collector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

/**
 * Ϊÿ�����url��title����һ��HashMap����
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
		//��ȡdocument
		Document document = context.reader().document(doc);
		documents.put(document.get("url"),document.get("title"));
	}
	
	@Override
	protected void doSetNextReader(LeafReaderContext context) throws IOException {
		this.context = context;
	}
	
	/**
	 * ���ز����޸ĵ�map����
	 * @return
	 */
	public Map<String,String> getLinks(){
		return Collections.unmodifiableMap(documents);
	}
	
}
