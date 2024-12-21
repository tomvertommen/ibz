package ibz;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = {"requestDate", "registrationDate", "decisionDate", "status", "extraInfo1", "extraInfo2", "number", "reference", "post"})
@XmlRootElement(name = "result")
public class Result {
	
	public static class STATUS {
		static final String IN_TREATMENT = "In behandeling";
		static final String REFUSED = "WEIGERING";
		static final String AKKOORD = "AKKOORD";
		static final String AWAITING_DOCUMENTS = "In afwachting ontvangst van documenten die de visumaanvraag ondersteunen en die door de Belgische diplomatieke/consulaire post per diplomatieke valies verzonden wordt.";
		static final String AKKOORD_NA_VOORLEGGING_EXTRA_DOCS = "Toekenning visum op voorlegging bijkomende stukken.";
		static final String DELETED = "Deleted";
		static final String CASA_VTL = "CASA: VTL";
	}
	
	public Result() {}
	
	public Result(List<String[]> values, int number) {
		this.number = number;
		for(String[] ss : values) {
			map.put(ss[0], ss[1]);
			if(ss[0].equals("extra info1"))
				this.extraInfo1 = ss[1];
			if(ss[0].equals("Diplomatic Post"))
				this.post = ss[1];
			if(ss[0].equals("Beslissing/Status Dossier"))
				this.status = ss[1];
			if(ss[0].equals("extra info2"))
				this.extraInfo2 = ss[1];
			if(ss[0].equals("ReferenceNummer"))
				this.reference = ss[1];
			if(ss[0].equals("Datum beslissing/Status Dossier"))
				this.decisionDate = toCalendar(ss[1]);
			if(ss[0].equals("Datum registratie visumaanvraag door Dienst Vreemdelingenzaken"))
				this.registrationDate = toCalendar(ss[1]);
			if(ss[0].equals("Datum visumaanvraag"))
				this.requestDate = toCalendar(ss[1]);
		}
	}
	
	Map<String, String> map = new HashMap<>();
	
	String post = "";
	int number;
	String status = "";
	String extraInfo1 = "";
	String extraInfo2 = "";
	String reference = "";
	Calendar decisionDate;
	Calendar registrationDate;
	Calendar requestDate;
	
	public boolean isInProgress() {
		return Utils.isInProgress(this.status);
	}
	
	public Result clone() {
		Result result = new Result();
		result.post = this.post;
		result.number = this.number;
		result.status = this.status;
		result.extraInfo1 = this.extraInfo1;
		result.extraInfo2 = this.extraInfo2;
		result.reference = this.reference;
		result.decisionDate = this.decisionDate;
		result.registrationDate = this.registrationDate;
		result.requestDate = this.requestDate;
		result.map = new HashMap<>();
		for(Map.Entry<String, String> entry : this.map.entrySet()) {
			result.map.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	String get(String key) {
		return map.get(key);
	}
	
	Calendar toCalendar(String value) {
		if(Utils.isNullOrEmpty(value))
			return null;
		Date d;
		try {
			d = DateFormats.SDF_MMM_d_yyyy.parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());
		return c;
	}
	
	Calendar getCalendar(String key) {
		return toCalendar(get(key));
	}
	
	@XmlElement(name = "extra_info_1")
	public String getExtraInfo1() {
		return extraInfo1;
	}

	public void setExtraInfo1(String extraInfo1) {
		this.extraInfo1 = extraInfo1;
	}

	@XmlElement(name = "extra_info_2")
	public String getExtraInfo2() {
		return extraInfo2;
	}

	public void setExtraInfo2(String extraInfo2) {
		this.extraInfo2 = extraInfo2;
	}

	@XmlElement(name = "reference")
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@XmlJavaTypeAdapter(CalendarAdapter_MMM_d_yyyy.class)
	@XmlElement(name = "decision_date")
	public Calendar getDecisionDate() {
		return decisionDate;
	}
	
	public String getDecisionDateFmd() {
		return Utils.format(DateFormats.SDF_MMM_d_yyyy, decisionDate);
	}

	public void setDecisionDate(Calendar decisionDate) {
		this.decisionDate = decisionDate;
	}

	@XmlJavaTypeAdapter(CalendarAdapter_MMM_d_yyyy.class)
	@XmlElement(name = "registration_date")
	public Calendar getRegistrationDate() {
		return registrationDate;
	}
	
	public String getRegistrationDateFmd() {
		return Utils.format(DateFormats.SDF_MMM_d_yyyy, registrationDate);
	}

	public void setRegistrationDate(Calendar registrationDate) {
		this.registrationDate = registrationDate;
	}

	@XmlJavaTypeAdapter(CalendarAdapter_MMM_d_yyyy.class)
	@XmlElement(name = "request_date")
	public Calendar getRequestDate() {
		return requestDate;
	}
	
	public String getRequestDateFmd() {
		return Utils.format(DateFormats.SDF_MMM_d_yyyy, requestDate);
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}
	
	@XmlElement(name = "post")
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	
	@XmlElement(name = "number")
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String toString() {
		String s = "";
		for(Map.Entry<String, String> entry : map.entrySet()) {
			s += entry.getKey() + ": " + entry.getValue() + "\n";
		}
		return s;
	}
}
