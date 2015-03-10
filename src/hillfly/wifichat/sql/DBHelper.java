package hillfly.wifichat.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	private static final int VERSION = 1;          //数据库版本
	private static final String DBNAME="user.db";  //数据库名
	private static final String T_NAME="t_user";   //数据表名
	
	/*获取数据库中操作表名字*/
	public String getTableName()
	{
		return T_NAME;
	}
	
	/*构造函数
	 * 参数为版本，数据库名
	 */
	public DBHelper(Context context)
	{
		super(context, DBNAME, null, VERSION);
	}
	
	/* T_NAME为创建数据表的名字
	 * 该函数初始化类的时候会调用，T_NAME为表名,如果表不存在则创建，如果存在则打开
	 * id(int) 
	 * name(varchar)
	 * sex(char)
	 * IMEI(varchar)
	 * status(int)
	 * avatar(int)
	 * lastdate(text)
	 * device(text)
	 * constellation(text)
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table " + T_NAME +
				" (id integer primary key,name varchar(20),sex char,age integer,IMEI varchar(20),ip varchar(20),status integer,avater integer,lastdate text,device text,constellation text)");
	}

	/*该函数当检测到版本数增加时候会自动调用
	 * 一般用来数据库表格的改动或者扩展
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		
	}
	

}
