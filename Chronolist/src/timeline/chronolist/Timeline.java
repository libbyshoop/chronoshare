package timeline.chronolist;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import parser.chronolist.Day;
import parser.chronolist.DayAdapter;
import parser.chronolist.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.ListView;
import fetchers.chronolist.CalendarFetcher;
import fetchers.chronolist.CallFetcher;
import fetchers.chronolist.ImageFetcher;
import fetchers.chronolist.TextFetcher;

//The timeline view of all content. Calls fetchers to retrieve content. 

public class Timeline extends Activity {
	
	private ListView listView1; //listView 1 holds all the panels
	public static long oneday = 86400000; //the number of milliseconds in a day
	Calendar c = Calendar.getInstance(); //makes a calendar
	
	/**
	 * Simple Dialog used to show the splash screen
	 */
	protected Dialog mSplashDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        setContentView(R.layout.activity_timeline);       
        
        //number of panels to generate; hard programmed number will be replaced by
	    //computed number when date picker is up and running
		Day[] daylist = new Day[15]; 
		
		TextFetcher texts = new TextFetcher(this); //calls date fetcher with CONTEXT


		Message[] messlist = new Message[texts.body.length]; //makes list of messages
		for(int i = 0; i < messlist.length; i++){
		messlist[i] = new Message(texts.senders[i], texts.body[i], texts.time[i]);
		}
		//we do this OUTSIDE of the fetcher because of the number of messages usually on the device
		//the other data types usually have far less instances, so cycling through them through each
		//date doesn't take very long. This is not true of messages. And since messages are stored 
		//chronologically, we can keep track of a position where we last retrieved a message
		//and only have to cycle through all the messages once. 
	
		int messpos = 0; //keep track of the last retrieved message

		for(int i = 0; i < 15; i++){ //for each panel
			
			long mildate = getStart() - oneday*i; //the start of the day of the midnight in milliseconds
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
			String dateString = formatter.format(new Date(mildate)); //string represent of mildate
			
			//gets a vector of messages received on that day
			Vector<Message> tmp = populateMessages(this, mildate, messpos, messlist);
			if (!tmp.isEmpty()){
			messpos += tmp.size(); //increases messpos based off of how messages were read
			}else{
				messpos += 0;
			}
			
			Message[] messport = new Message[tmp.size()]; //makes array equal to size of vector
			if (!tmp.isEmpty()){
			tmp.copyInto(messport); //copies vector into array
			}
			tmp.clear(); //clears vector just in case
			CalendarFetcher appointments = new CalendarFetcher(this, mildate); //gets appointments
			ImageFetcher images = new ImageFetcher(this, mildate); //gets images
			CallFetcher calls = new CallFetcher(this, mildate); //gets calls

			//makes new day with all the information just retrieved
			daylist[i] = new Day(dateString, messport, images.photos, appointments.appointment, calls.calls);
			}
		
		DayAdapter adapter = new DayAdapter(this, R.layout.list_row, daylist);
	
		listView1 = (ListView)findViewById(R.id.listView1);
		
		listView1.setAdapter(adapter);
    }

	
	//makes the setting button. We'll need to add/alter this code
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}
	
	//method gets the start of the current day in milliseconds
	public static long getStart(){
		
		Calendar rightNow = Calendar.getInstance();
		long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
	    long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
	    long dayStart = rightNow.getTimeInMillis() - sinceMidnight;
	    return dayStart;
		
	}
	
	//gets all the messages from messlist from the inputed day
	public static Vector<Message> populateMessages(Context context, long mildate, int messpos, Message[] messlist){
		Vector<Message> tmp = new Vector<Message>();
		boolean durday = true;
		
		while(durday == true && messlist.length != 0){ //while we're still in the day we're looking at
			long timerec = Long.valueOf(messlist[messpos].time); //convert the time at position
			
			if(timerec > mildate){
				//System.out.println(messpos);
				//if the time received was after the start of the day
				tmp.add(messlist[messpos]); //add that message to the portion						 
				messpos += 1;   // of messages that occured that day and move to next pos
				}
			else{
				durday = false;
			}
		}
		
		return tmp;
		
		
		
		
	}
}

	
