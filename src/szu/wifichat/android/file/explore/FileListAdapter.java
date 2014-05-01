package szu.wifichat.android.file.explore;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import szu.wifichat.android.R;

public class FileListAdapter extends BaseAdapter
{
	private ArrayList<FileState> filesStates = null;//接收到的对方传过来的文件名 
	private Context context = null;
	
	public FileListAdapter(Context context)
	{
		this.context=context;
	}
	
	 
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return filesStates.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return filesStates.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View vi = inflater.inflate(R.layout.send_recieve_file_layout, null);
		ImageView fileIcon = (ImageView)vi.findViewById(R.id.file_icon);
		TextView fileName = (TextView)vi.findViewById(R.id.file_name);
		TextView fileSize = (TextView)vi.findViewById(R.id.file_size);
		ProgressBar bar = (ProgressBar)vi.findViewById(R.id.file_progress);
		TextView filePercent = (TextView)vi.findViewById(R.id.file_percent);
		
		FileState fs = filesStates.get(position);//获得一个文件状态信息
		String ext = fs.fileName.substring(fs.fileName.lastIndexOf(".")+1);
		Integer srcId = Constant.exts.get(ext);
		if(null == srcId)srcId = R.drawable.file_icon_default;
		fileIcon.setImageResource(srcId);
		fileName.setText(fs.fileName);
		fileSize.setText(Constant.formatFileSize(fs.fileSize));
		bar.setMax(100);
		bar.setProgress(fs.percent);
		filePercent.setText(Constant.formatFileSize(fs.currentSize)+"/"+Constant.formatFileSize(fs.fileSize));
		if(fs.percent==100)filePercent.setTextColor(Color.GREEN);
		return vi;
	}
	
	public void setResources(ArrayList<FileState> filesStates){
		this.filesStates = filesStates;
	}

}
