package aos;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Keeping track of days when market is closed or closes earlier. Configuration with days
 * and closing hours is stored in json file.
 */
public class MarketCalendar {
	
	public static final int OPEN = 0;
	public static final int EARLY_CLOSE = 1;
	public static final int CLOSED = 2;
	
	private HashMap<LocalDate, MarketDayData> marketCalendar;
	
	private class MarketDayData {
		
		public String name;
		public LocalDate date;
		public LocalTime earlyClose;
		
		public String toString() {
			String out = "Date: " + date.toString() + ", Name: " + name;
			if (earlyClose != null) out = out + ", Early Close: " + earlyClose.toString();  
			return out;
		}
	}
	
	/**
	 * Initializes market calendar from provided json file
	 * @param configFile
	 * @throws IOException
	 */
	public MarketCalendar(String configFile) throws IOException {
		
		//create gson parser instance and define registerTypeAdapters for parsing LocalDate and LocalTime
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(
				LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) ->
				LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE));
		gsonBuilder.registerTypeAdapter(
				LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, jsonDeserializationContext) ->
				LocalTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_TIME));		
		Gson gson = gsonBuilder.create();
		
		Type marketDayType = 
				new TypeToken<HashMap<LocalDate, MarketDayData>>(){}.getType();
				
		JsonReader jr = new JsonReader(
				new FileReader(configFile));
		
		marketCalendar = gson.fromJson(jr, marketDayType); 
		
		//set date inside MarketDayData from hash key
		Iterator<LocalDate> i = marketCalendar.keySet().iterator();
		while(i.hasNext()) {
			LocalDate date = i.next();
			marketCalendar.get(date).date = date;
			//System.out.println(marketCalendar.get(date));
		}

	}
	
	
	/**
	 * Returns market status for specific day
	 * @param date
	 * @return MarketCalendar.OPEN, MarketCalendar.EARLY_CLOSE, MarketCalendar.CLOSED
	 */
	public int getMarketStatus(LocalDate date) {
		DayOfWeek dow = date.getDayOfWeek();
		
		//check for weekend
		if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY)
			return MarketCalendar.CLOSED;
		
		//check for entry in marketCalendar
		if (marketCalendar.containsKey(date)) {
			MarketDayData mdd = marketCalendar.get(date);
			if (mdd.earlyClose != null) {
				return MarketCalendar.EARLY_CLOSE;
			} else {
				return MarketCalendar.CLOSED;
			}
		}
		
		return MarketCalendar.OPEN;
	}
	
	
	/**
	 * Returns early close for market day or null if there is no entry in market calendar
	 * @param date
	 * @return LocalTime or null
	 */
	public LocalTime getEarlyClose(LocalDate date) {
		if (marketCalendar.containsKey(date)) {
			MarketDayData mdd = marketCalendar.get(date);
			if (mdd.earlyClose != null) {
				return mdd.earlyClose;
			}
		}
		
		return null;
	}

	public static void main(String[] args) throws IOException {
		MarketCalendar mc = new MarketCalendar("conf/market_calendar.json");
		LocalDate d0 = LocalDate.parse("2017-02-05", DateTimeFormatter.ISO_LOCAL_DATE); //Sunday		
		LocalDate d1 = LocalDate.parse("2017-02-09", DateTimeFormatter.ISO_LOCAL_DATE); //working Thursday
		LocalDate d2 = LocalDate.parse("2017-04-14", DateTimeFormatter.ISO_LOCAL_DATE); //Good Friday
		LocalDate d3 = LocalDate.parse("2017-07-03", DateTimeFormatter.ISO_LOCAL_DATE); //Day before Independence day with early close
		System.out.println(d0.toString() + ": " + mc.getMarketStatus(d0));		
		System.out.println(d1.toString() + ": " + mc.getMarketStatus(d1));
		System.out.println(d2.toString() + ": " + mc.getMarketStatus(d2));
		System.out.println(d3.toString() + ": " + mc.getMarketStatus(d3));
		System.out.println(d3.toString() + " Early Close: " + mc.getEarlyClose(d3));
		
	}
	
}
