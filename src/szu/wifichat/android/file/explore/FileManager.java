package szu.wifichat.android.file.explore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import szu.wifichat.android.R;

public class FileManager extends ListActivity
{	
	private static final String TAG="FileManager";
	private List<FileStyle> filePaths=new ArrayList<FileStyle>();//保存当前目录下的所有文件的文件路径
	private String rootPath = "/";//根目录路径
	private String parentPath = "/";//初始化上级目录路径
	private Button returnRootBtn = null;
	private Button returnParentBtn = null;
	private ArrayList<FileStyle> selectedFilePath = new ArrayList<FileStyle>();//保存被选择的所有文件路径
	private TextView mPath;//用来显示当前目录路径
	private String currentPath = null;//当前路径
	private int selectType = 0;
	private MyFileAdapter adapter = null; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);//		FEATURE_NO_TITLE：无标题
		setContentView(R.layout.file_select);

		Intent intent = getIntent();
		selectType = intent.getExtras().getInt("selectType");
		
		Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);//确定按钮
		buttonConfirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				if(selectType == Constant.SELECT_FILES){//如果当前为选择文件模式则返回当前选择的所有文件
					intent.putExtra("selectType", Constant.SELECT_FILES);
					intent.putExtra("files", selectedFilePath);
				}else if(selectType == Constant.SELECT_FILE_PATH){//如果当前为文件夹选择模式则返回当前选择的文件夹路径
					File file = new File(currentPath);
					intent.putExtra("selectType", Constant.SELECT_FILE_PATH);
					if(file.canWrite()){
						intent.putExtra("fileSavePath", currentPath);
					}
				}
				for(FileStyle file:selectedFilePath)
					Log.d(TAG, file.getFullPath());
				setResult(RESULT_OK, intent);
//				Log.d(TAG, "setResult");
				finish();
//				Log.d(TAG, "finish");
			}
		});
		
		Button buttonCancle = (Button) findViewById(R.id.buttonCancle);//取消按钮
		buttonCancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			/*	if(selectType == Constant.SELECT_FILE_PATH){//如果当前为选择文件夹模式，说明现在是保存文件操作，如果这个cancel按钮被点击
					Intent intent = new Intent();			//说明用户拒绝接收文件，则向对方发送一个拒绝接收文件的指令。
					intent.setAction(Constant.refuseReceiveFileAction);
					sendBroadcast(intent);
				}  */
				finish();
			}
		});
		
		returnRootBtn = (Button)findViewById(R.id.return_root_path);
		returnRootBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View returnRootBtn) {
				getFileDir(rootPath);
			}
		});
		returnParentBtn = (Button)findViewById(R.id.return_parent_path);
		returnParentBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View returnParentBtn) {
				getFileDir(parentPath);
			}
		});
		mPath = (TextView) findViewById(R.id.mPath);
		TextView title = (TextView)findViewById(R.id.file_select_title);
		if(selectType == Constant.SELECT_FILE_PATH){
			title.setText(getString(R.string.select_path_for_save));
		}else{
			title.setText(getString(R.string.select_file_for_send));
		}
		
		getFileDir(rootPath);
	}
	
	/**
	 * @param filePath 需要打开的目录路径
	 * 打开该目录并获得里面的所有文件信息，包括目录和文件
	 * 并把所有文件名存放在fileNames列表中，把所有文件路径存放在filePaths列表中
	 */
	private void getFileDir(String filePath) {
		if(null==filePath)return;//检测是不是超出了根目录
		File dirFile = new File(filePath);
		parentPath = dirFile.getParent();//获得当前目录的父目录
		File[] files = dirFile.listFiles();//提取当前目录下的所有文件
		if(null!=files){
			filePaths.clear();//filePaths保存当前目录下的所有文件的文件路径
			selectedFilePath.clear();//selectedFilePath保存被选择的所有文件路径
			currentPath = filePath;//currentPath当前路径
			Constant.fileSelectedState.clear();//fileSelectedState文件选择状态保存
			mPath.setText(getString(R.string.current_path_label)+filePath);
			for (File file : files) {
				if(selectType == Constant.SELECT_FILE_PATH){//如果选择模式为文件夹模式则只获得文件夹
					if(file.isDirectory()){
						FileStyle fPath = new FileStyle(1,file.getPath());
						filePaths.add(fPath);
					}
				}else{//如果选择模式为文件模式则获得所有文件夹与文件
					if(file.isDirectory()){
						FileStyle fPath = new FileStyle(1,file.getPath());
						filePaths.add(fPath);
					}else{
						FileStyle fPath = new FileStyle(2,file.getPath(),file.length(),false);
						filePaths.add(fPath);
					}
				}
			}
			Collections.sort(filePaths);//进行排序，把文件夹排在前面，文件排在后面
			if(null==adapter){
				adapter = new MyFileAdapter(this,filePaths);
			}else{
				adapter.setDatasource(filePaths);
			}
			setListAdapter(adapter);//把获得的文件信息传给List适配器，让适配器更新列表条目
		}
	}
	
	/**
	 * 当列表中的条目被点击时会触发该事件
	 */
	@Override
	protected void onListItemClick(ListView listView, View itemView, int position, long id) {
		File file = new File(filePaths.get(position).fullPath);//获得在List中被点击的这个item所对应的文件
		if (file.isDirectory()) {//如果该文件为目录文件则打开该目录
			getFileDir(filePaths.get(position).fullPath);
		} else {//如果该文件是一个普通文件则修改该条中选择框的状态，即选中该文件或取消选中
			CheckBox checkBox = (CheckBox)itemView.findViewById(R.id.file_selected);
			checkBox.setChecked(!checkBox.isChecked());//选择该文件或取消选择
			onCheck(checkBox);//传给onCheck方法继续处理
		}
	}
	
	//检查检测框架的状态，根据该状态来保存或删除文件信息
	public void onCheck(View fileSelectedCheckBox){
		CheckBox checkBox = (CheckBox)fileSelectedCheckBox;
		int fileIndex = (Integer)checkBox.getTag();//获得该检测框在文件列表中对应的序号，该序号与列表中该条目的序号一致
		Constant.fileSelectedState.put(fileIndex, checkBox.isChecked());
		if(checkBox.isChecked()){//如果是被选中则保存该序号对应的文件信息
			FileStyle file = filePaths.get(fileIndex);
			if(!selectedFilePath.contains(file))selectedFilePath.add(filePaths.get(fileIndex));
		}else{//如果取消选中则从保存的文件信息中删除该序号对应的文件信息
			selectedFilePath.remove(filePaths.get(fileIndex));
		}
	}
}
