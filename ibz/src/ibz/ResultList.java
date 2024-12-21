package ibz;

import java.util.Calendar;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(namespace = "be.tomvertommen")
public class ResultList {
	
	private List<Result> results;
	private Calendar latestUpdate;

	@XmlElement(name = "result")
	@XmlElementWrapper(name = "resultList")
	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	@XmlJavaTypeAdapter(CalendarAdapter_EEE__d_MMM_yyyy_h_mm_a.class)
	@XmlElement(name = "latest_update")
	public Calendar getLatestUpdate() {
		return latestUpdate;
	}
	
	public String getLatestUpdateFmd() {
		return Utils.format(DateFormats.SDF_EEE__d_MMM_yyyy_h_mm_a, latestUpdate);
	}

	public void setLatestUpdate(Calendar latestUpdate) {
		this.latestUpdate = latestUpdate;
	}
	
	public void add(Result result) {
		this.results.add(result);
	}
	
	public int getNrInProgressWithSmallerNumber(int number) {
		int nrOfInProgressWithSmallerVisaNumber = 0;
		for(Result result : results) {
			if(result.getNumber() < number && result.isInProgress())
				nrOfInProgressWithSmallerVisaNumber++;
		}
		return nrOfInProgressWithSmallerVisaNumber;
	}
	
}
