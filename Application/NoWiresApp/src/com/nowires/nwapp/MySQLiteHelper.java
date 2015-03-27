package com.nowires.nwapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.util.Log;



public class MySQLiteHelper extends SQLiteOpenHelper {
	
	//Database version
	private static final int dbVer = 1;
	private static final String dbName = "nowireslocal.db";
	private static final String dbPath = "/data/data/com.nowires.nwapp/databases/";
	private SQLiteDatabase mydb;
    GetContext getContext;
    Context myContext;

    
	public MySQLiteHelper(Context context) {
		super(context, dbName, null, dbVer);
		myContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		getContext = new GetContext();
		//myContext = getContext.getmyContext();
	}
	
	public void createdb(){
		if(databaseExist()){ 
				Log.d("Copied Database","Copied Database");
		}
		else this.getReadableDatabase();	
	}
	
	public boolean databaseExist()
	{
	    SQLiteDatabase checkDB = null;
	    String dbFullPath = dbPath+dbName;
	    try{
	    	checkDB = SQLiteDatabase.openDatabase(dbFullPath,null,SQLiteDatabase.OPEN_READONLY);
	    }catch(SQLiteException e){}
	    return checkDB != null ? true : false;
	}
	
	@Override 
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
		db.execSQL("drop table if exists",null);
		onCreate(db);
	}
	
	public void openDataBaseRead() throws SQLException{
		 
    	//Open the database
        String myPath = dbPath + dbName;
    	mydb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
	
	public void openDataBaseWrite() throws SQLException{
		 
    	//Open the database
        String myPath = dbPath + dbName;
    	mydb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
 
    }
	
	 public void CopyDB() throws IOException {
		 OutputStream outputStream = new FileOutputStream(dbPath);
		 
		 FileInputStream inputStream =  (FileInputStream) (myContext.getAssets()).open(dbName);
		 			byte[] buffer = new byte[1024];
			        int length;
			        while ((length = inputStream.read(buffer)) > 0) {
			        	outputStream.write(buffer, 0, length);
			        }
			        inputStream.close();
			        outputStream.close();
		}
 	
	public void closedb(){
		mydb.close();
	}
	
	
	//Database manipulations
	
 	public void insertInitialTables(){
 		try{mydb.execSQL("CREATE TABLE Jobs_Tbl(Job_ID integer primary key autoincrement,Job_Name varchar,Job_Total_Time varchar(50), Job_Total_Layers int, Job_Temp int, Job_Status varchar(20), Job_Layer_Height long, Job_Finished_Time varchar);");}catch(SQLException e){}	
 		try{mydb.execSQL("CREATE TABLE Jobs_Queue_Tbl(Job_Queue_ID integer primary key autoincrement,Job_ID_FK integer,Job_Total_Time varchar(50),foreign key (Job_ID_FK) references Jobs_Tbl(Job_ID));");}catch(SQLException e){}	
		try{mydb.execSQL("CREATE TABLE Jobs_Path_Tbl(Job_Path_ID integer primary key autoincrement,Job_Name varchar,Job_Path varchar,Job_Upload_Status varchar);");}catch(SQLException e){}
 		//try{mydb.execSQL("CREATE TABLE Workout_Weight_Ref_Tbl(Workout_Ref_ID integer primary key autoincrement,Workout_Completed_ID_FK integer,Weight_ID_FK integer,foreign key (Workout_Completed_ID_FK) references Workout_Completed(Workout_Completed_ID),foreign key (Weight_ID_FK) references Weight(Weight_ID));");}catch(SQLException e){}

 	}
	
 	//Inserts
 	
 	public void insertNewJob(String jobName,String jobTotalTime,String jobTotalLayers, int jobTemp, String jobStatus, long jobLayerHeight, String jobFinishedTime){
		try{
			
			ContentValues values = new ContentValues();
			
			values.put("Job_Name", jobName);
			values.put("Job_Total_Time", jobTotalTime);
			values.put("Job_Total_Layers", jobTotalLayers);
			values.put("Job_Temp", jobTemp);
			values.put("Job_Status", jobStatus);
			values.put("Job_Layer_Height", jobLayerHeight);
			values.put("Job_Finished_Time", jobFinishedTime);
			mydb.insert("Jobs_Tbl", null, values);

		}catch(SQLException e){}
	}
 	
 

 	public void insertNewJobPath(String jobName,String jobContentPath){
 			try{
 				
 				ContentValues values = new ContentValues();
 				
 				values.put("Job_Path", jobContentPath);
 				values.put("Job_Name", jobName);
 				//mydb.update("Jobs_Path_Tbl",values,"recipe_name like'"+jobName+"'",null);
 				mydb.insert("Jobs_Path_Tbl", null, values);


 			}catch(SQLException e){}
 		}
 /*
 	public void setAsFavourite(String recipeName){
		try{
 		ContentValues values = new ContentValues();
		
		values.put("favourite", 1);
		mydb.update("Recipe_Tbl",values,"recipe_name like '"+recipeName+"'",null);
 		Log.d("inserting fav","setted "+recipeName+" as fav in dataB");
		}catch(SQLException e){e.printStackTrace();}
 	}
 */	
 	


 	//Gets
 	public ArrayList<String> getJobFromJobsTbl(){
		ArrayList<String> jobNamesAL= new ArrayList<String>();
		Cursor c=null;
		String temp;
		
		try{
			c = mydb.rawQuery("select job_name from jobs_tbl",null);

			while (c.moveToNext()){	
					temp = c.getString(c.getColumnIndex("Job_Name"));
					if(!jobNamesAL.contains(temp)){jobNamesAL.add(c.getString(c.getColumnIndex("Job_Name")));}
					}	
			}catch(SQLException e){} 
			c.close();

		return jobNamesAL;
	}
 	
 	public ArrayList<String> getJobFromJobsPathTbl(){
		ArrayList<String> jobNamesAL= new ArrayList<String>();
		Cursor c=null;
		String temp;
		
		try{
			c = mydb.rawQuery("select job_name from Jobs_Path_Tbl",null);

			while (c.moveToNext()){	
					temp = c.getString(c.getColumnIndex("Job_Name"));
					if(!jobNamesAL.contains(temp)){jobNamesAL.add(c.getString(c.getColumnIndex("Job_Name")));}
					}	
			}catch(SQLException e){} 
			c.close();

		return jobNamesAL;
	}
 	
 	
 	public boolean checkIfExists(String jobName){
 		boolean check=false;
		Cursor c=null;

 		try{
			c = mydb.rawQuery("select job_name from job_name where job_name like '"+jobName+"'",null);

			while (c.moveToNext()){	
					if(c.getString(c.getColumnIndex("Job_Name")).equals(jobName)) check=true;
					}	
			
			c.close();
			}catch(SQLException e){} 
 		
 		return check;
 	}
 	
 	
 	
 	/*
 	public String getRecipeContentPath(String recipeName){
		String recipeContent = null;
		Cursor c=null;
		
		try{
			
			c = mydb.rawQuery("select recipe_content_path from recipe_tbl where recipe_name like '"+recipeName+"'",null);

			while (c.moveToNext()){	
				    recipeContent = c.getString(c.getColumnIndex("Recipe_Content_Path"));
					}	
			}catch(SQLException e){} 
			c.close();

		return recipeContent;
	}
 	
 	*/
 	
 	
 	//Removes
 	
 	public void removeJob(String jobName){

		try{		
			mydb.delete("job_tbl",  "job_name like '"+jobName+"'", null);
		}catch(SQLException e){} 

	}
 	
 	
}//end of MySQLiteHelper class

class GetContext extends Activity{
	Context myContext;
	
	public Context getmyContext(){
		myContext = (Context) getApplicationContext();
		return myContext;
	}
	
}