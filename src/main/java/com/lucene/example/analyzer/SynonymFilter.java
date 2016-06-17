package com.lucene.example.analyzer;

import java.io.IOException;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

/**
 * 同义词 过滤器 实现
 * @author qiaolin
 *
 */
public class SynonymFilter extends TokenFilter{
	
	//同义词 栈
	private Stack<String> synonymStack = null;
	// 项
	private CharTermAttribute charTerm ;
	//位置增量
	private PositionIncrementAttribute  positionIncrement;
	//获取同义词的接口
	private SynonymEngine engine ;
	//当前状态
	private AttributeSource.State current;
	protected SynonymFilter(TokenStream input,SynonymEngine engine) {
		super(input);
		this.engine = engine;
		synonymStack = new Stack<String>();
		charTerm = input.addAttribute(CharTermAttribute.class);
		positionIncrement = input.addAttribute(PositionIncrementAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if(synonymStack.size() > 0){
			restoreState(current);
			charTerm.setEmpty();
			charTerm.append(synonymStack.pop());//添加同义词
			positionIncrement.setPositionIncrement(0);//设置同义词位置
			return true;//这个地方要返回，否则分词出错
		}
		if(!input.incrementToken()){
			return false;
		}
		if(addSynonymToStack()){//添加同义词
			current = captureState();
		}
		return true;
	}
	
	public boolean addSynonymToStack(){
		String[] synonyms = engine.getSynonyms(charTerm.toString());
		if(synonyms == null){
			return false;
		}
		for(String synonym:synonyms){
			synonymStack.push(synonym);//添加同义词
		}
		return true;
	}

}
