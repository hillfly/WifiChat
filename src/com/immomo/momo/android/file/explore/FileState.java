package com.immomo.momo.android.file.explore;

public class FileState
{
	public long fileSize = 0;
	public long currentSize = 0;
	public String fileName = null;
	public int percent = 0;
	 
	public FileState(){
		
	}
	public FileState(long fileSize,long currentSize,String fileName){
		this.fileSize = fileSize;
		this.currentSize = currentSize;
		this.fileName = fileName;
	}
}
