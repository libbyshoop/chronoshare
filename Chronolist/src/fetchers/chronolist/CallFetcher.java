package fetchers.chronolist;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

public class CallFetcher {
	
	public long daystart;
	public String[] calls;
	
	public CallFetcher(Context context, long daystart){
		this.daystart = daystart;
		calls = getCalls(context, daystart);
		if(calls != null){
			System.out.println(calls[0]);
		}
		
	}
	
	public String[] getCalls(Context context, long daystart){
		
		Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);
		long nextday = daystart + DateUtils.DAY_IN_MILLIS;
		if (c.getCount() == 0){
			return null;
		}
		String[] calls = new String[c.getCount()];
		calls[0] = "to be replaced";
		long recent = 0;
		if(c.moveToFirst()){
			
	        for(int i=0 ; i < c.getCount();  i++){
	        	long recieved = c.getLong(c.getColumnIndex(android.provider.CallLog.Calls.DATE));
	        
	        	if(recieved > daystart && recieved < nextday){
	        		StringBuilder displayText = new StringBuilder();
	        		String name = c.getString(c.getColumnIndex(android.provider.CallLog.Calls.NUMBER)).toString();
            		String length = c.getString(c.getColumnIndex(android.provider.CallLog.Calls.DURATION)).toString();
            		displayText.append(name + " for " + length + " seconds");
            		calls[0] = displayText.toString(); //needs to be fixed
	        		
	        	}
	        	c.moveToNext();	
	        }
	        c.close();
		}
		if(calls[0].contains("to be replaced")){ //if nothing is ever added to the array
			calls = null;
		}
		return calls;
		
	}

}
