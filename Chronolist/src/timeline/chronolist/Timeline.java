package timeline.chronolist;

//import fetchers.chronolist.TextFetcher;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import fetchers.chronolist.Calendarfaux;
import fetchers.chronolist.ImageFetcher;
import fetchers.chronolist.TextFetcher;
import parser.chronolist.Day;
import parser.chronolist.DayAdapter;
import parser.chronolist.Message;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;

public class Timeline extends Activity {
	
	private ListView listView1;
	public static long oneday = 86400000;
	Calendar c = Calendar.getInstance();
	
	//public TextFetcher example = new TextFetcher(this);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		//////////////////////////////////
		
		//number of pannels to generate; hard programmed number will be replaced by
		//computed number when date picker is up and running
		Day[] daylist = new Day[30]; 
	
					
		/////////////////////////////////////
		
	
		
		
		//////////////////////////////////////////////
		
		TextFetcher texts = new TextFetcher(this); //calls date fetcher with CONTEXT
		Message[] messlist = new Message[texts.body.length]; //makes list of messages
		for(int i = 0; i < messlist.length; i++){
			messlist[i] = new Message(texts.senders[i], texts.body[i], texts.time[i]);
		}
		int messpos = 0;
		
		///////////////////////////////////////////////
		
		for(int i = 0; i < 30; i++){
			
			long mildate = getStart() - oneday*i;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
			
			
			ImageFetcher images = new ImageFetcher(this);
			String[] temp_photos = new String[images.photos.length];
			
			for(int j = 0; j < images.photos.length; j++){
				
			}
			
			
			
			
			String dateString = formatter.format(new Date(mildate));
			Vector<Message> tmp = populateMessages(this, mildate, messpos, messlist);
			messpos += tmp.size();
			Message[] messport = new Message[]{new Message(), new Message()};
			//System.out.println(dateString + " has " + (tmp.size()));
			
			if(tmp.size() > 1){
				messport = new Message[tmp.size()];
				tmp.copyInto(messport);
			}
			if(tmp.size() == 1){
				messport = new Message[2];
				messport[0] = tmp.elementAt(0);
				messport[1] = new Message();
			}
			tmp.clear();
			
			daylist[i] = new Day(dateString, messport, images.photos);
			
			//Calendarfaux appointments = new Calendarfaux(this, mildate);
			//System.out.println(appointments.appointment);
			//System.out.println(dateString);
			
			///////////////////////////////////////////////////
			

			
			
		}
		
	
	   
		
		
		
	
		
		
	
		
		DayAdapter adapter = new DayAdapter(this, R.layout.list_row, daylist);
		
		listView1 = (ListView)findViewById(R.id.listView1);
		
		listView1.setAdapter(adapter);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}
	
	public static long getStart(){
		
		Calendar rightNow = Calendar.getInstance();
		long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
	    long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
	    long dayStart = rightNow.getTimeInMillis() - sinceMidnight;
	    return dayStart;
		
	}
	
	public static Vector<Message> populateMessages(Context context, long mildate, int messpos, Message[] messlist){

		Vector<Message> tmp = new Vector<Message>();
		boolean durday = true;
		
		while(durday == true){ //while we're still in the day we're looking at
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

	
