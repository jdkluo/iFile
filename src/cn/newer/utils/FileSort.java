package cn.newer.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 将数据进行一个排序
 * @author Administrator
 *
 */
public class FileSort {
	
	public static File[] sortFile(File[] files) {

		List<File> listfile = Arrays.asList(files);
		Collections.sort(listfile, new CustomComparator());   //按照指定的规则进行一个排序

		File[] array = listfile.toArray(new File[listfile.size()]); 

		return array;

	}


	

}
