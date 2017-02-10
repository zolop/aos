/**
 * 
 */
package aos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.Vector;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.SoftDollarTier;
import com.ib.client.TagValue;

/**
 * @author kudlac
 *
 */
public class Pokus implements EWrapper {
	
	// Keep track of the next Order ID
	private int nextOrderID = 0;
	// The IB API Client Socket object
	private EClientSocket client = null;	
	private EReaderSignal readerSignal = null;
	private EReader reader;
	String[] spSymbols = {"AAPL","ABBV","ABT","ACN","AGN","AIG","ALL","AMGN","AMZN","AXP","BA","BAC","BIIB","BK","BLK","BMY","C","CAT","CELG","CL","CMCSA","COF","COP","COST","CSCO","CVS","CVX","DD","DHR","DIS","DOW","DUK","EMR","EXC","F","FB","FDX","FOXA","GD","GE","GILD","GM","GOOG","GS","HAL","HD","HON","IBM","INTC","JNJ","JPM","KHC","KMI","KO","LLY","LMT","LOW","MA","MCD","MDLZ","MDT","MET","MMM","MO","MON","MRK","MS","MSFT","NEE","NKE","ORCL","OXY","PCLN","PEP","PFE","PG","PM","PYPL","QCOM","RTN","SBUX","SLB","SO","SPG","T","TGT","TWX","TXN","UNH","UNP","UPS","USB","UTX","V","VZ","WBA","WFC","WMT","XOM"};
	//CSCO, INTC, MSFT need more specific exchange than SMART - NASDAQ
	private int tickSnapshotEndCnt = 0;
	
	public Pokus() {
		// Create a new EClientSocket object
		readerSignal = new EJavaSignal();
		client = new EClientSocket(this, readerSignal);
		// Connect to the TWS or IB Gateway application
		// Leave null for localhost
		// Port Number (should match TWS/IB Gateway configuration
		client.eConnect(null, 4001, 0);
		// Pause here for connection to complete
		try {
			while (!(client.isConnected()))
				;
			// Can also try: while (client.NextOrderId <= 0);
		} catch (Exception e) {
		}

		reader = new EReader(client, readerSignal);
		reader.start();
        new Thread() {
            public void run() {
                while (client.isConnected()) {
                    	readerSignal.waitForSignal();
                    try {
                        reader.processMsgs();
                    } catch (Exception e) {
                        System.out.println("Exception: "+e.getMessage());
                    }
                }
            }
        }.start();		
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss z");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,  -1);
        String yesterdaysDate = dateFormat.format(cal.getTime());  
        
        System.out.println("Symbols cnt: " + spSymbols.length);
        //for (int i=0; i<spSymbols.length; i++) {
        for (int i=0; i<2; i++) {        
        	Contract c = new Contract();
        	c.symbol(spSymbols[i]);
    		c.exchange("SMART");
    		c.secType("STK");
    		c.currency("USD");
    		client.reqMktData(i, c, null, true, null);
    		//client.reqHistoricalData(i, c, yesterdaysDate, "1 D", "1 day", "TRADES", 0, 1, null);
        }
        
/*		// Create a new contract
		Contract contract = new Contract();
		contract.symbol("EUR");
		contract.exchange("IDEALPRO");
		contract.secType("CASH");
		contract.currency("GBP");
		// Create a TagValue list
		Vector<TagValue> mktDataOptions = new Vector<TagValue>();
		// Make a call to reqMktData to start off data retrieval with
		// parameters:
		// ConID - Connection Identifier.
		// Contract - The financial instrument we are requesting data on
		// Ticks - Any custom tick values we are looking for (null in this case)
		// Snapshot - false give us streaming data, true gives one data snapshot
		// MarketDataOptions - tagValue list of additional options (API 9.71 and
		// newer)
		client.reqMktData(0, contract, null, false, null);
		// At this point our call is done and any market data events
		// will be returned via the tickPrice method
		Contract contract1 = new Contract();
		contract1.symbol("AAPL");
		contract1.exchange("SMART");
		contract1.secType("STK");
		contract1.currency("USD");
		client.reqMktData(1, contract1, null, false, null);		
*/	} // end RealTimeData

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#accountDownloadEnd(java.lang.String)
	 */
	@Override
	public void accountDownloadEnd(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#accountSummary(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void accountSummary(int arg0, String arg1, String arg2, String arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#accountSummaryEnd(int)
	 */
	@Override
	public void accountSummaryEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#accountUpdateMulti(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void accountUpdateMulti(int arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#accountUpdateMultiEnd(int)
	 */
	@Override
	public void accountUpdateMultiEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#bondContractDetails(int, com.ib.client.ContractDetails)
	 */
	@Override
	public void bondContractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#commissionReport(com.ib.client.CommissionReport)
	 */
	@Override
	public void commissionReport(CommissionReport arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#connectAck()
	 */
	@Override
	public void connectAck() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#connectionClosed()
	 */
	@Override
	public void connectionClosed() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#contractDetails(int, com.ib.client.ContractDetails)
	 */
	@Override
	public void contractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#contractDetailsEnd(int)
	 */
	@Override
	public void contractDetailsEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#currentTime(long)
	 */
	@Override
	public void currentTime(long arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#deltaNeutralValidation(int, com.ib.client.DeltaNeutralContract)
	 */
	@Override
	public void deltaNeutralValidation(int arg0, DeltaNeutralContract arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#displayGroupList(int, java.lang.String)
	 */
	@Override
	public void displayGroupList(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#displayGroupUpdated(int, java.lang.String)
	 */
	@Override
	public void displayGroupUpdated(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#error(java.lang.Exception)
	 */
	@Override
	public void error(Exception e) {
		// Print out a stack trace for the exception
        e.printStackTrace ();
	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#error(java.lang.String)
	 */
	@Override
	public void error(String str) {
		// Print out the error message
        System.err.println (str);
	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#error(int, int, java.lang.String)
	 */
	@Override
	public void error(int id, int errorCode, String errorMsg) {
		// Overloaded error event (from IB) with their own error 
		// codes and messages
	        System.err.println ("error: " + id + "," + errorCode + "," + errorMsg);
	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#execDetails(int, com.ib.client.Contract, com.ib.client.Execution)
	 */
	@Override
	public void execDetails(int arg0, Contract arg1, Execution arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#execDetailsEnd(int)
	 */
	@Override
	public void execDetailsEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#fundamentalData(int, java.lang.String)
	 */
	@Override
	public void fundamentalData(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#historicalData(int, java.lang.String, double, double, double, double, int, int, double, boolean)
	 */
	@Override
	public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume,
			int count, double WAP, boolean hasGaps) {
		
		System.out.println("YData " + spSymbols[reqId] + " " + date + " O: " + open + " H: " + high + " L: " + low + " C: " + close);

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#managedAccounts(java.lang.String)
	 */
	@Override
	public void managedAccounts(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#marketDataType(int, int)
	 */
	@Override
	public void marketDataType(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#nextValidId(int)
	 */
	@Override
	public void nextValidId(int orderId) {
		// Return the next valid OrderID
        nextOrderID = orderId;
	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#openOrder(int, com.ib.client.Contract, com.ib.client.Order, com.ib.client.OrderState)
	 */
	@Override
	public void openOrder(int arg0, Contract arg1, Order arg2, OrderState arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#openOrderEnd()
	 */
	@Override
	public void openOrderEnd() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#orderStatus(int, java.lang.String, double, double, double, int, int, double, int, java.lang.String)
	 */
	@Override
	public void orderStatus(int arg0, String arg1, double arg2, double arg3, double arg4, int arg5, int arg6,
			double arg7, int arg8, String arg9) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#position(java.lang.String, com.ib.client.Contract, double, double)
	 */
	@Override
	public void position(String arg0, Contract arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#positionEnd()
	 */
	@Override
	public void positionEnd() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#positionMulti(int, java.lang.String, java.lang.String, com.ib.client.Contract, double, double)
	 */
	@Override
	public void positionMulti(int arg0, String arg1, String arg2, Contract arg3, double arg4, double arg5) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#positionMultiEnd(int)
	 */
	@Override
	public void positionMultiEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#realtimeBar(int, long, double, double, double, double, long, double, int)
	 */
	@Override
	public void realtimeBar(int arg0, long arg1, double arg2, double arg3, double arg4, double arg5, long arg6,
			double arg7, int arg8) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#receiveFA(int, java.lang.String)
	 */
	@Override
	public void receiveFA(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#scannerData(int, int, com.ib.client.ContractDetails, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void scannerData(int arg0, int arg1, ContractDetails arg2, String arg3, String arg4, String arg5,
			String arg6) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#scannerDataEnd(int)
	 */
	@Override
	public void scannerDataEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#scannerParameters(java.lang.String)
	 */
	@Override
	public void scannerParameters(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#securityDefinitionOptionalParameter(int, java.lang.String, int, java.lang.String, java.lang.String, java.util.Set, java.util.Set)
	 */
	@Override
	public void securityDefinitionOptionalParameter(int arg0, String arg1, int arg2, String arg3, String arg4,
			Set<String> arg5, Set<Double> arg6) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#securityDefinitionOptionalParameterEnd(int)
	 */
	@Override
	public void securityDefinitionOptionalParameterEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#softDollarTiers(int, com.ib.client.SoftDollarTier[])
	 */
	@Override
	public void softDollarTiers(int arg0, SoftDollarTier[] arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickEFP(int, int, double, java.lang.String, double, int, java.lang.String, double, double)
	 */
	@Override
	public void tickEFP(int arg0, int arg1, double arg2, String arg3, double arg4, int arg5, String arg6, double arg7,
			double arg8) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickGeneric(int, int, double)
	 */
	@Override
	public void tickGeneric(int arg0, int arg1, double arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickOptionComputation(int, int, double, double, double, double, double, double, double, double)
	 */
	@Override
	public void tickOptionComputation(int arg0, int arg1, double arg2, double arg3, double arg4, double arg5,
			double arg6, double arg7, double arg8, double arg9) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickPrice(int, int, double, int)
	 */
	@Override
	public void tickPrice(int orderId, int field, double price, int canExecute) {
		try {
			// Print out the current price.
			// field will provide the price type:
			// 1 = bid, 2 = ask, 4 = last
			// 6 = high, 7 = low, 9 = close
			String[] fields = {"","bid","ask","","last","","high","low","","close"};
			System.out.println("tickPrice: " + spSymbols[orderId] + " " + fields[field] + " : " + price);
//			client.cancelMktData(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickSize(int, int, int)
	 */
	@Override
	public void tickSize(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickSnapshotEnd(int)
	 */
	@Override
	public void tickSnapshotEnd(int orderId) {
		System.out.println("tickSnapshotEnd: " + spSymbols[orderId] + " " + ++tickSnapshotEndCnt);
	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#tickString(int, int, java.lang.String)
	 */
	@Override
	public void tickString(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#updateAccountTime(java.lang.String)
	 */
	@Override
	public void updateAccountTime(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#updateAccountValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateAccountValue(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#updateMktDepth(int, int, int, int, double, int)
	 */
	@Override
	public void updateMktDepth(int arg0, int arg1, int arg2, int arg3, double arg4, int arg5) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#updateMktDepthL2(int, int, java.lang.String, int, int, double, int)
	 */
	@Override
	public void updateMktDepthL2(int arg0, int arg1, String arg2, int arg3, int arg4, double arg5, int arg6) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#updateNewsBulletin(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateNewsBulletin(int arg0, int arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#updatePortfolio(com.ib.client.Contract, double, double, double, double, double, double, java.lang.String)
	 */
	@Override
	public void updatePortfolio(Contract arg0, double arg1, double arg2, double arg3, double arg4, double arg5,
			double arg6, String arg7) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#verifyAndAuthCompleted(boolean, java.lang.String)
	 */
	@Override
	public void verifyAndAuthCompleted(boolean arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#verifyAndAuthMessageAPI(java.lang.String, java.lang.String)
	 */
	@Override
	public void verifyAndAuthMessageAPI(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#verifyCompleted(boolean, java.lang.String)
	 */
	@Override
	public void verifyCompleted(boolean arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ib.client.EWrapper#verifyMessageAPI(java.lang.String)
	 */
	@Override
	public void verifyMessageAPI(String arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting application");
		try	{
			// Create an instance
			// At this time a connection will be made
			// and the request for market data will happen
			Pokus myData = new Pokus();
			System.out.println("Ending application");
		} catch (Exception e)
		{
			e.printStackTrace ();
		}
	} // end main
}
