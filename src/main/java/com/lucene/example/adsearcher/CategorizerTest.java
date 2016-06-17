package com.lucene.example.adsearcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.junit.Before;
import org.junit.Test;

import com.lucene.example.util.TestUtil;

/**
 * 书籍分类:向量余弦夹角=B.C/||B||||C||=x1*y1+x2*y2 / (根号：x1的平方 + x2的平方)*(根号：y1的平方 + y2的平方)
 * 利用项向量寻找最佳书籍分类
 * @author qiaolin
 *
 */
@SuppressWarnings("rawtypes")
public class CategorizerTest {
	
	private Map<String, Map> categoryMap;
	
	
	@Before
	public void init() throws IOException{
		categoryMap = new TreeMap<String, Map>();
		buildCategoryVectors();
//		displayCategoryVectors();
	}
	
	@Test
	public void test(){
		System.out.println("------------------------------------------");
		System.out.println(getCategory("action ant"));
		System.out.println("------------------------------------------");
		System.out.println(getCategory("montessori education philosophy"));
	}
	
	/**
	 * 获取书的最佳类别
	 * @param subject
	 * @return
	 */
	public String getCategory(String subject){
		String[] words = subject.split(" ");
		Iterator<String> categoryIter = categoryMap.keySet().iterator();
		double bestAngle = Double.MAX_VALUE;
		String bestCategory = null;
		while(categoryIter.hasNext()){
			String category = categoryIter.next();
			double angle = computeAngle(words, category);
			System.out.println("category:" + category + ":" + angle);
			if(angle < bestAngle){
				bestAngle = angle;
				bestCategory = category;
			}
		}
		return bestCategory;
	}
	
	/**
	 * 计算项向量
	 * @param words
	 * @param category
	 * @return
	 */
	private double computeAngle(String[] words,String category){
		Map vectorMap = categoryMap.get(category);
		int dotProduct = 0;
		int sumOfSquares = 0;
		for(String word:words){
			long categoryWordFreq = 0;
			if(vectorMap.containsKey(word)){
				categoryWordFreq = (Long) vectorMap.get(word);
			}
			dotProduct += categoryWordFreq; // x1*y1+ x2*y2
			// x1*x1 + x2*x2  words.length = y1*y1 + y2*y2
			sumOfSquares += categoryWordFreq * categoryWordFreq; 
		}
		double denomination ;
		if(sumOfSquares == words.length){
			denomination = sumOfSquares; 
		}else{
			denomination = Math.sqrt(sumOfSquares) *Math.sqrt(words.length);
		}
		double ratio = dotProduct/denomination;
		return Math.acos(ratio);
	}
	
	/**
	 * 聚合各个类别建立类别项向量
	 * @throws IOException
	 */
	private void buildCategoryVectors() throws IOException{
		TestUtil.index();
		IndexReader reader = DirectoryReader.open(TestUtil.getIndexDirectory());
		int maxDoc = reader.maxDoc();
		for(int i = 0; i<maxDoc;i++){
			Document document = reader.document(i);
			String category = document.get("category");
			@SuppressWarnings("unchecked")
			Map<String, Long> vectorMap = categoryMap.get(category);
			if(vectorMap == null){
				vectorMap = new TreeMap<String, Long>();
				categoryMap.put(category,vectorMap);
			}
			Terms terms = MultiFields.getTerms(reader,"subject");
			addTermFreqToMap(vectorMap,terms);
		}
		TestUtil.deleteIndexFiles();
	}

	/**
	 * 每个类别聚集项频率
	 * @param vectorMap
	 * @param terms
	 * @throws IOException
	 */
	private void addTermFreqToMap(Map<String, Long> vectorMap, Terms terms) throws IOException {
		TermsEnum termsEnum = terms.iterator();
		BytesRef term = null;
		while((term = termsEnum.next()) != null){
			if(vectorMap.containsKey(term.utf8ToString())){
				long value = vectorMap.get(term.utf8ToString());
				vectorMap.put(term.utf8ToString(), value + termsEnum.totalTermFreq());
			}else{
				vectorMap.put(term.utf8ToString(), termsEnum.totalTermFreq());
			}
		}
	}
	
	/**
	 * 显示类别下的关键字的频率
	 */
	@SuppressWarnings("unused")
	private void displayCategoryVectors(){
		Iterator<String> categoryIterator = categoryMap.keySet().iterator();
		while(categoryIterator.hasNext()){
			String category = categoryIterator.next();
			System.out.println("Category:" + category);
			Map vector = categoryMap.get(category);
			Iterator vectorIter = vector.keySet().iterator();
			while(vectorIter.hasNext()){
				String term = (String) vectorIter.next();
		        System.out.println("    " + term + " = " + vector.get(term));
			}
		}
	}
}
