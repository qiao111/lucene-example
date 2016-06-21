package com.lucene.example.sort;

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.Scorer;

/**
 * 距离比较器
 * @author qiaolin
 *
 */
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
		return new DetainsScoreDocLookupComparator(fieldname,numHits);
	}
	
	/**
	 * 定义比较器
	 * @author qiaolin
	 *
	 */
	private class DetainsScoreDocLookupComparator extends FieldComparator<Float> implements LeafFieldComparator{
		
		private String field;
		//对应的评分最低的搜索结果
		private float bottom;
		//保存队列中所有符合搜索要求的结果文档
		private float[] values;
		private LeafReaderContext context;
	    private Scorer scorer;
		
		public DetainsScoreDocLookupComparator(String field,int numHits){
			this.field = field;
			values = new float[numHits];
		}
		
		@Override
		public int compare(int slot1, int slot2) {
			if(values[slot1] > values[slot2]) return 1;
			if(values[slot1] < values[slot2]) return -1;
			return 0;
		}

		@Override
		public void setTopValue(Float value) {
		}

		@Override
		public Float value(int slot) {
			return values[slot];
		}

		@Override
		public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
			this.context = context;
			return this;
		}

		@Override
		public void setBottom(int slot) {
			bottom = values[slot];
		}

		@Override
		public int compareBottom(int doc) throws IOException {
			float distance = getDistance(doc);
			if(bottom > distance) return 1;
			if(bottom < distance) return -1;
			return 0;
		}

		@Override
		public int compareTop(int doc) throws IOException {
			return 0;
		}

		@Override
		public void copy(int slot, int doc) throws IOException {
			values[slot] = getDistance(doc);
		}

		@Override
		public void setScorer(Scorer scorer) {
			this.scorer = scorer;
		}
		
		/**
		 * 获取坐标并计算与给定坐标的距离
		 * @param doc
		 * @return
		 * @throws IOException 
		 */
		private float getDistance(int doc) throws IOException{
			String location = context.reader().document(doc).get(field);
			int deltax = Integer.valueOf(location.split(",")[0]) - x;
			int deltay = Integer.valueOf(location.split(",")[1]) - y;
			return (float) Math.sqrt(deltax*deltax + deltay*deltay);
		}
	}

}
