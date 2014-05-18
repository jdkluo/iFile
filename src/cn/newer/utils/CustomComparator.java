package cn.newer.utils;

import java.io.File;
import java.util.Comparator;

public class CustomComparator implements Comparator<File>{

	@Override
	public int compare(File pFile1, File pFile2) {
		/*
		 * 1.�ȱȽ��ļ��� ���ļ������ļ���˳��֮�ϣ�2.��A-Z���ֵ�����3.�Ƚ��ļ��к��ļ�4.�Ƚ��ļ����ļ���
		 */

		if (pFile1.isDirectory() && pFile2.isDirectory()) {
			return pFile1.getName().compareToIgnoreCase(pFile2.getName());

		} else {

			if (pFile1.isDirectory() && pFile2.isFile()) {
				return -1;
			} else if (pFile1.isFile() && pFile2.isDirectory()) {
				return 1;
			} else {
				return pFile1.getName().compareToIgnoreCase(pFile2.getName());
			}

		}

	}


}
