package com.lucene.example.query.parser;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;


public class QueryParserTest {
	
	
	@Test
	public void testCutomeQueryParser() throws IOException{
		CustomeQueryParser queryParser = new CustomeQueryParser("field", new StandardAnalyzer());
		try {
			queryParser.parse("a?t");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
