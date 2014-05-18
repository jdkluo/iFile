package cn.newer.busniess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * �ļ�����������
 * 
 * @author wtao
 * 
 */
public class CustomOperatFile {

	/**
	 * �½��ļ�
	 * 
	 * @param currentPath
	 *            ·��
	 * @param filename
	 *            �ļ���
	 * @return �Ƿ�ɹ�
	 * @throws IOException
	 */
	public static boolean createFile(File currentPath, String filename)
			throws IOException {

		File file = new File(currentPath, filename);

		if (file.exists()) {
			return false;
		} else {
			return file.createNewFile();
		}
		
		
//		Collections.sort(list, comparator)
	}

	/**
	 * �½�Ŀ¼
	 * 
	 * @param currentPath
	 *            ·��
	 * @param foldername
	 *            Ŀ¼��
	 */
	public static void createFolder(File currentPath, String foldername) {
		File folder = new File(currentPath, foldername);
		folder.mkdirs();
	}

	/**
	 * ɾ���ļ�(��Ŀ¼)
	 * 
	 * @param file
	 *            Ҫɾ�����ļ�
	 */
	public static void rm(File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			// ���Ŀ¼�е�����
			File[] files = file.listFiles();

			// ɾ��Ŀ¼�е�����
			for (File f : files) {
				if (f.isFile()) {
					f.delete();
				} else {
					// �ݹ�
					rm(f);
				}
			}

			// ɾ����Ŀ¼
			file.delete();
		}
	}

	/**
	 * ������
	 * 
	 * @param file
	 *            ��ǰ�ļ�
	 * @param newPath
	 *            �µ��ļ�
	 * @return �Ƿ�ɹ�
	 */
	public static boolean rename(File file, File newPath) {
		if (!newPath.exists()) {
			return file.renameTo(newPath);
		} else {
			return false;
		}
	}

	/**
	 * �����ļ���Ŀ¼
	 * 
	 * @param src
	 *            ����Դ���ļ���Ŀ¼��
	 * @param destDir
	 *            Ŀ��·��
	 * @throws IOException
	 */
	public static void cp(File src, File destDir) throws IOException {
		if (src.isFile()) {
			cpFile(src, new File(destDir, src.getName()));
		} else {
			File dest = new File(destDir, src.getName());
			if (!dest.exists()) {
				dest.mkdir();
				File[] files = src.listFiles();
				for (File file : files) {
					if (file.isFile()) {
						// �����ļ�
						cpFile(file, new File(dest, file.getName()));
					} else {
						// �ݹ鸴��Ŀ¼
						cp(file, dest);
					}
				}
			}
		}
	}

	/**
	 * �����ļ�
	 * 
	 * @param srcFile
	 *            Դ�ļ�
	 * @param destFile
	 *            Ŀ���ļ�
	 * @throws IOException
	 */
	private static void cpFile(File srcFile, File destFile) throws IOException {
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(destFile);

		byte[] buf = new byte[1024 * 16];
		int len;
		while (-1 != (len = in.read(buf))) {
			out.write(buf, 0, len);
		}
		out.flush();
		in.close();
		out.close();
	}

}
