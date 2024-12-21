package ibz;

import java.util.Calendar;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class CalendarAdapter_EEE__d_MMM_yyyy_h_mm_a extends XmlAdapter<String, Calendar> {
	
	@Override
	public String marshal(Calendar c) throws Exception {
		if(c == null)
			return "";
		return DateFormats.SDF_EEE__d_MMM_yyyy_h_mm_a.format(c.getTime());
	}

	@Override
	public Calendar unmarshal(String s) throws Exception {
		if(s == null || s.trim().equals(""))
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(DateFormats.SDF_EEE__d_MMM_yyyy_h_mm_a.parse(s));
		return c;
	}

}
