package cn.newer.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;


/**
 * �����ļ��Ĺ�����
 * @author Administrator
 *
 */
public class CustomFileFilter implements FileFilter {

	@Override
	public boolean accept(File dirName) {
		// TODO Auto-generated method stub
		if(!dirName.getName().startsWith(".")){
		    return true ;
		}else{
			return false;
		}
		
	}

        

}
