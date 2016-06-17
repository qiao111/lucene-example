package com.lucene.example.analyzer;

import java.util.HashMap;
import java.util.Map;

public class TestSynonymEngineImpl implements SynonymEngine {

	private static Map<String,String[]> map = new HashMap<String,String[]>();
	
	static{
	    map.put("quick", new String[] {"fast", "speedy"});
	    map.put("jumps", new String[] {"leaps", "hops"});
	    map.put("over", new String[] {"above"});
	    map.put("lazy", new String[] {"apathetic", "sluggish"});
	    map.put("dog", new String[] {"canine", "pooch"});
	}
	
	@Override
	public String[] getSynonyms(String word) {
		return map.get(word);
	}

}
