package cn.newer.busniess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * 文件操作工具类
 * 
 * @author wtao
 * 
 */
public class CustomOperatFile {

	/**
	 * 新建文件
	 * 
	 * @param currentPath
	 *            路径
	 * @param filename
	 *            文件名
	 * @return 是否成功
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
	 * 新建目录
	 * 
	 * @param currentPath
	 *            路径
	 * @param foldername
	 *            目录名
	 */
	public static void createFolder(File currentPath, String foldername) {
		File folder = new File(currentPath, foldername);
		folder.mkdirs();
	}

	/**
	 * 删除文件(或目录)
	 * 
	 * @param file
	 *            要删除的文件
	 */
	public static void rm(File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			// 获得目录中的内容
			File[] files = file.listFiles();

			// 删除目录中的内容
			for (File f : files) {
				if (f.isFile()) {
					f.delete();
				} else {
					// 递归
					rm(f);
				}
			}

			// 删除该目录
			file.delete();
		}
	}

	/**
	 * 重命名
	 * 
	 * @param file
	 *            当前文件
	 * @param newPath
	 *            新的文件
	 * @return 是否成功
	 */
	public static boolean rename(File file, File newPath) {
		if (!newPath.exists()) {
			return file.renameTo(newPath);
		} else {
			return false;
		}
	}

	/**
	 * 复制文件或目录
	 * 
	 * @param src
	 *            复制源（文件或目录）
	 * @param destDir
	 *            目标路径
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
						// 复制文件
						cpFile(file, new File(dest, file.getName()));
					} else {
						// 递归复制目录
						cp(file, dest);
					}
				}
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param srcFile
	 *            源文件
	 * @param destFile
	 *            目标文件
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
