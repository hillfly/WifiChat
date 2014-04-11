package com.immomo.momo.android.file.explore;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.immomo.momo.android.R;

public class MyFileAdapter extends BaseAdapter
{

	private LayoutInflater mInflater;
	private List<FileStyle> filePaths;
	private Context context = null;
	//  BaseAdapter见http://www.cnblogs.com/loulijun/archive/2011/12/28/2305016.html

	  
	 public void setDatasource(List<FileStyle> filePaths){
		  this.filePaths = filePaths;
	  }
 
	  public MyFileAdapter(Context context,List<FileStyle> filePaths)
	  {
	    mInflater = LayoutInflater.from(context);
	    this.filePaths = filePaths;
	    this.context = context;
	  }
	  
	  @Override
	  public int getCount()
	  {
	    return filePaths.size();
	  }
	  @Override
	  public Object getItem(int position)
	  {
	    return filePaths.get(position);
	  }
	  
	  @Override
	  public long getItemId(int position)
	  {
	    return position;
	  }
	  
	  @Override
	  public View getView(int position,View convertView,ViewGroup parent)
	  {
		  View fileView = mInflater.inflate(R.layout.row_file_layout, null);
		  fileView.setTag(Integer.valueOf(position));
		  ImageView fileIcon = (ImageView)fileView.findViewById(R.id.file_icon);
		  TextView fileName = (TextView)fileView.findViewById(R.id.file_name);
		  TextView fileSize = (TextView)fileView.findViewById(R.id.file_size);
		  CheckBox fileSelected = (CheckBox)fileView.findViewById(R.id.file_selected);
		  
		  FileStyle file = filePaths.get(position);
		  String fileFullPath = file.fullPath;
		  fileName.setText(file.getFileName());
		  if(file.isDirectory)
	      {
			  fileIcon.setImageResource(R.drawable.folder);
			  ((TableRow)((TableLayout)fileView).getChildAt(0)).removeViewAt(3);//如果是文件夹则移除最后面的那个文件选择框
	      }
	      else
	      {//根据文件的扩展名设置文件的图标
	    	  String ext = fileFullPath.substring(fileFullPath.lastIndexOf(".")+1);
	    	  if(Constant.exts.containsKey(ext)){
	    		  fileIcon.setImageResource(Constant.exts.get(ext));
	    	  }else{
	    		  fileIcon.setImageResource(R.drawable.file_icon_default);
	    	  }
	    	  fileSize.setText(Constant.formatFileSize(file.size));
	    	  fileSelected.setTag(position);//设置CheckBox在列表中的序号（也是文件列表项序号），以便根据该序号获得该对象并对其进行相关操作
	    	  Boolean isFileSelected = Constant.fileSelectedState.get(Integer.valueOf(position));
	    	  if(null!=isFileSelected)fileSelected.setChecked(isFileSelected);
	      }
	    return fileView;
	  }
}
