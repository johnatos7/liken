package com.liken.data;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//VERSION 1= profile (id,username,facebookid,email,profilepicpath),TOPICS(id)
public class SQLiteHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="FoodeeDB";

	 public SQLiteHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PROFILE_TABLE = "CREATE TABLE Profile ( id  INTEGER ,  username   TEXT, facebookId INTEGER, email TEXT, profilepicpath TEXT)";
        db.execSQL(CREATE_PROFILE_TABLE);
        
        String CREATE_TOPICS_TABLE = "CREATE TABLE TOPICS ( id  INTEGER)";
        db.execSQL(CREATE_TOPICS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}
	
	 
   public void insertprofile(ContentValues data) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Inserting Row
        db.insert("Profile", null, data);
        db.close(); // Closing database connection
    }
  
   public String getUserID() {
	String ID=new String();
	   SQLiteDatabase db = this.getReadableDatabase();
    	
    	 Cursor cursor = db.query("Profile", new String[]{"id"},  null,
                null, null, null, null, null);
    	  
    	 while(cursor.moveToNext()){
    		 ID=cursor.getString(0);
    	 }  
    	 db.close(); // Closing database connection
    	 return ID;
    }
   // Getting single contact
   public HashMap<String,String> getProfile() {
	   HashMap<String,String> profile=new HashMap<String,String>();
	   SQLiteDatabase db = this.getReadableDatabase();
    	
    	 Cursor cursor = db.query("Profile", null,  null,
                null, null, null, null, null);
    	  
    	 while(cursor.moveToNext()){
    		 String id=cursor.getString(0);
    		 String username=cursor.getString(1);
    		 String facebookId=cursor.getString(2);
    		 String email=cursor.getString(3);
    		 String profilepicpath=cursor.getString(4);
    		 
    		 
    		 profile.put("id", id);
    		 profile.put("username", username);
    		 profile.put("facebookId", facebookId);
    		 profile.put("email", email);
    		 profile.put("profilepicpath", profilepicpath);  	
    	 }  
    	 db.close(); // Closing database connection
    	 return profile;
    }
   
   public void insertTopics(ArrayList<ContentValues> datas) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0;i<datas.size();i++){
        // Inserting Row
        db.insert("TOPICS", null, datas.get(i));
        }
        db.close(); // Closing database connection
    }
 

   public ArrayList<String> getTopics() {
	   ArrayList<String> listTopics=new ArrayList<String>();
	   SQLiteDatabase db = this.getReadableDatabase();
    	
    	 Cursor cursor = db.query("TOPICS", null,  null,
                null, null, null, null, null);
    	  
    	 while(cursor.moveToNext()){
    		 String topicID=cursor.getString(0);  		
    		 listTopics.add(topicID);
    	 }  
    	 db.close(); // Closing database connection
    	 return listTopics;
    }
  
 
    // Deleting single contact
    public void deleteAllEvents() {
      /*  SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();*/
    }
    
    // Deleting single contact
    public void deleteEvent(String invites_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Calendar", "Invites_ID = ?",
                new String[] { invites_ID });
        db.close();
    }
    
    public void deleteAllData(){
    	
    	SQLiteDatabase db = this.getWritableDatabase();
		String drop_tbl_profile = "DELETE FROM PROFILE";
		db.execSQL(drop_tbl_profile);
		String drop_tbl_topics = "DELETE FROM TOPICS";
		db.execSQL(drop_tbl_topics);
		db.close();
    }
 
 
  
}
