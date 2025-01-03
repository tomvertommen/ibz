package ibz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {
	
	private static Config config = Config.getInstance();
	private static Authenticator authenticator;
	private static Properties mailProps = new Properties();
	private static String username = config.getMail().getUsername();
	
	private static Logger logger = Logger.getLogger(Utils.class.getName());
	
	public static List<String> DATE_OPTIONS;
	
	public static class DATE_OPTION {
		public static final String NO_REQ_DATE_NO_REG_DATE = "no request date and no registration date";
		public static final String NO_REQ_DATE_EARLIER_REG_DATE = "no request date and registration date &lt;";
		public static final String NO_REQ_DATE_EQUAL_REG_DATE = "no request date  and registration date =";
		public static final String NO_REQ_DATE_LATER_REG_DATE = "no request date  and registration date &gt;";
		public static final String EARLIER_REQ_DATE_NO_REG_DATE = "request date &lt; and no registration date ";
		public static final String EARLIER_REQ_DATE_EARLIER_REG_DATE = "request date &lt; and registration date &lt;";
		public static final String EARLIER_REQ_DATE_EQUAL_REG_DATE = "request date &lt; and registration date =";
		public static final String EARLIER_REQ_DATE_LATER_REG_DATE = "request date &lt; and registration date &gt;";
		public static final String EQUAL_REQ_DATE_NO_REG_DATE = "request date = and no registration date ";
		public static final String EQUAL_REQ_DATE_EARLIER_REG_DATE = "request date = and registration date &lt;";
		public static final String EQUAL_REQ_DATE_EQUAL_REG_DATE = "request date = and registration date =";
		public static final String EQUAL_REQ_DATE_LATER_REG_DATE = "request date = and registration date &gt;";
		public static final String LATER_REQ_DATE_NO_REG_DATE = "request date &gt; and no registration date ";
		public static final String LATER_REQ_DATE_EARLIER_REG_DATE = "request date &gt; and registration date &lt;";
		public static final String LATER_REQ_DATE_EQUAL_REG_DATE = "request date &gt; and registration date =";
		public static final String LATER_REQ_DATE_LATER_REG_DATE = "request date &gt; and registration date &gt;";
	}
	
	static {
		DATE_OPTIONS = new ArrayList<String>();
		DATE_OPTIONS.add(DATE_OPTION.NO_REQ_DATE_NO_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.NO_REQ_DATE_EARLIER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.NO_REQ_DATE_EQUAL_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.NO_REQ_DATE_LATER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EARLIER_REQ_DATE_NO_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EARLIER_REQ_DATE_EARLIER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EARLIER_REQ_DATE_EQUAL_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EARLIER_REQ_DATE_LATER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EQUAL_REQ_DATE_NO_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EQUAL_REQ_DATE_EARLIER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EQUAL_REQ_DATE_EQUAL_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.EQUAL_REQ_DATE_LATER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.LATER_REQ_DATE_NO_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.LATER_REQ_DATE_EARLIER_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.LATER_REQ_DATE_EQUAL_REG_DATE);
		DATE_OPTIONS.add(DATE_OPTION.LATER_REQ_DATE_LATER_REG_DATE);
		mailProps.put("mail.smtp.auth", config.getMail().isMail_smtp_auth());
		mailProps.put("mail.smtp.starttls.enable", config.getMail().getMail_smtp_starttls_enable());
		mailProps.put("mail.smtp.host", config.getMail().getMail_smtp_host());
		mailProps.put("mail.smtp.port", config.getMail().getMail_smtp_port());
		authenticator = new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, config.getMail().getPassword());
		    }
		};
	}
	
	public static void setToNextWeekDay(Calendar c) {
		while(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			c.add(Calendar.DATE, 1);
	}
	
	public static void setToStartOfDay(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
	
	public static int getDaysFromTill(Calendar c1, Calendar c2) {
		return (int)((c2.getTimeInMillis() - c1.getTimeInMillis()) / (24 * 60 * 60 * 1000));
	}
	
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().equals("");
	}
	
	public static boolean isNullOrEmpty(Collection<?> c) {
		return c == null || c.size() == 0;
	}
	
	public static boolean equals(List<String[]> results1, List<String[]> results2) {
		logger.fine("equals - start");
		boolean returnValue = true;
		if(results1 == null && results2 == null)
			returnValue = true;
		else if(results1 == null && results2 != null)
			returnValue = false;
		else if(results1 != null && results2 == null)
			returnValue = false;
		else if(results1.size() != results2.size())
			returnValue = false;
		else {
			OUTER:
			for(int i = 0; i < results1.size(); i++) {
				if(!results1.get(i)[0].equals(results2.get(i)[0]) || !results1.get(i)[1].equals(results2.get(i)[1])) {
					returnValue = false;
					break OUTER;
				}
			}
		}
		logger.fine("equals - end | equals: " + returnValue);
		return returnValue;
	}
	
	public static boolean isSameDay(Calendar c1, Calendar c2) {
		if(c1 == null || c2 == null)
			return false;
		if(
				c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
				c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
				c1.get(Calendar.DATE) == c2.get(Calendar.DATE)
				)
			return true;
		return false;
	}
	
	public static boolean isInProgress(String status) {
		return 
				!Result.STATUS.REFUSED.equals(status) && 
				!Result.STATUS.AKKOORD.equals(status) && 
				!Result.STATUS.AKKOORD_NA_VOORLEGGING_EXTRA_DOCS.equals(status) && 
				!Result.STATUS.AWAITING_DOCUMENTS.equals(status) &&
				!Result.STATUS.DELETED.equals(status);
	}
	
	public static long getMillisTill(Calendar c1, Calendar c2) {
		return c2.getTimeInMillis() - c1.getTimeInMillis();
	}
	
	public static String getPercentage(int nr, int total) {
		if(total == 0)
			return "NAN";
		BigDecimal nrBd = new BigDecimal(nr);
		BigDecimal totalBd = new BigDecimal(total);
		BigDecimal percentage = (nrBd.multiply(new BigDecimal(100))).divide(totalBd, 0, RoundingMode.HALF_UP);
		return percentage.toPlainString();
	}
	
	public static List<String[]> getResults(int visumNr) {
		try {
			logger.fine("getting results - start - " + visumNr);
			URL url = new URL("https://infovisa.ibz.be/ResultNl.aspx?place=" + config.getPlace() + "&visumnr=" + visumNr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			con.disconnect();
			Document doc = Jsoup.parse(content.toString());
			
	
			Elements els = doc.getElementsByTag("b");
			for(int i = 0; i < els.size(); i++) {
				if(els.get(i).text().contains("Het feit dat uw zoekopdracht geen resultaten"))
					return null;
			}
			
			els = doc.getElementsByTag("td");
			List<String[]> results = new ArrayList<>();
			for(int i = 0; i < els.size(); i++) {
				Element el = els.get(i);
				results.add(new String[] {el.text().replace(":", ""), els.get(i + 1).text()});
				i++;
			}
			if (results .size() == 0) {
				logger.fine("results .size() == 0");
			} else {
				for(String[] ss : results) {
					logResult(ss);
				}
			}
			logger.fine("getting results - end");
			return results;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Result getResult(int visumNr) {
		List<String[]> results = getResults(visumNr);
		if(results == null)
			return null;
		return new Result(results, visumNr);
	}
	
	private static void logResult(String[] result) {
		String s = "";
		if(result == null)
			s += "null";
		else if(result.length == 0)
			s += "result.length == 0";
		else {
			s += result[0];
			for(int i = 1; i < result.length; i++) {
				s += " - " + result[i];
			}
		}
		logger.fine("result: " + s);
	}
	
	public static void send(String subject, String content) {
		logger.fine("send - start");
		Session session = Session.getInstance(mailProps, authenticator);
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(config.getMail().getSender()));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getMail().getRecipient()));
			message.setSubject(subject);
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(content, "text/html; charset=utf-8");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			message.setContent(multipart);
			Transport.send(message);
		} catch (AddressException e) {
			logger.log(Level.SEVERE, "AddressException while sending", e);
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			logger.log(Level.SEVERE, "MessageException while sending", e);
			throw new RuntimeException(e);
		}
		logger.fine("send - end");
	}
	
	public static SortedMap<Integer, Result> toMap(ResultList resultList) {
		SortedMap<Integer, Result> map = new TreeMap<>();
		for(Result result : resultList.getResults()) {
			map.put(result.getNumber(), result);
		}
		return map;
	}
	
	public static String format(SimpleDateFormat sdf, Calendar c) {
		if(c == null)
			return "";
		return sdf.format(c.getTime());
	}
	
	public static String render(TableData tableDate) {
		String s  = "<table style=\"margin-top: 5px; margin-bottom: 5px; border: 1px solid black; border-collapse: collapse;\">";
		if(!isNullOrEmpty(tableDate.getTitle())) {
			s += "<tr style=\"border-bottom: 1px solid black; background-color: #e6e6e6;\"><td style=\"text-align: center; font-weight: bold; font-size: 18px;\" colspan=\"" + tableDate.getNrOfColumns() + "\">";
			s += tableDate.getTitle();
			s += "</td></tr>";
		}
		if(!isNullOrEmpty(tableDate.getHeaders())) {
			s += "<tr style=\"border: 1px solid black; background-color: #d9d9d9;\">";
			for(String header : tableDate.getHeaders()) {
				s += "<td style=\"border: 1px solid black; font-weight: bold; font-size: 16px;\">";
				s += header;
				s += "</td>";
			}
			s += "</tr>";
		}
		for(int i = 0; i < tableDate.getContent().size(); i++)  {
			List<String> row = tableDate.getContent().get(i);
			String color = i % 2 == 0 ? "#e6e6e6" : "#f2f2f2";
			s += "<tr style=\"background-color: " + color + ";\">";
			for(String cell : row) {
				s += "<td style=\"vertical-align: top;\">";
				s += cell;
				s += "</td>";
			}
			s += "</tr>";
		}
		s += "</table>";
		return s;
	}

}
