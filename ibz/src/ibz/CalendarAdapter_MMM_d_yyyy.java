package ibz;

import java.util.Calendar;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class CalendarAdapter_MMM_d_yyyy extends XmlAdapter<String, Calendar> {
	
	@Override
	public String marshal(Calendar c) throws Exception {
		if(c == null)
			return "";
		return DateFormats.SDF_MMM_d_yyyy.format(c.getTime());
	}

	@Override
	public Calendar unmarshal(String s) throws Exception {
		if(s == null || s.trim().equals(""))
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(DateFormats.SDF_MMM_d_yyyy.parse(s));
		return c;
	}
	
}
