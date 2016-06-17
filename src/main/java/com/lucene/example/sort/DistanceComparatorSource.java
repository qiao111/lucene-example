package com.lucene.example.sort;

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.LeafFieldComparator;

public class DistanceComparatorSource extends FieldComparatorSource{

	private int x;
	private int y;
	
	public DistanceComparatorSource(int x,int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed)
			throws IOException {
		return null;
	}
	
	private class DiatnceScoreDocLookupComparator extends FieldComparator {
		
		private String field;
		
		@Override
		public int compare(int slot1, int slot2) {
			return 0;
		}

		@Override
		public void setTopValue(Object value) {
			
		}

		@Override
		public Object value(int slot) {
			return null;
		}

		@Override
		public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
			Terms terms = MultiFields.getTerms(context.reader(), field);
			return null;
		}

	
	}

}
