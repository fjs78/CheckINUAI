package cl.uai.checkin;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sql_horarios {
	
	public static final String KEY_ROWID = "id";
	public static final String KEY_IDPROFE = "id_del_profesor";
	public static final String KEY_NAME = "nombre";
	public static final String KEY_HORA = "hora";
	
	
	public static final String DATABASE_NAME = "CheckInUai_Horarios_DB";
	public static final String DATABASE_TABLE = "Horarios";
	public static final int DATABASE_VERSION = 1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + 
					KEY_ROWID + " INTEGER PRIMARY KEY, " +
					KEY_IDPROFE + " TEXT NOT NULL, " +
					KEY_NAME + " TEXT NOT NULL, "+
					KEY_HORA + " TEXT NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
	public Sql_horarios(Context c){
		ourContext = c;
	}
	
	public Sql_horarios open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}

	public long creatyEntry(int id, String id_profe, String nombre, String hora) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_IDPROFE, id_profe);
		cv.put(KEY_NAME, nombre);
		cv.put(KEY_HORA, hora);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}
	
	public String checkin(long id){
		String[] columns = new String[]{ KEY_ROWID, KEY_IDPROFE, KEY_NAME, KEY_HORA};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_IDPROFE + "=" + id, null, null, null, null, null);
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			String name = c.getString(1);
			c.close();
			return name;
		}
		c.close();
		return null;
	}
	
	public String getClase_id(String id_profe) {
		String[] columns = new String[]{ KEY_ROWID, KEY_IDPROFE, KEY_HORA};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_IDPROFE + "=" + id_profe, null, null, null, null);
		String result = null;
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iHora = c.getColumnIndex(KEY_HORA);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(compararHora(c.getString(iHora)))
				{result = c.getString(iRow);}
		}
		c.close();
		return result;
	}
	
	public String getNombreDeClase(String l){
		String[] columns = new String[]{ KEY_ROWID, KEY_NAME};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + l, null, null, null, null, null);
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			String name = c.getString(1);
			c.close();
			return name;
		}
		c.close();
		return null;
	}
	public String getHoraDeClase(String l){
		String[] columns = new String[]{ KEY_ROWID, KEY_HORA};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + l, null, null, null, null, null);
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			String hora = c.getString(1);
			SimpleDateFormat formato1;
			SimpleDateFormat formato2;
			java.util.Date horario;
			String retu = null;
			formato1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			formato2 = new SimpleDateFormat("hh:mm a");
			
			try {
				horario = formato1.parse(hora);
				retu = formato2.format(horario);
			} catch (java.text.ParseException e) {}
			
			c.close();
			return retu;
		}
		c.close();
		return null;
	}
	
	public boolean compararHora(String hora){
		SimpleDateFormat formato;
		java.util.Date horario;
		
		Calendar antes_pre = Calendar.getInstance();
		antes_pre.add(Calendar.MINUTE,-15);
		java.util.Date antes = antes_pre.getTime();
		
		Calendar despues_pre = Calendar.getInstance();
		despues_pre.add(Calendar.MINUTE,30);
		java.util.Date despues = despues_pre.getTime();
		
		formato = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
		horario = formato.parse(hora);
		boolean estaAntes;
		boolean estaDespues;
		
		if(horario.after(antes))
		{estaDespues = true;}
		else
		{estaDespues = false;}
		if(horario.before(despues))
		{estaAntes = true;}
		else
		{estaAntes = false;}
		
		if(estaDespues && estaAntes){return true;}
		else{return false;}
		
		}
		catch (java.text.ParseException e) {return false;}
	}
	
	public String getClase(long id){
		String[] columns = new String[]{ KEY_ROWID, KEY_IDPROFE};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_IDPROFE + "=" + id, null, null, null, null, null);
		if(c != null && !c.isAfterLast()){
			c.moveToFirst();
			String id_clase = c.getString(0);
			c.close();
			return id_clase;
		}
		c.close();
		return null;
	}
	
	public void deleteDatabase(){
		ourDatabase.delete(DATABASE_TABLE, null, null);
	}
}