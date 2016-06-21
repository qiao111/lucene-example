package com.lucene.example.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * sax 解析xml文件并转换成document
 * @author qiaolin
 *
 */
public class SAXXMLDocuemnt extends DefaultHandler{
	
	private StringBuilder builder = new StringBuilder();
	
	private Map<String,String> attributeMap = new HashMap<String,String>();
	
	private Document document;
	
	public Document getDocument(InputStream input){
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(input, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return document;
	}
	
	@Override
	public void startDocument() throws SAXException {
		document = new Document();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		int numAtts = attributes.getLength();
		if(numAtts > 0){
			for(int i = 0; i<numAtts;i++){
				attributeMap.put(attributes.getQName(i), attributes.getValue(i));
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if("address-book".equals(qName)){
			return;
		}else if("contact".equals(qName)){
			for(Entry<String, String> entry:attributeMap.entrySet()){
				String name = entry.getKey();
				String value = entry.getValue();
				document.add(new TextField(name,value,Store.YES));
			}
		}else{
			document.add(new TextField(qName,builder.toString(),Store.YES));
		}
		builder.delete(0, builder.capacity());
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(ch,start,length);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		SAXXMLDocuemnt handle = new SAXXMLDocuemnt();
		String path = "src/main/java/com/lucene/example/tika/data/addressbook.xml";
		Document doc = handle.getDocument(new FileInputStream(new File(path)));
		System.out.println(doc);
	}
}
