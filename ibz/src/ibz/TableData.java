package ibz;

import java.util.ArrayList;
import java.util.List;

public class TableData {
	
	private String title;
	private List<String> headers = new ArrayList<>();
	private List<List<String>> content = new ArrayList<>();
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void addHeader(String header) {
		headers.add(header);
	}
	
	public void addHeaders(String[] headers) {
		for(String header : headers) {
			addHeader(header);
		}
	}
	
	public void addRow(List<String> row) {
		content.add(row);
	}
	
	public void addRow(String[] row) {
		List<String> l = new ArrayList<>();
		for(String r : row) {
			l.add(r);
		}
		addRow(l);
	}
	
	public int getNrOfColumns() {
		int nrOfColums = headers.size();
		for(List<String> row : content) {
			if(row.size() > nrOfColums)
				nrOfColums = row.size();
		}
		return nrOfColums;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<List<String>> getContent() {
		return content;
	}

	public void setContent(List<List<String>> content) {
		this.content = content;
	}

}
