package com.lvrenyang.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

import android.content.Context;
import android.os.Environment;

public class FileUtils {

	public static final String external_sd_dump_txt = Environment
			.getExternalStorageDirectory() + File.separator + "dump.txt";
	public static final String sdcard_dump_txt = "sdcard" + File.separator
			+ "dump.txt";

	public static boolean debug = false;

	public static void DebugAddToFile(byte[] buffer, int byteOffset,
			int byteCount, String dumpfile) {
		if (debug)
			AddToFile(buffer, byteOffset, byteCount, dumpfile);
	}

	public static void DebugAddToFile(String text, String dumpfile) {
		if (debug)
			AddToFile(text, dumpfile);
	}

	public static void DebugSaveToFile(String text, String dumpfile) {
		if (debug)
			SaveToFile(text, dumpfile);
	}

	/**
	 * 将数据存到文件
	 */
	public static void AddToFile(byte[] buffer, int byteOffset, int byteCount,
			String dumpfile) {
		if (null == dumpfile)
			return;
		if (null == buffer)
			return;
		if (byteOffset < 0 || byteCount <= 0)
			return;

		try {
			File file = new File(dumpfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(buffer, byteOffset, byteCount);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void AddToFile(String text, String dumpfile) {
		if (null == dumpfile)
			return;
		if (null == text)
			return;
		if ("".equals(text))
			return;

		try {
			File file = new File(dumpfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(text.getBytes());
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void SaveToFile(String text, String dumpfile) {
		if (null == dumpfile)
			return;
		if (null == text)
			return;

		try {
			File file = new File(dumpfile);
			if (file.exists())
				file.delete();

			file.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(0);
			raf.write(text.getBytes());
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String ReadToString(String filePathName) {

		File file = new File(filePathName);
		if (!file.exists())
			return null;

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			return new String(filecontent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] ReadToMem(String filePathName) {
		File file = new File(filePathName);
		if (!file.exists())
			return null;

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			return filecontent;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> void SaveToFile(T t, String file, Context context) {

		FileOutputStream fos;
		ObjectOutputStream oos;

		try {
			fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(t);
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> void SaveToSD(T t, String file) {

		FileOutputStream fos;
		ObjectOutputStream oos;

		try {
			File dir = new File(file).getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}

			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(t);
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T ReadFromFile(String file, Context context) {

		T t = null;
		FileInputStream fis;
		ObjectInputStream ois;

		try {
			fis = context.openFileInput(file);
			ois = new ObjectInputStream(fis);
			t = (T) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}

	@SuppressWarnings("unchecked")
	public static <T> T ReadFromSD(String file, Context context) {

		T t = null;
		FileInputStream fis;
		ObjectInputStream ois;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			t = (T) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}
}
