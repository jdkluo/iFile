package cn.newer.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * �����ݽ���һ������
 * @author Administrator
 *
 */
public class FileSort {
	
	public static File[] sortFile(File[] files) {

		List<File> listfile = Arrays.asList(files);
		Collections.sort(listfile, new CustomComparator());   //����ָ���Ĺ������һ������

		File[] array = listfile.toArray(new File[listfile.size()]); 

		return array;

	}


	

}
