package fetchers.chronolist;

import java.sql.Date;
import java.util.HashSet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

public class CalendarFetcher{
	
	public long lDate; //midnight in milliseconds
	public String appointment; //string of appointment
	
	public CalendarFetcher(Context context, long lDate){
		appointment = fetch(context, lDate);
	}
	
	public String fetch(Context context, long lDate){
	    	
	    	// Fetch a list of all calendars synced with the device, their display names and whether the
			// user has them selected for display.
	    	ContentResolver contentResolver = context.getContentResolver();
	        final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
	                (new String[] { "_id", "displayName", "selected" }), null, null, null);
	
	        HashSet<String> calendarIds = new HashSet<String>();                    
	        while (cursor.moveToNext()) {                       
	            final String _id = cursor.getString(0);
	            final String displayName = cursor.getString(1);
	            final Boolean selected = !cursor.getString(2).equals("0");                     
	            calendarIds.add(_id);
	        }
	    	
	     
	     // For each calendar, fetch all the events from start of the day to the end of the day.
	    StringBuilder displayText = new StringBuilder();
		for (String id : calendarIds) {
			Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
			ContentUris.appendId(builder, lDate);
			ContentUris.appendId(builder, lDate + DateUtils.DAY_IN_MILLIS);

			Cursor eventCursor = contentResolver.query(builder.build(),
					new String[] { "title", "begin", "end", "allDay"}, "Calendars._id=" + id,
					null, "startDay ASC, startMinute ASC"); 
			// For a full list of available columns see http://tinyurl.com/yfbg76w
	
			while (eventCursor.moveToNext()) {
				final String title = eventCursor.getString(0);
				final Date begin = new Date(eventCursor.getLong(1));
				final Date end = new Date(eventCursor.getLong(2));
				final Boolean allDay = !eventCursor.getString(3).equals("0");
				
				//displayText.append("Title: " + title + " Begin: " + begin + " End: " + end +
					//	" All Day: " + allDay);
				displayText.append(title);
			}
		}
		return displayText.toString();
	
	}
}