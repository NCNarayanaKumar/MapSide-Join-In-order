package mapsideJoin.issue;


import java.io.*;

import org.apache.hadoop.io.*;

public class TwoValueWritable implements WritableComparable<TwoValueWritable> {

  private Integer first;
  private Integer second;
  
  public TwoValueWritable() {
	  set(first, second);
  }
  
  
  public TwoValueWritable(Integer first, Integer second) {
    set(first, second);
  }
  
  public void set(Integer first, Integer second) {
    this.first = first;
    this.second = second;
  }
  
  public Integer getFirst() {
    return first;
  }

  public Integer getSecond() {
    return second;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(first);
    out.writeInt(second);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
	  first = in.readInt(); 
	  second = in.readInt(); 
  }
  
  @Override
  public int hashCode() {
    return first.hashCode() * 163 + second.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof TwoValueWritable) {
      TwoValueWritable tp = (TwoValueWritable) o;
      return first.equals(tp.first) && second.equals(tp.second);
    }
    return false;
  }

  @Override
  public String toString() {
    return first + "\t" + second;
  }
  
  @Override
  public int compareTo(TwoValueWritable tp) {
    int cmp = first.compareTo(tp.first);
    if (cmp != 0) {
      return cmp;
    }
    return second.compareTo(tp.second);
  }
  // ^^ TextPair
  
  // vv TextPairComparator
  public static class Comparator extends WritableComparator {
    
    private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();
    
    public Comparator() {
      super(TwoValueWritable.class);
    }

    @Override
    public int compare(byte[] b1, int s1, int l1,
                       byte[] b2, int s2, int l2) {
      
      try {
        int firstL1 = WritableUtils.decodeVIntSize(b1[s1]) + readVInt(b1, s1);
        int firstL2 = WritableUtils.decodeVIntSize(b2[s2]) + readVInt(b2, s2);
        int cmp = TEXT_COMPARATOR.compare(b1, s1, firstL1, b2, s2, firstL2);
        if (cmp != 0) {
          return cmp;
        }
        return TEXT_COMPARATOR.compare(b1, s1 + firstL1, l1 - firstL1,
                                       b2, s2 + firstL2, l2 - firstL2);
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  static {
    WritableComparator.define(TwoValueWritable.class, new Comparator());
  }
  // ^^ TextPairComparator
  
  // vv TextPairFirstComparator
  public static class FirstComparator extends WritableComparator {
    
    private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();
    
    public FirstComparator() {
      super(TwoValueWritable.class);
    }

    @Override
    public int compare(byte[] b1, int s1, int l1,
                       byte[] b2, int s2, int l2) {
      
      try {
        int firstL1 = WritableUtils.decodeVIntSize(b1[s1]) + readVInt(b1, s1);
        int firstL2 = WritableUtils.decodeVIntSize(b2[s2]) + readVInt(b2, s2);
        return TEXT_COMPARATOR.compare(b1, s1, firstL1, b2, s2, firstL2);
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }
    
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
      if (a instanceof TwoValueWritable && b instanceof TwoValueWritable) {
        return ((TwoValueWritable) a).first.compareTo(((TwoValueWritable) b).first);
      }
      return super.compare(a, b);
    }
  }
  // ^^ TextPairFirstComparator
  
// vv TextPair
}

