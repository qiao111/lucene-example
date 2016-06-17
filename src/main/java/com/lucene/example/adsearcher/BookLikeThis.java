package com.lucene.example.adsearcher;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import com.lucene.example.util.TestUtil;

public class BookLikeThis {
	
	private IndexReader reader;
	
	private IndexSearcher searcher;
	
	private BookLikeThis(IndexReader reader){
		this.reader = reader;
		this.searcher = new IndexSearcher(reader);
	}
	
	/**
	 * 查找相似书籍
	 * @param id
	 * @param max
	 * @return
	 * @throws IOException
	 */
	public Document[] docsLike(int id,int max) throws IOException{
		Document[] results = null;
		Document source = reader.document(id);
		String[] authors = source.getValues("author");
		Builder builder = new BooleanQuery.Builder();
		//排除当前书籍 此处对isbn进行小写 因为分词器进行了小写的操作 否则有的书籍自身无法排除
		builder.add(new TermQuery(new 
				Term("isbn",source.get("isbn").toLowerCase())),
				Occur.MUST_NOT);
		for(String author:authors){
			builder.add(new TermQuery(new Term("author",author)),Occur.SHOULD);
		}
		//获取subject中的分词
		Terms terms =  MultiFields.getTerms(reader, "subject");
		TermsEnum termsEnum = terms.iterator();
		BytesRef term = null;
		while((term = termsEnum.next()) != null){
			//转换成string
			builder.add(new TermQuery(new Term("subject",term.utf8ToString())),Occur.SHOULD);
		}
		BooleanQuery query = builder.build();
		TopDocs topDocs = searcher.search(query, max);
		results = new Document[topDocs.totalHits];
		for(int i =0; i<results.length;i++){
			results[i] = searcher.doc(topDocs.scoreDocs[i].doc);
		}
		return results;
	}
	
	public static void main(String[] args) throws IOException {
		TestUtil.index();
		IndexReader reader = DirectoryReader.open(TestUtil.getIndexDirectory());
		BookLikeThis blt = new BookLikeThis(reader) ;
		int max = reader.maxDoc();
		for(int i = 0; i<max;i++){
			System.out.println("======================");
			Document doc = reader.document(i);
			System.out.println(doc.get("title") + ",isbn:" + doc.get("isbn") );
			Document[] docs = blt.docsLike(i, max);
			for(Document likeThis:docs){
				System.out.println("---> " + likeThis.get("title") + ",isbn:" + likeThis.get("isbn"));
			}
		}
		TestUtil.deleteIndexFiles();
	}
}
