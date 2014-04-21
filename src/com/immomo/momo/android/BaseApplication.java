package com.immomo.momo.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.file.explore.FileState;
import com.immomo.momo.android.util.FileUtils;

public class BaseApplication extends Application
{

	/** mEmoticons 表情 **/
	public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
	public static List<String> mEmoticons = new ArrayList<String>();
	public static List<String> mEmoticons_Zem = new ArrayList<String>();
	public static List<String> mEmoticons_Zemoji = new ArrayList<String>();

	/** 缓存 **/
	private Map<String, SoftReference<Bitmap>> mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
	private HashMap<String, String> mLastMsgCache; // 最后一条消息缓存，以IMEI为KEY

	public List<NearByGroup> mNearByGroups = new ArrayList<NearByGroup>(); // 群列表
	private ArrayList<NearByPeople> mUnReadPeople; // 未读用户队列
	private HashMap<String, String> mLocalUserSession; // 本机用户Session信息
	private HashMap<String, NearByPeople> mOnlineUsers; // 在线用户集合，以IMEI为KEY
	
	public static HashMap<String,FileState> sendFileStates;//存放文件状态
	public static HashMap<String,FileState> recieveFileStates;//存放文件状态
	/** 屏幕长宽 **/
	public double mLongitude;
	public double mLatitude;

	private Bitmap mDefaultAvatar; // 默认头像
	private static BaseApplication instance; // 唯一实例

	//本地图像、声音、文件存储路径
	public static String IMAG_PATH;
	public static String VOICE_PATH;
	public static String FILE_PATH;
	public static String SAVE_PATH;
	// 程序在开始运行的时候,调用以下函数创建存储图片语音文件目录
	private void createSavePath()
	{
		if (null == IMAG_PATH)
		{
			SAVE_PATH = FileUtils.getSDPath();// 获取SD卡的根目录路径,如果不存在就返回Null
			if (null == SAVE_PATH)
			{
				SAVE_PATH = instance.getFilesDir().toString();// 获取内置存储区的地址
			}
			String appName = instance.getString(R.string.app_name);
			IMAG_PATH = SAVE_PATH + File.separator + appName + File.separator
					+ "image";
			VOICE_PATH = SAVE_PATH + File.separator + appName + File.separator
					+ "voice";
			FILE_PATH = SAVE_PATH + File.separator + appName + File.separator
					+ "file";
			if (!FileUtils.isFileExists(IMAG_PATH))
				FileUtils.createDirFile(BaseApplication.IMAG_PATH);// 如果目录不存在则创建目录
			if (!FileUtils.isFileExists(VOICE_PATH))
				FileUtils.createDirFile(BaseApplication.VOICE_PATH);
			if (!FileUtils.isFileExists(FILE_PATH))
				FileUtils.createDirFile(BaseApplication.FILE_PATH);

		}
	}

	/**
	 * <p>
	 * 获取BaseApplication实例
	 * <p>
	 * 单例模式，返回唯一实例
	 * 
	 * @return instance
	 */
	public static BaseApplication getInstance()
	{
		return instance;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		if (instance == null)
		{
			instance = this;
		}
		sendFileStates=new HashMap<String, FileState>();
		recieveFileStates=new HashMap<String, FileState>();
		mLocalUserSession = new HashMap<String, String>(14); // 存储用户登陆信息
		mDefaultAvatar = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_common_def_header);
		for (int i = 1; i < 64; i++)
		{
			String emoticonsName = "[zem" + i + "]";
			int emoticonsId = getResources().getIdentifier("zem" + i,
					"drawable", getPackageName());
			mEmoticons.add(emoticonsName);
			mEmoticons_Zem.add(emoticonsName);
			mEmoticonsId.put(emoticonsName, emoticonsId);
		}
		for (int i = 1; i < 59; i++)
		{
			String emoticonsName = "[zemoji" + i + "]";
			int emoticonsId = getResources().getIdentifier("zemoji_e" + i,
					"drawable", getPackageName());
			mEmoticons.add(emoticonsName);
			mEmoticons_Zemoji.add(emoticonsName);
			mEmoticonsId.put(emoticonsName, emoticonsId);
		}

		createSavePath();
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Log.e("BaseApplication", "onLowMemory");
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		Log.e("BaseApplication", "onTerminate");
	}

	public HashMap<String, String> getUserSession()
	{
		return mLocalUserSession;
	}

	/** 初始化相关基本参数 */
	public void initParam()
	{
		initOnlineUserMap(); // 初始化用户列表
		initUnReadMessages(); // 初始化未读消息
		initLastMsgCache(); // 初始化消息缓存
	}

	// mOnlineUsers setter getter
	public void initOnlineUserMap()
	{
		mOnlineUsers = new LinkedHashMap<String, NearByPeople>();
	}

	public void addOnlineUser(String paramIMEI, NearByPeople paramObject)
	{
		mOnlineUsers.put(paramIMEI, paramObject);
	}

	public NearByPeople getOnlineUser(String paramIMEI)
	{
		return mOnlineUsers.get(paramIMEI);
	}

	/**
	 * 移除在线用户
	 * 
	 * @param paramIMEI
	 *            需要移除的用户IMEI
	 * @param paramtype
	 *            操作类型，0:清空在线列表，1:移除指定用户
	 */
	public void removeOnlineUser(String paramIMEI, int paramtype)
	{
		if (paramtype == 1)
		{
			mOnlineUsers.remove(paramIMEI);
		} else if (paramtype == 0)
		{
			mOnlineUsers.clear();
		}
	}

	public HashMap<String, NearByPeople> getOnlineUserMap()
	{
		return mOnlineUsers;
	}

	// mLastMsgCache setter getter
	/** 初始化消息缓存 */
	public void initLastMsgCache()
	{
		mLastMsgCache = new HashMap<String, String>();
	}

	/**
	 * 新增用户缓存
	 * 
	 * @param paramIMEI
	 *            新增记录的对应用户IMEI
	 * @param paramMsg
	 *            需要缓存的消息对象
	 */
	public void addLastMsgCache(String paramIMEI, Message msg)
	{
		StringBuffer content = new StringBuffer();
		switch (msg.getContentType())
		{
		case FILE:
			content.append("<FILE>: ").append(msg.getMsgContent());
			break;
		case IMAGE:
			content.append("<IMAGE>: ").append(msg.getMsgContent());
			break;
		case VOICE:
			content.append("<VOICE>: ").append(msg.getMsgContent());
			break;
		default:
			content.append(msg.getMsgContent());
			break;
		}
		if (msg.getMsgContent().isEmpty())
		{
			content.append(" ");
		}
		mLastMsgCache.put(paramIMEI, content.toString());
	}

	/**
	 * 获取消息缓存
	 * 
	 * @param paramIMEI
	 *            需要获取消息缓存记录的用户IMEI
	 * @return
	 */
	public String getLastMsgCache(String paramIMEI)
	{
		return mLastMsgCache.get(paramIMEI);
	}

	/**
	 * 移除消息缓存
	 * 
	 * @param paramIMEI
	 *            需要清除缓存的用户IMEI
	 */
	public void removeLastMsgCache(String paramIMEI)
	{
		mLastMsgCache.remove(paramIMEI);
	}

	// mUnReadMessags setter getter
	/** 初始化未读消息队列 */
	public void initUnReadMessages()
	{
		mUnReadPeople = new ArrayList<NearByPeople>();
	}

	/**
	 * 新增未读消息用户
	 * 
	 * @param people
	 */
	public void addUnReadPeople(NearByPeople people)
	{
		Log.i("BaseActivity", "进入到 UnReadMessages()");
		if (!mUnReadPeople.contains(people))
			mUnReadPeople.add(people);
	}

	/**
	 * 获取未读消息用户队列
	 * 
	 * @return
	 */
	public ArrayList<NearByPeople> getUnReadPeopleList()
	{
		return mUnReadPeople;
	}

	/**
	 * 获取未读用户数
	 * 
	 * @return
	 */
	public int getUnReadPeopleSize()
	{
		return mUnReadPeople.size();
	}

	/**
	 * 移除指定未读用户
	 * 
	 * @param people
	 */
	public void removeUnReadPeople(NearByPeople people)
	{
		if (mUnReadPeople.contains(people))
			mUnReadPeople.remove(people);
	}

	/**
	 * 获取用户头像bitmap
	 * <p>
	 * 优先从缓存中获取信息，若不存在，则从SD卡中重新读取并设置缓存
	 * 
	 * @param paramAvatarName
	 *            头像文件名 "Avatar" + avatarID，例如 Avatar2
	 * @return
	 */
	public Bitmap getAvatar(String paramAvatarName)
	{
		if (mAvatarCache.containsKey(paramAvatarName))
		{
			Reference<Bitmap> reference = mAvatarCache.get(paramAvatarName);
			if (reference.get() == null || reference.get().isRecycled())
			{
				mAvatarCache.remove(paramAvatarName);
				return getAvatarFromRes(paramAvatarName);
			} else
			{
				return reference.get();
			}
		} else
		{
			return getAvatarFromRes(paramAvatarName);
		}
	}

	/**
	 * 将drawable中的指定图片转为bitmap资源
	 * 
	 * @param paramAvatarName
	 *            需要转换的图片文件名
	 * @return
	 */
	private Bitmap getAvatarFromRes(String paramAvatarName)
	{
		Bitmap returnBitmap = null;
		InputStream is = null;
		Bitmap bitmap = null;
		try
		{
			is = this.getResources().openRawResource(
					getIDfromDrawable(paramAvatarName));
			bitmap = BitmapFactory.decodeStream(is);
			if (bitmap == null)
			{
				throw new FileNotFoundException(paramAvatarName + "is not find");
			}
			mAvatarCache
					.put(paramAvatarName, new SoftReference<Bitmap>(bitmap));
			returnBitmap = bitmap;
		} catch (Exception e)
		{
			returnBitmap = mDefaultAvatar;
		} finally
		{
			try
			{
				if (is != null)
				{
					is.close();
					is = null;
				}
			} catch (IOException e)
			{

			}
		}
		return returnBitmap;
	}

	/**
	 * 获取drawable中指定文件名图片的id
	 * 
	 * @param paramAvatarName
	 *            图片名
	 * @return
	 */
	public int getIDfromDrawable(String paramAvatarName)
	{
		return this.getResources().getIdentifier(paramAvatarName, "drawable",
				getPackageName());
	}

}
