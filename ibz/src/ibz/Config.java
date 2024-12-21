package ibz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class Config {	
	
	private int visaNumber;
	private boolean initializeDb;
	private boolean report;
	private String dbPath;
	private String place;
	private int deadlineMonths;
	
	private Mail mail = new Mail();
	private Date requestDate = new Date();
	private MonthWindow estimationWindow = new MonthWindow();
	
	private List<Time> times = new ArrayList<Time>();
	
	@Override
	public String toString() {
		String s = "";
		s += "visaNumber: " + visaNumber + "\n";
		s += "initializeDb: " + initializeDb + "\n";
		s += "report: " + report + "\n";
		s += "dbPath: " + dbPath + "\n";
		s += "place: " + place + "\n";
		s += "deadlineMonths: " + deadlineMonths + "\n";
		s += "estimationWindow: " + estimationWindow + "\n";
		s += "requestDate: " + requestDate + "\n";
		s += "mail: " + mail + "\n";
		for(Time time : times) {
			s += time + " \n";
		}
		return s;
	}
	
	public static class Time {
		
		@Override
		public String toString() {
			return "hour: " + hour + " | minute: " + minute + " | second: " + second + " | millisecond: " + millisecond + " | report: " + report;
		}

		public Time() { }

		public Time(int hour, boolean report) {
			this.report = report;
			this.hour = hour;
		}
		
		public Time(int hour, int minutes, boolean report) {
			this(hour, report);
			this.minute = minutes;
		}
		
		private int millisecond;
		private int second;
		private int minute;
		private int hour;
		private boolean report;

		@XmlElement(name = "millisecond")
		public int getMillisecond() {
			return millisecond;
		}
		
		public void setMillisecond(int millisecond) {
			this.millisecond = millisecond;
		}

		@XmlElement(name = "second")
		public int getSecond() {
			return second;
		}
		
		public void setSecond(int second) {
			this.second = second;
		}
		
		@XmlElement(name = "minute")
		public int getMinute() {
			return minute;
		}
		
		public void setMinute(int minute) {
			this.minute = minute;
		}

		@XmlElement(name = "hour")
		public int getHour() {
			return hour;
		}
		
		public void setHour(int hour) {
			this.hour = hour;
		}

		@XmlElement(name = "report")
		public boolean isReport() {
			return report;
		}
		
		public void setReport(boolean report) {
			this.report = report;
		}
		
	}

	public static class MonthWindow {
		
		@Override
		public String toString() {
			return "start: " + start + " | end: " + end;
		}

		private int start;
		private int end;
		
		@XmlElement(name = "start")
		public int getStart() {
			return start;
		}
		
		public void setStart(int start) {
			this.start = start;
		}
		
		@XmlElement(name = "end")
		public int getEnd() {
			return end;
		}
		
		public void setEnd(int end) {
			this.end = end;
		}
		
	}
	
	public static class Date {
		
		@Override
		public String toString() {
			return "date: " + date + " | month: " + month + " | year: " + year;
		}
		
		private int year;
		private int month;
		private int date;
		
		@XmlElement(name = "year")
		public int getYear() {
			return year;
		}
		
		public void setYear(int year) {
			this.year = year;
		}

		@XmlElement(name = "month")
		public int getMonth() {
			return month;
		}
		
		public void setMonth(int month) {
			this.month = month;
		}

		@XmlElement(name = "date")
		public int getDate() {
			return date;
		}
		
		public void setDate(int date) {
			this.date = date;
		}
		
	}
	
	public static class Mail {
		
		@Override
		public String toString() {
			return "username: " + username + " | password: " + password + " | recipient: " + recipient + " | sender: " + sender + 
					" | mail_smtp_auth: " + mail_smtp_auth + " | mail_smtp_starttls_enable: " + mail_smtp_starttls_enable + 
					" | mail_smtp_host: " + mail_smtp_host + " | mail_smtp_port: " + mail_smtp_port;
		}
		
		private String username;
		private String password;
		private String recipient;
		private String sender;
		private boolean mail_smtp_auth;
		private String mail_smtp_starttls_enable;
		private String mail_smtp_host;
		private String mail_smtp_port;

		@XmlElement(name = "mail_smtp_auth")
		public boolean isMail_smtp_auth() {
			return mail_smtp_auth;
		}

		public void setMail_smtp_auth(boolean mail_smtp_auth) {
			this.mail_smtp_auth = mail_smtp_auth;
		}

		@XmlElement(name = "mail_smtp_starttls_enable")
		public String getMail_smtp_starttls_enable() {
			return mail_smtp_starttls_enable;
		}

		public void setMail_smtp_starttls_enable(String mail_smtp_starttls_enable) {
			this.mail_smtp_starttls_enable = mail_smtp_starttls_enable;
		}

		@XmlElement(name = "mail_smtp_host")
		public String getMail_smtp_host() {
			return mail_smtp_host;
		}

		public void setMail_smtp_host(String mail_smtp_host) {
			this.mail_smtp_host = mail_smtp_host;
		}

		@XmlElement(name = "mail_smtp_port")
		public String getMail_smtp_port() {
			return mail_smtp_port;
		}

		public void setMail_smtp_port(String mail_smtp_port) {
			this.mail_smtp_port = mail_smtp_port;
		}

		@XmlElement(name = "password")
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@XmlElement(name = "recipient")
		public String getRecipient() {
			return recipient;
		}

		public void setRecipient(String recipient) {
			this.recipient = recipient;
		}

		@XmlElement(name = "username")
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		@XmlElement(name = "sender")
		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}
		
	}
	
	@XmlElementWrapper(name = "times")
	@XmlElement(name = "time")
	public List<Time> getTimes() {
		return times;
	}

	public void setTimes(List<Time> times) {
		this.times = times;
	}

	@XmlElement(name = "estimation_window")
	public MonthWindow getEstimationWindow() {
		return estimationWindow;
	}

	public void setEstimationWindow(MonthWindow estimationWindow) {
		this.estimationWindow = estimationWindow;
	}

	@XmlElement(name = "deadline_months")
	public int getDeadlineMonths() {
		return deadlineMonths;
	}

	public void setDeadlineMonths(int deadlineMonths) {
		this.deadlineMonths = deadlineMonths;
	}

	@XmlElement(name = "request_date")
	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	
	@XmlElement(name = "db_path")
	public String getDbPath() {
		return dbPath;
	}
	
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}
	
	@XmlElement(name = "report")
	public boolean isReport() {
		return report;
	}
	
	public void setReport(boolean report) {
		this.report = report;
	}
	
	@XmlElement(name = "visa_number")
	public int getVisaNumber() {
		return visaNumber;
	}
	
	public void setVisaNumber(int visaNumber) {
		this.visaNumber = visaNumber;
	}
	
	@XmlElement(name = "initialize_db")
	public boolean isInitializeDb() {
		return initializeDb;
	}

	@XmlElement(name = "mail")
	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}
	
	@XmlElement(name = "place")
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	
	public void setInitializeDb(boolean initializeDb) {
		this.initializeDb = initializeDb;
	}

	public static void main(String[] args) {
		//Config.initialize("D:/temp/config.xml");
		//Config.loadAndPrint("D:/temp/config.xml");
	}

	private static Config instance;
	private static JAXBContext context;
	private static Marshaller marshaller;
	private static Unmarshaller unmarchaller;
	private static String path = "./config/config.xml";
	
	static {
		try {
			context = JAXBContext.newInstance(Config.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			unmarchaller = context.createUnmarshaller();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Config() { }
	
	public static Config getInstance() {
		if(instance == null)
			load();
		return instance;
	}
	
	private static void load() {
		File file = new File(path);
		try {
			instance = (Config)unmarchaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private static void initialize(String path) {
		File file = new File(path);
		try {
			marshaller.marshal(new Config(), file);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private static void loadAndPrint(String path) {
		File file = new File(path);
		try {
			System.out.println((Config)unmarchaller.unmarshal(file));
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
}
