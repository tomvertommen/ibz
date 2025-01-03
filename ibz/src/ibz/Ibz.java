package ibz;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ibz.Config.Time;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class Ibz implements Runnable {
	
	private static Config config = Config.getInstance();
	
	private static Logger logger = Logger.getLogger(Ibz.class.getName());
	
	private boolean end = false;
	private List<String[]> previousResults;
	private boolean firstRun = true;
	
	private Calendar requestDate = Calendar.getInstance();
	private Calendar[] estimationWindow = new Calendar[] {Calendar.getInstance(), Calendar.getInstance()};
	private Calendar deadlineDate = Calendar.getInstance();
	
	private Time[] times = config.getTimes().toArray(new Time[0]);
	
	private JAXBContext context;
	private Marshaller marshaller;
	private Unmarshaller unmarchaller;
	private boolean initializeDb = false;
	
	private ResultList database;
	
	private boolean report = config.isReport();
	
	{
		try {
			context = JAXBContext.newInstance(ResultList.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			unmarchaller = context.createUnmarshaller();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Ibz() {
		logger.fine("Ibz - start");
		requestDate.set(Calendar.YEAR, config.getRequestDate().getYear());
		requestDate.set(Calendar.MONTH, config.getRequestDate().getMonth());
		requestDate.set(Calendar.DATE, config.getRequestDate().getDate());
		Utils.setToStartOfDay(requestDate);
		deadlineDate.setTimeInMillis(requestDate.getTimeInMillis());
		deadlineDate.add(Calendar.MONTH, config.getDeadlineMonths());
		estimationWindow[0].setTimeInMillis(requestDate.getTimeInMillis());
		estimationWindow[0].add(Calendar.MONTH, config.getEstimationWindow().getStart());
		estimationWindow[1].setTimeInMillis(requestDate.getTimeInMillis());
		estimationWindow[1].add(Calendar.MONTH, config.getEstimationWindow().getEnd());
		Utils.setToNextWeekDay(deadlineDate);
		Utils.setToNextWeekDay(estimationWindow[0]);
		Utils.setToNextWeekDay(estimationWindow[1]);
		logger.fine("ibz - end");
	}
	
	private long getMillisToSleep() {
		logger.fine("getting millis to sleep - start");
		Calendar now = Calendar.getInstance();
		List<Time> times = new ArrayList<>();
		for(Time time : this.times) {
			times.add(time);
		}
		Collections.sort(times, new Comparator<Time>() {
			@Override
			public int compare(Time o1, Time o2) {
				int millis1 = (o1.getHour() * 60 * 60 * 1000);
				millis1 += (o1.getMinute() * 60 * 1000);
				millis1 += (o1.getSecond() * 1000);
				millis1 += (o1.getMillisecond());
				int millis2 = (o2.getHour() * 60 * 60 * 1000);
				millis2 += (o2.getMinute() * 60 * 1000);
				millis2 += (o2.getSecond() * 1000);
				millis2 += (o2.getMillisecond());
				if(millis1 == millis2)
					return 0;
				if(millis1 > millis2)
					return 1;
				return -1;
			}
		});
		for(int i = 0; i < 2; i++) {
			for(Time time : times) {
				Calendar possibility = Calendar.getInstance();
				possibility.setTimeInMillis(now.getTimeInMillis());
				possibility.add(Calendar.DATE, i);
				possibility.set(Calendar.MILLISECOND, time.getMillisecond());
				possibility.set(Calendar.SECOND, time.getSecond());
				possibility.set(Calendar.MINUTE, time.getMinute());
				possibility.set(Calendar.HOUR_OF_DAY, time.getHour());
				long millis = Utils.getMillisTill(now, possibility);
				if(millis > 0) {
					report = time.isReport();
					return millis;
				}
			}
		}
		throw new RuntimeException("couldn't determine millis to sleep");
	}
	
	private void save() {
		if(database != null && database.getResults() != null)
			Collections.sort(database.getResults(), new Comparator<Result>() {
				@Override
				public int compare(Result o1, Result o2) {
					return o1.number - o2.number;
				}
			});
		File file = new File(config.getDbPath());
		try {
			marshaller.marshal(database, file);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void load() {
		File file = new File(config.getDbPath());
		try {
			database = (ResultList)unmarchaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private int getDaysLeftTillDeadline() {
		return Utils.getDaysFromTill(Calendar.getInstance(), deadlineDate);
	}
	
	private int[] getDaysLeftTillEstimationWindow() {
		Calendar now = Calendar.getInstance();
		return new int[] {Utils.getDaysFromTill(now, estimationWindow[0]), Utils.getDaysFromTill(now, estimationWindow[1])};
	}
	
	private String renderExtraInfo() {
		logger.fine("renderExtraInfo - start");
		String s = "";
		s += "<table style=\"margin-top: 5px; margin-bottom: 5px; border: 1px solid black; border-collapse: collapse;\">"
				+ "<tr style=\"background-color: #d9d9d9;\"><th colspan=\"2\" style=\"border-bottom: 1px solid black; font-size: 16px;\">Extra info</th></tr>";
		int i = 0;
		int[] daysLeftTillEstimationWindow = getDaysLeftTillEstimationWindow();
		for(String[] ss : new String[][] {
				{"Days left till estimation window", "[ " + daysLeftTillEstimationWindow[0] + " , " + daysLeftTillEstimationWindow[1] + " ]"},
				{"Days left till deadline", String.valueOf(getDaysLeftTillDeadline())},
				{
					"Estimation window", 
					"[ " + DateFormats.SDF_dd_MM_yyyy.format(estimationWindow[0].getTime()) + " , " + DateFormats.SDF_dd_MM_yyyy.format(estimationWindow[1].getTime()) + " ]"},
				{"Deadline", DateFormats.SDF_dd_MM_yyyy.format(deadlineDate.getTime())}
		}) {
			String color = i % 2 == 0 ? "#e6e6e6" : "#f2f2f2";
			String header = ss[0];
			String value = ss[1];
			s += "<tr style=\"background-color: " + color + "\"><td>" + header + "</td><td>" + value + "</td></tr>";
			i++;
		}
		s += "</table>";
		logger.fine(s);
		logger.fine("renderExtraInfo - end");
		return s;
	}
	
	private final static class SEND_TYPE {
		public static final String COULDNT_RETRIEVE = "COULDNT_RETRIEVE";
		public static final String NEW_RESULTS = "NEW_RESULTS";
		public static final String SAME_RESULTS = "SAME_RESULTS";
	}
	
	private void report() {
		Calendar now = Calendar.getInstance();
		
		load();
		
		Result ourResult = Utils.getResult(config.getVisaNumber());
		
		Map<Integer, Result> databaseAsMap = Utils.toMap(database);
		
		ResultList newResultList = new ResultList();
		newResultList.setLatestUpdate(now);
		newResultList.setResults(new ArrayList<>());
		int count = 0;
		for(Map.Entry<Integer, Result> entry : databaseAsMap.entrySet()) {
			count++;
			int number = entry.getKey();
			Result dbResult = entry.getValue();
			Result result = Utils.getResult(number);
			if(result == null) {
				result = dbResult.clone();
				result.setStatus(Result.STATUS.DELETED);
			}
			newResultList.add(result);
			if(count % 10 == 0)
				logger.info(count  + " / " + databaseAsMap.entrySet().size() + " | " + Utils.getPercentage(count, databaseAsMap.entrySet().size()) + " %");
		}
		addNewResults(newResultList);
		Map<Integer, Result> newResultsAsMap = Utils.toMap(newResultList);

		boolean ourStatusChanged = false;
		List<Bundle> updates = new ArrayList<>();
		for(Result dbResult : database.getResults()) {
			Result newResult = newResultsAsMap.get(dbResult.getNumber());
			if(!dbResult.getStatus().equals(newResult.getStatus())) {
				updates.add(new Bundle(dbResult, newResult));
				if(dbResult.getNumber() == config.getVisaNumber())
					ourStatusChanged = true;
			}
		}
		
		String subject = "Visa - updates";
		String content = "<html>";
		content += "<head></head>";
		content += "<body>";
		content += "<span style=\"font-weight: bold; font-size: 16px;\">";
		if(ourStatusChanged)
			content += "!!! Our application status changed !!! - ";
		content += "Updates - " + database.getLatestUpdateFmd() + " -> " + newResultList.getLatestUpdateFmd();
		content += "</span>";
		content += "</br>";
		content += "</br>";
		
		int nrOfUpdates = updates.size();
		int nrOfAgreed = 0;
		int nrOfRefused = 0;
		int nrOfOther = 0;
		int previousNrInProgressWithSmallerNumber = database.getNrInProgressWithSmallerNumber(config.getVisaNumber());
		int currentNrInProgressWithSmallerNumber = newResultList.getNrInProgressWithSmallerNumber(config.getVisaNumber());
		Calendar earliestRequestDate = null;
		Calendar latestRequestDate = null;
		Calendar earliestRegistrationDate = null;
		Calendar latestRegistrationDate = null;
		
		TreeMap<String, Integer> occPerData = new TreeMap<String, Integer>();
		for(String dateOption : Utils.DATE_OPTIONS) {
			occPerData.put(dateOption, 0);
		}
		
		for(Bundle bundle : updates) {
			Result currentResult = bundle.getNewResult();
			earliestRequestDate = getEarliest(earliestRequestDate, currentResult.getRequestDate());
			latestRequestDate = getLatest(latestRequestDate, currentResult.getRequestDate());
			earliestRegistrationDate = getEarliest(earliestRegistrationDate , currentResult.getRegistrationDate());
			latestRegistrationDate = getLatest(latestRegistrationDate, currentResult.getRegistrationDate());
			if(Result.STATUS.AKKOORD.equals(currentResult.getStatus()))
				nrOfAgreed++;
			else if(Result.STATUS.REFUSED.equals(currentResult.getStatus()))
				nrOfRefused++;
			else
				nrOfOther++;
			
			Calendar ourRequestDate = ourResult.getRequestDate();
			Calendar ourRegistrationDate = ourResult.getRegistrationDate();
			Calendar resultRequestDate = currentResult.getRequestDate();
			Calendar resultRegistrationDate = currentResult.getRegistrationDate();
			String key = null;
			if(resultRequestDate == null) {
				if(resultRegistrationDate == null) {
					key = Utils.DATE_OPTION.NO_REQ_DATE_NO_REG_DATE;
				} else if(resultRegistrationDate.before(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.NO_REQ_DATE_EARLIER_REG_DATE;
				} else if(resultRegistrationDate.compareTo(ourRegistrationDate) == 0) {
					key = Utils.DATE_OPTION.NO_REQ_DATE_EQUAL_REG_DATE;
				} else if(resultRegistrationDate.after(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.NO_REQ_DATE_LATER_REG_DATE;
				}
			} else if(resultRequestDate.before(ourRequestDate)) {
				if(resultRegistrationDate == null) {
					key = Utils.DATE_OPTION.EARLIER_REQ_DATE_NO_REG_DATE;
				} else if(resultRegistrationDate.before(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.EARLIER_REQ_DATE_EARLIER_REG_DATE;
				} else if(resultRegistrationDate.compareTo(ourRegistrationDate) == 0) {
					key = Utils.DATE_OPTION.EARLIER_REQ_DATE_EQUAL_REG_DATE;
				} else if(resultRegistrationDate.after(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.EARLIER_REQ_DATE_LATER_REG_DATE;
				}
			} else if(resultRequestDate.compareTo(ourRequestDate) == 0) {
				if(resultRegistrationDate == null) {
					key = Utils.DATE_OPTION.EQUAL_REQ_DATE_NO_REG_DATE;
				} else if(resultRegistrationDate.before(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.EQUAL_REQ_DATE_EARLIER_REG_DATE;
				} else if(resultRegistrationDate.compareTo(ourRegistrationDate) == 0) {
					key = Utils.DATE_OPTION.EQUAL_REQ_DATE_EQUAL_REG_DATE;
				} else if(resultRegistrationDate.after(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.EQUAL_REQ_DATE_LATER_REG_DATE;
				}
			} else if(resultRequestDate.after(ourRequestDate)) {
				if(resultRegistrationDate == null) {
					key = Utils.DATE_OPTION.LATER_REQ_DATE_NO_REG_DATE;
				} else if(resultRegistrationDate.before(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.LATER_REQ_DATE_EARLIER_REG_DATE;
				} else if(resultRegistrationDate.compareTo(ourRegistrationDate) == 0) {
					key = Utils.DATE_OPTION.LATER_REQ_DATE_EQUAL_REG_DATE;
				} else if(resultRegistrationDate.after(ourRegistrationDate)) {
					key = Utils.DATE_OPTION.LATER_REQ_DATE_LATER_REG_DATE;
				}
			}
			occPerData.put(key, occPerData.get(key) + 1);
		}
		
		TableData td = new TableData();
		td.addRow(new String[] {"# In progress with smaller number (previous -> current)", previousNrInProgressWithSmallerNumber + " -> " + currentNrInProgressWithSmallerNumber});
		td.addRow(new String[] {"# Updates", String.valueOf(nrOfUpdates)});
		td.addRow(new String[] {"# AKKOORD", format(nrOfAgreed, Utils.getPercentage(nrOfAgreed, nrOfUpdates))});
		td.addRow(new String[] {"# WEIGERING", format(nrOfRefused, Utils.getPercentage(nrOfRefused, nrOfUpdates))});
		td.addRow(new String[] {"# OTHER", format(nrOfOther, Utils.getPercentage(nrOfOther, nrOfUpdates))});
		td.addRow(new String[] {"Request date window", formatDateWindow(earliestRequestDate, latestRequestDate)});
		td.addRow(new String[] {"Registration date window", formatDateWindow(earliestRegistrationDate, latestRegistrationDate)});
		content += Utils.render(td);
		
		td = new TableData();
		for(Map.Entry<String, Integer> entry : occPerData.entrySet()) {
			if(entry.getValue() != 0)
				td.addRow(new String[] {
						entry.getKey().toString(),
						entry.getValue().toString()
				});
		}
		content += Utils.render(td);
		
		for(Bundle bundle : updates) {
			Result previousResult = bundle.getPreviousResult();
			Result currentResult = bundle.getNewResult();
			TableData tableDate = new TableData();
			tableDate.addHeaders(new String[] {"", "Previous", "Current"});
			tableDate.addRow(new String[] {"Request date", previousResult.getRequestDateFmd(), currentResult.getRequestDateFmd()});
			tableDate.addRow(new String[] {"Registration date", previousResult.getRegistrationDateFmd(), currentResult.getRegistrationDateFmd()});
			tableDate.addRow(new String[] {"Decision date", previousResult.getDecisionDateFmd(), currentResult.getDecisionDateFmd()});
			tableDate.addRow(new String[] {"Status", previousResult.getStatus(), currentResult.getStatus()});
			tableDate.addRow(new String[] {"Extra info 1", previousResult.getExtraInfo1(), currentResult.getExtraInfo1()});
			tableDate.addRow(new String[] {"Extra info 2", previousResult.getExtraInfo2(), currentResult.getExtraInfo2()});
			tableDate.addRow(new String[] {"Number", String.valueOf(previousResult.getNumber()), String.valueOf(currentResult.getNumber())});
			tableDate.addRow(new String[] {"Reference", previousResult.getReference(), currentResult.getReference()});
			tableDate.addRow(new String[] {"Post", previousResult.getPost(), currentResult.getPost()});
			content += Utils.render(tableDate);
		}
		content += "</body>";
		content += "</html>";
		
		database = newResultList;
		save();
		
		Utils.send(subject, content);
	}
	
	private Calendar getEarliest(Calendar c1, Calendar c2) {
		if(c1 == null)
			return c2;
		if(c2 == null)
			return c1;
		if(c1.before(c2))
			return c1;
		return c2;
	}
	
	private Calendar getLatest(Calendar c1, Calendar c2) {
		if(c1 == null)
			return c2;
		if(c2 == null)
			return c1;
		if(c1.after(c2))
			return c1;
		return c2;
	}
	
	private String format(int nr, String percentage) {
		if(percentage.equals("NAN"))
			return nr + "";
		else
			return nr + " ( " + percentage + " % )";
	}
	
	private String formatDateWindow(Calendar earliest, Calendar latest) {
		if(earliest == null)
			return "";
		return "[ " + DateFormats.SDF_MMM_d_yyyy.format(earliest.getTime()) + " - " + DateFormats.SDF_MMM_d_yyyy.format(latest.getTime()) + " ]";
	}
	
	private void addNewResults(ResultList resultList) {
		if(resultList == null)
			return;
		if(resultList.getResults() == null)
			resultList.setResults(new ArrayList<>());
		int heighestVisaNumber = 0;
		for(Result result : resultList.getResults()) {
			if(heighestVisaNumber < result.getNumber())
				heighestVisaNumber = result.getNumber();
		}
		int nrOfNotFound = 0;
		int checked = 0;
		for(int i = heighestVisaNumber + 1; i < heighestVisaNumber + 30000; i++) {
			List<String[]> values = Utils.getResults(i);
			checked++;
			if(values == null)
				nrOfNotFound++;
			else
				resultList.getResults().add(new Result(values, i));
			if(checked == 100) {
				if(nrOfNotFound == 100) {
					logger.info("breaking on " + i);
					break;
				} else {
					checked = 0;
					nrOfNotFound = 0;
				}
			}
			if(i % 10 == 0)
				logger.info(i + " - adding new results - checked: " + checked + " | nrOfNotFound: " + nrOfNotFound);
		}
	}

	@Override
	public void run() {
		logger.fine("start running");
		while (!end) {
			logger.info("start cycle");
			if(initializeDb)
				initializeDb();
			else {
				if(this.report)
					report();
				
				List<String[]> results = null;
				results = Utils.getResults(config.getVisaNumber());
				if(results == null) {
					process(SEND_TYPE.COULDNT_RETRIEVE, results);
				} else {
					if(!firstRun && !Utils.equals(results, previousResults)) {
						process(SEND_TYPE.NEW_RESULTS, results);
					} else {
						process(SEND_TYPE.SAME_RESULTS, results);
					}
					previousResults = results;
				}
				firstRun = false;
				try {
					long sleepInMillis = getMillisToSleep();
					logger.info("going to sleep for " + (int) ((sleepInMillis / (1000*60*60)) % 24) + " hours");
					logger.info("and " + sleepInMillis % 1000 + " milliseconds");
					logger.info("and " + (int) (sleepInMillis / (1000) % 60) + " seconds");
					logger.info("and " + (int) ((sleepInMillis / (1000*60)) % 60) + " minutes");
					logger.info("report: " + report);
					Thread.sleep(sleepInMillis);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "InterruptedException while trying to sleep", e);
					throw new RuntimeException(e);
				}
			}
			logger.info("end cycle");
		}
		logger.fine("run - end");
	}
	
	private String render(List<String[]> results, String title) {
		logger.fine("render - start");
		String s = "";
		s += "<table style=\"margin-top: 5px; margin-bottom: 5px; border: 1px solid black; border-collapse: collapse;\"><tr style=\"background-color: #d9d9d9;\"><th colspan=\"2\" style=\"border-bottom: 1px solid black; font-size: 16px;\">" + title + "</th></tr>";
		int i = 0;
		for(String[] ss : results) {
			String color = i % 2 == 0 ? "#e6e6e6" : "#f2f2f2";
			String header = ss[0];
			String value = ss[1];
			if(header.equals("Beslissing/Status Dossier") || header.equals("Datum beslissing/Status Dossier")) {
				s += "<tr style=\"background-color: " + color + "\"><td>" + header + "</td><td style=\"font-weight: bold; font-size: 16px;\">" + value + "</td></tr>";
			} else
				s += "<tr style=\"background-color: " + color + "\"><td>" + header + "</td><td>" + value + "</td></tr>";
			i++;
		}
		s += "</table>";
		logger.fine(s);
		logger.fine("render - end");
		return s;
	}
	
	private void process(String sendType, List<String[]> results) {
		logger.fine("process - start");
		logger.fine("sendType: " + sendType);
		String subject = null;
		String content = null;
		if(SEND_TYPE.COULDNT_RETRIEVE.equals(sendType)) {
			subject = "Visa - couldn't retrieve info";
			content = "<html>";
			content += "<head></head>";
			content += "<body>";
			content += "<span style=\"font-weight: bold; font-size: 16px;\">Couldn't retrieve info.</span>";
			content += "</br>";
			content += "</br>";
			content += renderExtraInfo();
			content += "</body>";
			content += "</html>";
		}
		if(SEND_TYPE.NEW_RESULTS.equals(sendType)) {
			subject = "!!! Visa - found new info !!!";
			content = "<html>";
			content += "<head></head>";
			content += "<body>";
			content += "<span style=\"font-weight: bold; font-size: 16px;\">!!! Found new info !!!</span>";
			content += "</br>";
			content += "</br>";
			content += render(results, "latest info");
			content += "</br>";
			content += "</br>";
			content += render(previousResults, "previous info");
			content += "</br>";
			content += "</br>";
			content += renderExtraInfo();
			content += "</body>";
			content += "</html>";
		}
		if(SEND_TYPE.SAME_RESULTS.equals(sendType)) {
			subject = "Visa - found same info";
			content = "<html>";
			content += "<head></head>";
			content += "<body>";
			content += "<span style=\"font-weight: bold; font-size: 16px;\">Found same info.</span>";
			content += "</br>";
			content += "</br>";
			content += render(results, "latest info");
			content += "</br>";
			content += "</br>";
			content += renderExtraInfo();
			content += "</body>";
			content += "</html>";
		}
		Utils.send(subject, content);
		logger.fine("process - end");
	}
	
	public void end() {
		logger.fine("end start");
		this.end = true;
		logger.fine("end - end | end: " + true);
	}
	
	private void initializeDb() {
		database = new ResultList();
		database.setResults(new ArrayList<>());
		database.setLatestUpdate(Calendar.getInstance());
		Calendar limit = Calendar.getInstance();
		limit.add(Calendar.MONTH, -15);
		
		Calendar latestRequestDate = null;
		for(int i = config.getVisaNumber(); i > 100000; i--) {
			List<String[]> values = Utils.getResults(i);
			if(values != null) {
				Result result = new Result(values, i);
				database.getResults().add(result);
				latestRequestDate = result.getRequestDate();
				if(result.getRequestDate() != null && limit.after(result.getRequestDate())) {
					logger.info("breaking on " + i);
					break;
				}
			}
			if(i % 10 == 0)
				logger.info(i + " - adding own and previous requests - " + (latestRequestDate == null ? null : DateFormats.SDF_MMM_d_yyyy.format(latestRequestDate.getTime())));
		}
		
		int nrOfNotFound = 0;
		int checked = 0;
		for(int i = 131056; i < 160000; i++) {
			List<String[]> values = Utils.getResults(i);
			checked++;
			if(values == null)
				nrOfNotFound++;
			else
				database.getResults().add(new Result(values, i));
			if(checked == 100) {
				if(nrOfNotFound == 100) {
					logger.info("breaking on " + i);
					break;
				} else {
					checked = 0;
					nrOfNotFound = 0;
				}
			}
			if(i % 10 == 0)
				logger.info(i + " - second loop - checked: " + checked + " | nrOfNotFound: " + nrOfNotFound);
		}
		
		Collections.sort(database.getResults(), new Comparator<Result>() {
			@Override
			public int compare(Result o1, Result o2) {
				return o1.number - o2.number;
			}
		});
		save();
		
		this.end = true;
	}

}
