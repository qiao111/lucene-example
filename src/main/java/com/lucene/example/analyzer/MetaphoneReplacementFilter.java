package com.lucene.example.analyzer;

import java.io.IOException;

import org.apache.commons.codec.language.Metaphone;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * ½üÒô´Ê filter
 * @author qiaolin
 *
 */
public class MetaphoneReplacementFilter extends TokenFilter{
	
	public static final String METAPHONE = "metaphone";
	
	private Metaphone metaphone = new Metaphone();
	
	private CharTermAttribute charTermAttribute;
	
	private TypeAttribute typeAttribute;
	
	protected MetaphoneReplacementFilter(TokenStream input) {
		super(input);
		charTermAttribute = addAttribute(CharTermAttribute.class);
		typeAttribute = addAttribute(TypeAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if(!input.incrementToken()){
			return false;
		}
		//×ª»»ÎªMetaphone±àÂë
		String encoded = metaphone.encode(charTermAttribute.toString());
		typeAttribute.setType(METAPHONE);
		charTermAttribute.setEmpty();
		charTermAttribute.append(encoded);
		return true;
	}

}
