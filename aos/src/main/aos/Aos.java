package aos;

import java.util.ArrayList;
import java.util.Iterator;

import com.ib.client.Types.NewsType;
import com.ib.controller.ApiController;
import com.ib.controller.Formats;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController.IBulletinHandler;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.ITimeHandler;

public class Aos implements IConnectionHandler {
	public static Aos INSTANCE;
	private ApiController m_controller;
	private final Logger m_inLogger = new Logger();
	private final Logger m_outLogger = new Logger();	
	
	public ILogger getInLogger()            { return m_inLogger; }
	public ILogger getOutLogger()           { return m_outLogger; }	

	@Override
	public void accountList(ArrayList<String> accountList) {
		show( "Received account list");
		Iterator<String> i = accountList.iterator();
		while (i.hasNext()) {
			show(i.next());
		}
	}

	@Override
	public void connected() {
		show( "connected");
		
		controller().reqCurrentTime( new ITimeHandler() {
			@Override public void currentTime(long time) {
				show( "Server date/time is " + Formats.fmtDate(time * 1000) );
			}
		});
		
		controller().reqBulletins( true, new IBulletinHandler() {
			@Override public void bulletin(int msgId, NewsType newsType, String message, String exchange) {
				String str = String.format( "Received bulletin:  type=%s  exchange=%s", newsType, exchange);
				show( str);
				show( message);
			}
		});		
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		show("Disconnected");

	}

	@Override
	public void error(Exception e) {
		show( e.toString() );		
	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		show( id + " " + errorCode + " " + errorMsg);
	}

	@Override
	public void show(String str) {
		System.out.println(str);

	}
	
	private void run() {
		controller().connect( "127.0.0.1", 4001, 0, null );
	}
	
    public static void start( Aos aos ) {
        INSTANCE = aos;
        INSTANCE.run();
    }
    
    public ApiController controller() {
        if ( m_controller == null ) {
            m_controller = new ApiController( this, getInLogger(), getOutLogger() );
        }
        return m_controller;
    }    

	public static void main(String[] args) {
		System.out.println("starting aos...");
		start( new Aos());

	}
	private static class Logger implements ILogger {

		@Override public void log(final String str) {
			System.out.println(str);
		}
	}
}
