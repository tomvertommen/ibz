package ibz;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class DateData implements Comparable<DateData> {
	
	@Override
	public int hashCode() {
		return Objects.hash(regDate, reqDate);
	}

	@Override
	public String toString() {
		return "Req D " + reqDate + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Reg D " + regDate + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; 
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateData other = (DateData) obj;
		return regDate.equals(other.regDate) && reqDate.equals(other.reqDate);
	}
	
	public DateData(String reqDate, String regDate) {
		this.reqDate = reqDate;
		this.regDate = regDate;
	}
	
	public static String getPossibility(Calendar ourDate, Calendar otherDate) {
		if(otherDate == null)
			return POSSIBILITIES.EMPTY;
		if(Utils.isSameDay(ourDate, otherDate))
			return POSSIBILITIES.EQUAL;
		if(otherDate.before(ourDate))
			return POSSIBILITIES.EARLIER;
		return POSSIBILITIES.LATER;
	}

	public static final class POSSIBILITIES {
		public static final String EMPTY = "empty";
		public static final String EARLIER = "&lt;";
		public static final String EQUAL = "=";
		public static final String LATER = "&gt;";
	}
	
	private static List<String> ALL_POSSIBILITIES = new ArrayList<>();
	
	static {
		ALL_POSSIBILITIES.add(POSSIBILITIES.EMPTY);
		ALL_POSSIBILITIES.add(POSSIBILITIES.EARLIER);
		ALL_POSSIBILITIES.add(POSSIBILITIES.EQUAL);
		ALL_POSSIBILITIES.add(POSSIBILITIES.LATER);
	}
	
	private String reqDate;
	private String regDate;
	
	public String getReqDate() {
		return reqDate;
	}
	
	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
	}
	
	public String getRegDate() {
		return regDate;
	}
	
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	
	public static List<DateData> possibilities = new ArrayList<>();
	
	static {
		for(String possiblity : ALL_POSSIBILITIES) {
			for(String possiblity2 : ALL_POSSIBILITIES) {
				possibilities.add(new DateData(possiblity, possiblity2));
			}
		}
	}

	@Override
	public int compareTo(DateData o) {
		int index = 0;
		int thisIndex = 0;
		int toCompareToIndex = 0;
		for(DateData dateData : possibilities) {
			if(this.equals(dateData))
				thisIndex = index;
			if(o.equals(dateData))
				toCompareToIndex = index;
			index++;
		}
		return thisIndex - toCompareToIndex;
	}

}
