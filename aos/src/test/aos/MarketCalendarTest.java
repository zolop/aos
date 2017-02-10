package aos;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.BeforeClass;
import org.junit.Test;

public class MarketCalendarTest {

	private static MarketCalendar marketCalendar;
	
	@BeforeClass
	public static void setUp() throws Exception {
		marketCalendar = new MarketCalendar("src/test/resources/market_calendar.json");
	}
	
	@Test
	public void dayIsWeekendMaketClosed() {
		LocalDate d = LocalDate.parse("2017-02-05", DateTimeFormatter.ISO_LOCAL_DATE); //Sunday
		int marketStatus = marketCalendar.getMarketStatus(d);
		assertEquals(marketStatus, MarketCalendar.CLOSED);
		assertNull(marketCalendar.getEarlyClose(d));
	}

	@Test
	public void dayIsWorkingMarketIsOpen() {
		LocalDate d = LocalDate.parse("2017-02-09", DateTimeFormatter.ISO_LOCAL_DATE); //Thursday
		int marketStatus = marketCalendar.getMarketStatus(d);
		assertEquals(marketStatus, MarketCalendar.OPEN);	
		assertNull(marketCalendar.getEarlyClose(d));
	}
	
	@Test
	public void dayIsHolidayMarketIsClosed() {
		LocalDate d = LocalDate.parse("2017-04-14", DateTimeFormatter.ISO_LOCAL_DATE); //GoodFriday
		int marketStatus = marketCalendar.getMarketStatus(d);
		assertEquals(marketStatus, MarketCalendar.CLOSED);
		assertNull(marketCalendar.getEarlyClose(d));		
	}
	
	@Test
	public void dayHasEarlyClose() {
		LocalDate d = LocalDate.parse("2017-07-03", DateTimeFormatter.ISO_LOCAL_DATE); //Day before Independence day
		int marketStatus = marketCalendar.getMarketStatus(d);
		assertEquals(marketStatus, MarketCalendar.EARLY_CLOSE);
		assertNotNull(marketCalendar.getEarlyClose(d));
		assertEquals(marketCalendar.getEarlyClose(d), LocalTime.parse("13:00:00"));
	}
}
