package com.lucene.example.query.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Query;

/**
 * 禁止使用通配符查询
 * @author qiaolin
 *
 */
public class CustomeQueryParser extends QueryParser{

	public CustomeQueryParser(String f, Analyzer a) {
		super(f, a);
	}
	
	@Override
	protected final Query getWildcardQuery(String field, String termStr) {
		try {
			throw new ParserException("Wildcard query not allowed!");
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected Query getFuzzyQuery(String field, String termStr, float minSimilarity)
			throws ParseException {
		try {
			throw new ParserException("Fuzzy query not allowed!");
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}
}
