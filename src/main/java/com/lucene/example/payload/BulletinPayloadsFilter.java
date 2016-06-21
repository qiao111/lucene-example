package com.lucene.example.payload;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;


/**
 * 分析期间生成有效载荷
 * @author qiaolin
 *
 */
public class BulletinPayloadsFilter extends TokenFilter {
	
	private CharTermAttribute charTerm;
	
	private PayloadAttribute payload;
	
	private boolean isBulletin;
	
	private BytesRef boostPayload;

	protected BulletinPayloadsFilter(TokenStream input,BytesRef warningBoost) {
		super(input);
		charTerm = input.addAttribute(CharTermAttribute.class);
		payload = input.addAttribute(PayloadAttribute.class);
		boostPayload =warningBoost;
	}

	@Override
	public boolean incrementToken() throws IOException {
		if(input.incrementToken()){
			if(isBulletin && "warning".equals(charTerm.toString())){
				payload.setPayload(boostPayload);
			}else{
				payload.setPayload(null);
			}
			return true;
		}
		return false;
	}
	
	public void setIsBulletin(boolean isBulletin){
		this.isBulletin = isBulletin;
	}

}
