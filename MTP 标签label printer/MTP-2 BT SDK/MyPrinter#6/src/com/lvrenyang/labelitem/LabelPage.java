package com.lvrenyang.labelitem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

import com.lvrenyang.label.Label1;

/**
 * 每一页有他自己的函数
 * 
 * @author lvrenyang
 * 
 */
public class LabelPage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3010483527817842071L;

	/**
	 * 页面起始点x坐标
	 */
	public int startx;

	/**
	 * 页面起始点y坐标
	 */
	public int starty;

	/**
	 * 页宽 0-384
	 */
	public int width;

	/**
	 * 页高 0-936
	 */
	public int height;

	/**
	 * 是否旋转
	 */
	public int rotate;

	private int CUR_ID = 100;

	/**
	 * 所有的元素，都在这个容器里保存着。 索引是一直增加的，如果需要交换上下位置，那么id不变，把内容改变。
	 */
	public ArrayList<LabelItem> items = new ArrayList<LabelItem>(10);

	public LabelPage(int startx, int starty, int width, int height, int rotate) {
		this.startx = startx;
		this.starty = starty;
		this.width = width;
		this.height = height;
		this.rotate = rotate;
	}

	/**
	 * 开始一页标签，根据设定的起点坐标和页面宽高。
	 */
	public void PageBegin() {
		Label1.PageBegin(startx, starty, width, height, rotate);
	}

	/**
	 * 依次对items里面的所有元素，调用write。
	 */
	public void PageFill() {
		for (LabelItem item : items) {
			item.Write();
		}
	}

	/**
	 * 标识一个标签页的结束
	 */
	public void PageEnd() {
		Label1.PageEnd();
	}

	/**
	 * 打印标签页
	 * 
	 * @param num
	 */
	public void PagePrint(int num) {
		Label1.PagePrint(num);
	}

	public LabelItem findItemById(int id) {
		for (LabelItem item : items) {
			if (id == item.id)
				return item;
		}
		return null;
	}

	public void removeItemById(int id) {
		for (LabelItem item : items) {
			if (id == item.id) {
				items.remove(item);
				return;
			}
		}
	}

	// 每一个LabelPage，里面的所有的item id必须是唯一的。
	public int getNextId() {
		return CUR_ID++;
	}

	/**
	 * 序列化对Bitmap不适用，这里使用开源的fastjson。 但是开源fastjson也不能序列化Bitmap，所以自定义一个Bitmap。
	 * 
	 * @param label
	 * @param file
	 */

	public static void SaveToFile(LabelPage label, String file) {
		/*
		 * try { String jsonString = JSON.toJSONString(label);
		 * FileUtils.SaveToFile(jsonString, file); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */

		FileOutputStream fos;
		ObjectOutputStream oos;

		try {

			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(label);
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static LabelPage ReadFromFile(String file) {
		/*
		 * try { String jsonString = FileUtils.ReadToString(file); LabelPage
		 * label = JSON.parseObject(jsonString, LabelPage.class); return label;
		 * } catch (Exception e) { e.printStackTrace(); return null; }
		 */

		LabelPage label = null;
		FileInputStream fis;
		ObjectInputStream ois;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			label = (LabelPage) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return label;
	}

	public static LabelPage ReadFromAssets(String file, Context context) {
		LabelPage label = null;
		InputStream is;
		ObjectInputStream ois;

		try {
			is = context.getAssets().open(file);
			ois = new ObjectInputStream(is);
			label = (LabelPage) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return label;
	}

}
