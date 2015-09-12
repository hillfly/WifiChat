package hillfly.wifichat.file;

import java.io.File;
import java.io.Serializable;

/**
 * 
 * @author george
 *         该类用来保存文件路径和文件名，同时实现Comparable接口，根据type的值来进行排序，(调用方法compareto、Collections
 *         .sort) type=1代表当前存的是目录信息 type=2代表当前存的是文件信息
 *         根据type的值从小到大排例，这样文件夹均被排在前面，文件排在后面
 */
public class FileStyle implements Comparable<FileStyle>, Serializable {
    private static final long serialVersionUID = 1L;
    public int type = 0;
    public String fullPath = "";
    public long size = -1;
    public boolean isDirectory = true;

    public FileStyle() {
    }

    public FileStyle(int type, String fullPath) {
        this.type = type;
        this.fullPath = fullPath;
    }

    public FileStyle(int type, String fileName, long size, boolean isDirectory) {
        this.type = type;
        this.fullPath = fileName;
        this.size = size;
        this.isDirectory = isDirectory;
    }

    public String getFileName() {
        int index = fullPath.lastIndexOf(File.separator);
        return fullPath.substring(index + 1);
    }

    public String getFullPath() {
        return fullPath;
    }

    @Override
    public int compareTo(FileStyle another) {
        // TODO Auto-generated method stub
        int result = -2;
        if (type < another.type)
            result = -1;
        if (type == another.type)
            result = 0;
        if (type > another.type)
            result = 1;
        return result;
    }

    public int hashCode() {
        int result = 56;
        result = 56 * result + type;
        result = 56 * result + fullPath.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        if (!(o instanceof FileStyle)) // 如果o是Null或者不是FileStyle或其子类的实例，则返回fasle
            return false;
        FileStyle another = (FileStyle) o;
        return (type == another.type) && (fullPath.equals(another.fullPath));
    }
}
