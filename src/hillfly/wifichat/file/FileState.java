package hillfly.wifichat.file;

import hillfly.wifichat.bean.Message.CONTENT_TYPE;

public class FileState
{
	public long fileSize = 0;
	public long currentSize = 0;
	public String fileName = null;
	public int percent = 0;
	public CONTENT_TYPE type = CONTENT_TYPE.TEXT;

	public FileState()
	{

	}

	public FileState(String fileFullPath)
	{
		this.fileName = fileFullPath;
	}

	public FileState(String fileFullPath,CONTENT_TYPE type)
	{
		this(fileFullPath);
		this.type=type;
	}
	
	public FileState(long fileSize, long currentSize, String fileName)
	{
		this.fileSize = fileSize;
		this.currentSize = currentSize;
		this.fileName = fileName;
	}

	public FileState(long fileSize, long currentSize, String fileName,
			CONTENT_TYPE type)
	{
		this(fileSize, currentSize, fileName);
		this.type = type;
	}
}
