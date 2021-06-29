/**
 * 一些函数的命名。
 * ShowDialogAdd*** 当用户点击确认之后，才会将Item添加到Page
 * ShowDialogModify*** 在已有的Item基础上进行修改。
 */

package com.lvrenyang.mylabelactivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.lvrenyang.label.*;

import com.lvrenyang.barcode.Barcode;
import com.lvrenyang.barcode.Barcode.BarcodeType;
import com.lvrenyang.barcode.I25Barcode;
import com.lvrenyang.barcode.UPCABarcode;
import com.lvrenyang.barcode.UPCEBarcode;
import com.lvrenyang.labelitem.Image32;
import com.lvrenyang.labelitem.ItemBarcode;
import com.lvrenyang.labelitem.ItemBitmap;
import com.lvrenyang.labelitem.ItemBox;
import com.lvrenyang.labelitem.ItemCode128;
import com.lvrenyang.labelitem.ItemGrid;
import com.lvrenyang.labelitem.ItemHardtext;
import com.lvrenyang.labelitem.ItemLine;
import com.lvrenyang.labelitem.ItemPDF417;
import com.lvrenyang.labelitem.ItemPlaintext;
import com.lvrenyang.labelitem.ItemQrcode;
import com.lvrenyang.labelitem.ItemRect;
import com.lvrenyang.labelitem.LabelItem;
import com.lvrenyang.labelitem.LabelItem.LabelItemType;
import com.lvrenyang.labelitem.LabelPage;
import com.lvrenyang.labelview.HVScrollView;
import com.lvrenyang.labelview.HVScrollView.OnScrollChangedListener;
import com.lvrenyang.labelview.LabelLayout;
import com.lvrenyang.mylabelwork.Global;
import com.lvrenyang.mylabelwork.R;
import com.lvrenyang.mylabelwork.WorkService;
import com.lvrenyang.photocropper.CropHandler;
import com.lvrenyang.photocropper.CropHelper;
import com.lvrenyang.photocropper.CropParams;
import com.lvrenyang.pos.ImageProcessing;
import com.lvrenyang.pos.Pos;
import com.lvrenyang.utils.Language;
import com.lvrenyang.utils.StringUtils;
import com.lvrenyang.utils.UIUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

// 添加一个项目，需要更新坐标。
public class ActivityLable extends Activity implements OnClickListener,
		OnTouchListener, OnFocusChangeListener, CropHandler {

	private static final String TAG = "AdvanceLabel";
	private static final String DIR_MAIN = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "LabelPrinter"
			+ File.separator;
	private static final String DIR_FONTS = DIR_MAIN + "Fonts" + File.separator;
	private static Context mContext;
	private static Handler mHandler = null;

	private static final int MSG_SETLABELPAGETOLAYOUT = 999;

	CropParams mCropParams = new CropParams();

	private static final int REQUEST_CODE_OPENTEMPLATE = 4;
	private static final String EXTRA_FILENAME = "extra.filename";

	ImageButton ibReturn, ibPrint;
	LinearLayout llPicture, llPlainText, llHardText, llBarcode, llQRCode,
			llPDF417, llBox, llLine, llRect, llGrid, llNew, llOpen, llSave,
			llPaper, llSetting;
	RelativeLayout rlPanel;
	LinearLayout llChange, llDelete;
	ToggleButton toggleButtonItemLock, toggleButtonPageLock;
	ScrollView verticalRuler;
	HorizontalScrollView horizontalRuler;
	HVScrollView hvScrollView;
	LinearLayout llFocus;

	int screenWidth, screenHeight;
	int paperWidth, paperHeight;

	LabelLayout mLabelLayout;

	LabelPage mLabelPage = new LabelPage(0, 0, 0, 0, 0);
	LabelItem mItem; // 当前正在处理的item
	int mode = 0; // 打印模式，是按图片打印还是按照标签指令打印。这里按照标签指令打印。

	int x, y;
	boolean moved;
	GestureDetector mGesture = null;

	private static final String strPleaseConnectPrinter[] = {
			"Please connect printer", "请先连接打印机" };
	private static final String strSetPaper[] = { "Set paper page", "设置纸张页面" };
	private static final String strOK[] = { "OK", "确定" };
	private static final String strCancel[] = { "Cancel", "取消" };
	private static final String strPictureOrigin[] = { "Picture origin", "图片来源" };
	private static final String strPhotography[] = { "Photography", "拍照" };
	private static final String strAlbum[] = { "Album", "相册" };
	private static final String strReadTemplateError[] = {
			"Read template error", "读取模板错误" };
	private static final String strSetPictureWidth[] = { "Set picture width",
			"设定图片宽度" };
	private static final String strInputText[] = { "Please input text", "请输入文本" };
	private static final String strNoEmpty[] = { "Cannot be empty", "不能为空" };
	private static final String strSetTextFormat[] = { "Set text format",
			"设置文本格式" };
	private static final String strInputBarcodeContent[] = {
			"Set barcode content", "输入条码文本" };
	private static final String strSetBarcodeFormat[] = { "Set barcode format",
			"设置条码格式" };
	private static final String strBarcodeTypeNotSupport[] = {
			"Barcode type not support", "条码类型不支持" };
	private static final String strCannotEncode[] = { "Cannot encode", "无法编码" };
	private static final String strChecksumError[] = { "Checksum error", "校验错误" };
	private static final String strCannotGenerateBarcode[] = {
			"Cannot generate barcode", "无法生成条码" };
	private static final String strInputQRCodeContent[] = {
			"Set qrcode content", "输入二维码文本" };
	private static final String strTextTooLong[] = { "Text too long", "文本太长" };
	private static final String strInputPDF417Content[] = {
			"Set pdf417 content", "输入PDF417码文本" };
	private static final String strSetBoxFormat[] = { "Set box format",
			"设置方框格式" };
	private static final String strCannotBeZero[] = { "Cannot be zero", "不能为零" };
	private static final String strSetRectFormat[] = { "Set rect format",
			"设置矩形格式" };
	private static final String strSetLineFormat[] = { "Set line format",
			"设置矩形格式" };
	private static final String strInputInteger[] = { "Please input integer",
			"请输入整数" };
	private static final String strInputFileName[] = { "Please input filename",
			"请输入文件名" };
	private static final String strFailed[] = { "Failed", "失败" };
	private static final String strDone[] = { "Done", "成功" };
	private static final String strSettings[] = { "Settings", "设置" };

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_label_advanced);

		mContext = this;

		ibReturn = (ImageButton) findViewById(R.id.imageButtonEditCaptionReturn);
		ibPrint = (ImageButton) findViewById(R.id.imageButtonEditCaptionPrint);

		llPicture = (LinearLayout) findViewById(R.id.linearLayoutEditInsertPicture);
		llPlainText = (LinearLayout) findViewById(R.id.linearLayoutEditInsertPlaintext);
		llHardText = (LinearLayout) findViewById(R.id.linearLayoutEditInsertHardtext);
		llBarcode = (LinearLayout) findViewById(R.id.linearLayoutEditInsertBarcode);
		llQRCode = (LinearLayout) findViewById(R.id.linearLayoutEditInsertQRCode);
		llPDF417 = (LinearLayout) findViewById(R.id.linearLayoutEditInsertPDF417);
		llBox = (LinearLayout) findViewById(R.id.linearLayoutEditInsertBox);
		llLine = (LinearLayout) findViewById(R.id.linearLayoutEditInsertLine);
		llRect = (LinearLayout) findViewById(R.id.linearLayoutEditInsertRectangle);
		llGrid = (LinearLayout) findViewById(R.id.linearLayoutEditInsertGrid);

		llNew = (LinearLayout) findViewById(R.id.linearLayoutFileNew);
		llOpen = (LinearLayout) findViewById(R.id.linearLayoutFileOpen);
		llSave = (LinearLayout) findViewById(R.id.linearLayoutFileSave);
		llPaper = (LinearLayout) findViewById(R.id.linearLayoutFilePaperSet);
		llSetting = (LinearLayout) findViewById(R.id.linearLayoutFileSettings);

		rlPanel = (RelativeLayout) findViewById(R.id.relativeLayoutPanel);
		llChange = (LinearLayout) findViewById(R.id.linearLayoutEditItemMenuChange);
		llDelete = (LinearLayout) findViewById(R.id.linearLayoutEditItemMenuDelete);
		toggleButtonItemLock = (ToggleButton) findViewById(R.id.toggleButtonEditItemMenuLock);
		toggleButtonPageLock = (ToggleButton) findViewById(R.id.toggleButtonPageLock);

		verticalRuler = (ScrollView) findViewById(R.id.verticalruler);
		horizontalRuler = (HorizontalScrollView) findViewById(R.id.horizontalruler);
		hvScrollView = (HVScrollView) findViewById(R.id.hvScrollView);

		llFocus = (LinearLayout) findViewById(R.id.linearLayoutFocus);
		mLabelLayout = (LabelLayout) findViewById(R.id.labelLayoutLabelPage);
		// 添加事件

		ibReturn.setOnClickListener(this);
		ibPrint.setOnClickListener(this);
		llPicture.setOnClickListener(this);
		llPlainText.setOnClickListener(this);
		llHardText.setOnClickListener(this);
		llBarcode.setOnClickListener(this);
		llQRCode.setOnClickListener(this);
		llPDF417.setOnClickListener(this);
		llBox.setOnClickListener(this);
		llLine.setOnClickListener(this);
		llRect.setOnClickListener(this);
		llGrid.setOnClickListener(this);
		llNew.setOnClickListener(this);
		llOpen.setOnClickListener(this);
		llSave.setOnClickListener(this);
		llPaper.setOnClickListener(this);
		llSetting.setOnClickListener(this);
		llChange.setOnClickListener(this);
		llDelete.setOnClickListener(this);
		toggleButtonItemLock.setOnClickListener(this);

		mLabelLayout.setWillNotDraw(false);
		llFocus.setClickable(true);
		llFocus.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS); // 事件分发之前，就获得焦点
		llFocus.setOnTouchListener(this);
		hvScrollView.setOnScrollChangedListener(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				// TODO Auto-generated method stub
				horizontalRuler.scrollBy(l - oldl, 0);
				verticalRuler.scrollBy(0, t - oldt);
			}
		});

		WindowManager wm = this.getWindowManager();
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();

		SetPaperSize(384, 400);

		// 建立目录
		File folder = new File(DIR_FONTS);
		if (!folder.exists())
			folder.mkdirs();

		mGesture = new GestureDetector(this, new GestureListener());

		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (this.getCropParams() != null)
			CropHelper.clearCachedCropFile(this.getCropParams().uri);

		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
	}

	// UI事件有 触摸，移动，单击，焦点等。所有的第一步，都在触摸。
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		// 如果是触摸LabelLayout，就直接返回false，不处理，交给上面处理。
		if (v == llFocus) {
			if (null != mItem) {
				View view = llFocus.findViewById(mItem.id);
				if (null != view)
					view.clearFocus();
				mItem = null;
				rlPanel.setVisibility(View.INVISIBLE);
			}
			return false;
		}

		if (!toggleButtonPageLock.isChecked()) { // 整个屏幕没有锁定

			LabelItem item = mLabelPage.findItemById(v.getId());
			// 处理移动和手势
			if ((null != item) && (!item.lock)) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					x = (int) event.getRawX();
					y = (int) event.getRawY();
					moved = false;
					if (null != item) // 如果这个view是mLabelLayout里面添加的view，就自己消费掉。
					{
						ViewParent parent = v.getParent();
						if (null != parent)
							parent.requestDisallowInterceptTouchEvent(true);
					}

					break;

				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - x;
					int dy = (int) event.getRawY() - y;

					int t = v.getTop() + dy;

					int l = v.getLeft() + dx;

					if (l <= 0) {
						l = 0;
					}
					if (t <= 0) {
						t = 0;
					}
					if (t >= mLabelLayout.getHeight() - v.getHeight()) {
						t = mLabelLayout.getHeight() - v.getHeight();
					}
					if (l >= mLabelLayout.getWidth() - v.getWidth()) {
						l = mLabelLayout.getWidth() - v.getWidth();
					}
					int b = t + v.getHeight();
					int r = l + v.getWidth();

					// 拖动
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v
							.getLayoutParams();
					params.alignWithParent = true;
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					params.leftMargin = l;
					params.topMargin = t;
					v.setLayoutParams(params);

					// 更新坐标
					if (null != item) {
						if (LabelItemType.BITMAP == item.type) {
							ItemBitmap itemBitmap = (ItemBitmap) item;
							itemBitmap.startx = l;
							itemBitmap.starty = t;
						} else if (LabelItemType.PLAINTEXT == item.type) {
							ItemPlaintext itemPlaintext = (ItemPlaintext) item;
							itemPlaintext.startx = l;
							itemPlaintext.starty = t;
						} else if (LabelItemType.HARDTEXT == item.type) {
							ItemHardtext itemHardtext = (ItemHardtext) item;
							itemHardtext.startx = l;
							itemHardtext.starty = t;
						} else if (LabelItemType.BARCODE == item.type) {
							ItemBarcode itemBarcode = (ItemBarcode) item;
							itemBarcode.startx = l;
							itemBarcode.starty = t;
						} else if (LabelItemType.QRCODE == item.type) {
							ItemQrcode itemQrcode = (ItemQrcode) item;
							itemQrcode.startx = l;
							itemQrcode.starty = t;
						} else if (LabelItemType.PDF417 == item.type) {
							ItemPDF417 itemPDF417 = (ItemPDF417) item;
							itemPDF417.startx = l;
							itemPDF417.starty = t;
						} else if (LabelItemType.BOX == item.type) {
							ItemBox itemBox = (ItemBox) item;
							itemBox.startx = l;
							itemBox.starty = t;
						} else if (LabelItemType.RECTANGLE == item.type) {
							ItemRect itemRect = (ItemRect) item;
							itemRect.startx = l;
							itemRect.starty = t;
						} else if (LabelItemType.LINE == item.type) {
							ItemLine itemLine = (ItemLine) item;
							if (itemLine.startx > itemLine.stopx) {
								itemLine.startx = r - itemLine.getPadding();
								itemLine.stopx = l + itemLine.getPadding();
							} else {
								itemLine.stopx = r - itemLine.getPadding();
								itemLine.startx = l + itemLine.getPadding();
							}
							if (itemLine.starty > itemLine.stopy) {
								itemLine.starty = b - itemLine.getPadding();
								itemLine.stopy = t + itemLine.getPadding();
							} else {
								itemLine.stopy = b - itemLine.getPadding();
								itemLine.starty = t + itemLine.getPadding();
							}
						} else if (LabelItemType.BARCODEV2 == item.type) {
							// BARCODEV2无法显示出来
						} else if (LabelItemType.MYCODE128 == item.type) {
							ItemCode128 itemCode128 = (ItemCode128) item;
							itemCode128.startx = l;
							itemCode128.starty = t;
						} else if (LabelItemType.GRID == item.type) {
							ItemGrid itemGrid = (ItemGrid) item;
							itemGrid.startx = l;
							itemGrid.starty = t;
						}
					}

					x = (int) event.getRawX();
					y = (int) event.getRawY();
					if ((Math.abs(dx) >= 5) || (Math.abs(dy) >= 5)) {
						moved = true;
					}
					mLabelLayout.invalidate();
					break;
				case MotionEvent.ACTION_UP:
					if (!moved) {
						v.performClick();
					}
					mLabelLayout.invalidate();
					if (null != item) {
						ViewParent parent = v.getParent();
						if (null != parent)
							parent.requestDisallowInterceptTouchEvent(false);
					}
					break;
				}
				return mGesture.onTouchEvent(event);
			}
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.imageButtonEditCaptionReturn: {
			finish();
			break;
		}

		case R.id.imageButtonEditCaptionPrint: {
			// 有可能编辑完字体，就按了打印，导致来不及更新。这里让layout获取焦点来避免这种情况。
			if (null != mItem) {
				if (mItem.type == LabelItemType.PLAINTEXT)
					findViewById(mItem.id).clearFocus();
			}

			if (WorkService.workThread.isConnected()) {
				// 标签模式
				if (mode == 0) {
					Bundle data = new Bundle();
					data.putSerializable(Global.PARCE1, mLabelPage);
					data.putInt(Global.INTPARA1, 1);
					WorkService.workThread.handleCmd(
							Global.CMD_LABEL_PAGEFINISH, data);

					UpdateAutoComplete();

				} else {
					// 图片模式
					Bitmap mBitmap = UIUtils.ViewToBitmap(mLabelLayout);
					if (null == mBitmap) {
						Toast.makeText(mContext, "生成失败，页面是否过大？",
								Toast.LENGTH_LONG).show();
						break;
					}
					if (mLabelPage.rotate != 0)
						mBitmap = ImageProcessing.adjustPhotoRotation(mBitmap,
								90);
					Bundle data = new Bundle();
					data.putParcelable(Global.PARCE1, mBitmap);
					data.putInt(Global.INTPARA1, mBitmap.getWidth());
					data.putInt(Global.INTPARA2, 0);
					WorkService.workThread.handleCmd(
							Global.CMD_POS_PRINTBWPICTURE, data);
				}
			} else {
				Toast.makeText(this,
						strPleaseConnectPrinter[Language.GetIndex()],
						Toast.LENGTH_SHORT).show();
			}
			break;
		}

		case R.id.linearLayoutEditInsertPicture: {

			// 从相册或照相机截图，并处理成黑白图片，如果图片处理正确，就会添加ItemBitmap到mLabelLayout里面
			ShowDialogAddPhoto();
			break;
		}

		case R.id.linearLayoutEditInsertPlaintext: {
			ShowDialogAddPlaintext();
			break;
		}

		case R.id.linearLayoutEditInsertHardtext: {
			// 提示一个对话框，让用户输入文字。再按下确认之后，才把ItemText添加到mLabelLayout里面。
			ShowDialogAddHardtext();
			break;
		}

		case R.id.linearLayoutEditInsertBarcode: {
			// ShowDialogAddBarcode();
			ShowDialogAddCode128();
			break;
		}

		case R.id.linearLayoutEditInsertQRCode: {
			ShowDialogAddQrcode();
			break;
		}

		case R.id.linearLayoutEditInsertPDF417: {
			ShowDialogAddPDF417();
			break;
		}

		case R.id.linearLayoutEditInsertBox: {
			ShowDialogAddBox();
			break;
		}

		case R.id.linearLayoutEditInsertRectangle: {
			ShowDialogAddRect();
			break;
		}

		case R.id.linearLayoutEditInsertLine: {
			ShowDialogAddLine();
			break;
		}

		case R.id.linearLayoutEditInsertGrid: {
			ShowDialogAddGrid();
			break;
		}

		case R.id.linearLayoutEditItemMenuChange: {
			if (null == mItem)
				break;

			if (LabelItemType.BITMAP == mItem.type) {
				ShowDialogModifyItemBitmap((ItemBitmap) mItem);
			} else if (LabelItemType.PLAINTEXT == mItem.type) {
				ShowDialogModifyItemPlaintext((ItemPlaintext) mItem);
			} else if (LabelItemType.HARDTEXT == mItem.type) {
				ShowDialogModifyItemHardtext((ItemHardtext) mItem);
			} else if (LabelItemType.BARCODE == mItem.type) {
				ShowDialogModifyItemBarcode((ItemBarcode) mItem);
			} else if (LabelItemType.QRCODE == mItem.type) {
				ShowDialogModifyItemQrcode((ItemQrcode) mItem);
			} else if (LabelItemType.PDF417 == mItem.type) {
				ShowDialogModifyItemPDF417((ItemPDF417) mItem);
			} else if (LabelItemType.BOX == mItem.type) {
				ShowDialogModifyItemBox((ItemBox) mItem);
			} else if (LabelItemType.RECTANGLE == mItem.type) {
				ShowDialogModifyItemRect((ItemRect) mItem);
			} else if (LabelItemType.LINE == mItem.type) {
				ShowDialogModifyItemLine((ItemLine) mItem);
			} else if (LabelItemType.MYCODE128 == mItem.type) {
				ShowDialogModifyItemCode128((ItemCode128) mItem);
			} else if (LabelItemType.GRID == mItem.type) {
				ShowDialogModifyItemGrid((ItemGrid) mItem);
			}

			break;
		}

		case R.id.linearLayoutEditItemMenuDelete: {
			if (null == mItem)
				break;
			// 销毁顺序很重要，不然会有bug
			int id = mItem.id;
			mItem = null;
			mLabelPage.removeItemById(id);
			mLabelLayout.removeView(mLabelLayout.findViewById(id));
			llFocus.requestFocus();
			break;
		}

		case R.id.toggleButtonEditItemMenuLock: {
			if (null == mItem)
				break;
			mItem.lock = toggleButtonItemLock.isChecked();
			break;
		}

		// 从模板创建，或者创建空白布局
		case R.id.linearLayoutFileNew: {
			ShowDialogNew();
			break;
		}

		case R.id.linearLayoutFileOpen: {
			Intent intent = new Intent(this, FileManager.class);
			intent.putExtra(FileManager.EXTRA_INITIAL_DIRECTORY, DIR_MAIN);
			startActivityForResult(intent, REQUEST_CODE_OPENTEMPLATE);
			break;
		}

		case R.id.linearLayoutFileSave: {
			ShowDialogSaveTemplate();
			break;
		}

		case R.id.linearLayoutFilePaperSet: {
			ShowDialogModifyPaperSize();
			break;
		}

		case R.id.linearLayoutFileSettings: {
			ShowDialogSettings();
			break;
		}

		default: {
			break;
		}

		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

		if (hasFocus) {

			LabelItem item = mLabelPage.findItemById(v.getId());

			if (null != item) {
				mItem = item;
				toggleButtonItemLock.setChecked(mItem.lock);
				rlPanel.setVisibility(View.VISIBLE);
			}

		} else {

			LabelItem item = mLabelPage.findItemById(v.getId());

			// 处理edittext 处理Panel
			if (null != item) {

				if (LabelItemType.PLAINTEXT == item.type) {
					ItemPlaintext itemPlaintext = (ItemPlaintext) item;
					EditText et = (EditText) v;
					Log.i(TAG, "InputType:" + et.getInputType());
					et.setInputType(InputType.TYPE_NULL);
					et.setSingleLine(false); //
					Bitmap bitmap = convertToBitmap(et);
					if (null != bitmap) {
						itemPlaintext.strText = et.getText().toString();
						itemPlaintext.image = new Image32();
						itemPlaintext.image.setBitmap(bitmap);
						// Pos.saveMyBitmap(bitmap, "PlainText.bmp");
					}
				}

				mItem = null; //
				rlPanel.setVisibility(View.INVISIBLE);
			}

		}

		Log.i(TAG, "View id: " + v.getId() + " hasFocus:" + hasFocus);
	}

	private void ShowDialogNew() {
		// 加载布局
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		inflater.inflate(R.layout.dialog_edit_label_advanced_new, rl);

		// 初始化
		final LinearLayout llBlankCanvas = (LinearLayout) rl
				.findViewById(R.id.linearLayoutBlankCanvas);
		final ListView lvTemplate = (ListView) rl
				.findViewById(R.id.listViewTemplate);

		AssetManager assetManager = getAssets();
		String[] templates = null;
		try {
			templates = assetManager.list("template");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		final List<String> list = new ArrayList<String>();
		for (String template : templates)
			list.add(template);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lvTemplate.setAdapter(adapter);

		// 设置回调及显示对话框
		final AlertDialog dialog = new AlertDialog.Builder(rl.getContext())
				.setTitle(strSetPaper[Language.GetIndex()]).setView(rl)
				.create();

		// 不需要设置确定，取消按钮。只需要把按钮事件设置好
		llBlankCanvas.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLabelLayout.removeAllViews();
				mLabelPage.items.clear();
				dialog.dismiss();
			}
		});
		lvTemplate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mLabelLayout.removeAllViews();
				String file = "template" + File.separator + list.get(position);
				mLabelPage = LabelPage.ReadFromAssets(file, mContext);
				if (null == mLabelPage) {
					Toast.makeText(mContext,
							strReadTemplateError[Language.GetIndex()],
							Toast.LENGTH_LONG).show();
					return;
				}

				Message msg = new Message();
				msg.what = MSG_SETLABELPAGETOLAYOUT;
				mHandler.sendMessage(msg);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void ShowDialogModifyPaperSize() {
		// 加载布局
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		inflater.inflate(R.layout.dialog_edit_label_advanced_paperset, rl);

		// 初始化
		final EditText editTextPaperWidth = (EditText) rl
				.findViewById(R.id.editTextPaperWidth);
		final EditText editTextPaperHeight = (EditText) rl
				.findViewById(R.id.editTextPaperHeight);
		final RadioButton radioTurn0 = (RadioButton) rl
				.findViewById(R.id.radioTurn0);
		final RadioButton radioTurn90 = (RadioButton) rl
				.findViewById(R.id.radioTurn90);
		editTextPaperWidth.setText("" + paperWidth);
		editTextPaperHeight.setText("" + paperHeight);
		if (mLabelPage.rotate == 0)
			radioTurn0.setChecked(true);
		else
			radioTurn90.setChecked(true);

		// 设置回调及显示对话框
		AlertDialog dialog = new AlertDialog.Builder(rl.getContext())
				.setTitle(strSetPaper[Language.GetIndex()])
				.setView(rl)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String strWidth = editTextPaperWidth.getText()
										.toString();
								String strHeight = editTextPaperHeight
										.getText().toString();
								int width = 0;
								int height = 0;
								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
								} catch (Exception e) {

								}

								if (width < 0)
									width = 384;
								if (height < 0)
									height = 576;
								SetPaperSize(width, height);
								if (radioTurn0.isChecked())
									mLabelPage.rotate = 0;
								else
									mLabelPage.rotate = 1;
							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void SetPaperSize(int width, int height) {
		paperWidth = width;
		paperHeight = height;

		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLabelLayout
				.getLayoutParams();
		lp.width = paperWidth;
		lp.height = paperHeight;

		if (screenWidth > paperWidth) {
			// lp.leftMargin = (screenWidth - paperWidth) / 2;
			// lp.rightMargin = screenWidth - paperWidth - lp.leftMargin;
			// lp.topMargin = lp.bottomMargin = 60;
		} else {
			// lp.topMargin = lp.bottomMargin = lp.leftMargin = lp.rightMargin =
			// 60;

		}
		Log.i(TAG, "leftMargin:" + lp.leftMargin + " rightMargin:"
				+ lp.rightMargin);
		mLabelLayout.setLayoutParams(lp);

		mLabelPage.width = width;
		mLabelPage.height = height;
	}

	private void ShowDialogSettings() {
		// 加载布局
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		inflater.inflate(R.layout.dialog_edit_label_advanced_settings, rl);

		// 初始化
		final RadioButton radioButtonLabel = (RadioButton) rl
				.findViewById(R.id.radioModeLabel);
		final RadioButton radioButtonPicture = (RadioButton) rl
				.findViewById(R.id.radioModePicture);
		if (mode == 0)
			radioButtonLabel.setChecked(true);
		else
			radioButtonPicture.setChecked(true);

		// 设置回调及显示对话框
		AlertDialog dialog = new AlertDialog.Builder(rl.getContext())
				.setTitle(strSettings[Language.GetIndex()])
				.setView(rl)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (radioButtonLabel.isChecked())
									mode = 0;
								else
									mode = 1;
							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	/**
	 * 选择提示对话框
	 */
	private void ShowDialogAddPhoto() {
		// 加载布局
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		inflater.inflate(R.layout.dialog_edit_label_advanced_add_photo, rl);

		// 初始化
		final EditText editTextWidth = (EditText) rl
				.findViewById(R.id.editTextWidth);
		final EditText editTextHeight = (EditText) rl
				.findViewById(R.id.editTextHeight);

		new AlertDialog.Builder(this)
				.setTitle(strPictureOrigin[Language.GetIndex()])
				.setView(rl)
				.setNegativeButton(strAlbum[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String strWidth = editTextWidth.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();

								int width = 1;
								int height = 1;
								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
								} catch (Exception ex) {
									Toast.makeText(mContext, "请输入数字",
											Toast.LENGTH_LONG).show();
									return;
								}

								if (width <= 0 || height <= 0) {
									Toast.makeText(mContext, "宽和高需在1-5之间",
											Toast.LENGTH_LONG).show();
									return;
								}

								mCropParams.aspectX = width;
								mCropParams.aspectY = height;
								if (width > height) {
									mCropParams.outputX = 300;
									mCropParams.outputY = (int) (300.0 * height / width);
								} else {
									mCropParams.outputY = 300;
									mCropParams.outputX = (int) (300.0 * width / height);
								}

								dialog.dismiss();

								Intent intent = CropHelper
										.buildCropFromGalleryIntent(mCropParams);
								startActivityForResult(intent,
										CropHelper.REQUEST_CROP);
							}
						})
				.setPositiveButton(strPhotography[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String strWidth = editTextWidth.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();

								int width = 1;
								int height = 1;
								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
								} catch (Exception ex) {
									Toast.makeText(mContext, "请输入数字",
											Toast.LENGTH_LONG).show();
									return;
								}

								if (width <= 0 || height <= 0) {
									Toast.makeText(mContext, "宽和高需在1-5之间",
											Toast.LENGTH_LONG).show();
									return;
								}

								mCropParams.aspectX = width;
								mCropParams.aspectY = height;
								if (width > height) {
									mCropParams.outputX = 300;
									mCropParams.outputY = (int) (300.0 * height / width);
								} else {
									mCropParams.outputY = 300;
									mCropParams.outputX = (int) (300.0 * width / height);
								}

								dialog.dismiss();

								Intent intent = CropHelper
										.buildCaptureIntent(mCropParams.uri);
								startActivityForResult(intent,
										CropHelper.REQUEST_CAMERA);
							}
						}).show();
	}

	@Override
	public void onPhotoCropped(Uri uri) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Crop Uri in path: " + uri.getPath());
		Toast.makeText(this, "Photo cropped!", Toast.LENGTH_LONG).show();
		setPicToView(CropHelper.decodeUriAsBitmap(this, mCropParams.uri));
	}

	@Override
	public void onCropCancel() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Crop canceled!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCropFailed(String message) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Crop failed:" + message, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public CropParams getCropParams() {
		// TODO Auto-generated method stub
		return mCropParams;
	}

	@Override
	public Activity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		super.onActivityResult(requestCode, resultCode, data);

		if (null == data)
			return;

		switch (requestCode) {
		case REQUEST_CODE_OPENTEMPLATE:
			mLabelLayout.removeAllViews();
			String file = data.getStringExtra(EXTRA_FILENAME);
			mLabelPage = LabelPage.ReadFromFile(file);
			if (null == mLabelPage) {
				Toast.makeText(mContext,
						strReadTemplateError[Language.GetIndex()],
						Toast.LENGTH_LONG).show();
				break;
			}
			// 设置idn，不然会导致出错。
			// SetLabelPageToLayout(mLabelPage, mLabelLayout);
			Message msg = new Message();
			msg.what = MSG_SETLABELPAGETOLAYOUT;
			mHandler.sendMessage(msg);
			break;
		default:
			break;
		}

		CropHelper.handleResult(this, requestCode, resultCode, data);

	}

	// 更新自动增加的字符串
	private void UpdateAutoComplete() {
		for (LabelItem item : mLabelPage.items) {
			if (item.type == LabelItemType.PLAINTEXT) {
				ItemPlaintext itemPlaintext = (ItemPlaintext) item;
				if (itemPlaintext.autoinc) {
					itemPlaintext.strText = StringUtils
							.AutoIncrease(itemPlaintext.strText);
					EditText et = (EditText) mLabelLayout
							.findViewById(itemPlaintext.id);
					et.setText(itemPlaintext.strText);
					Bitmap bmp = convertToBitmap(et);
					itemPlaintext.image = new Image32();
					itemPlaintext.image.setBitmap(bmp);
				}
			} else if (item.type == LabelItemType.MYCODE128) {
				ItemCode128 itemCode128 = (ItemCode128) item;
				if (itemCode128.autoinc) {
					itemCode128.strText = StringUtils
							.AutoIncrease(itemCode128.strText);
					ImageView iv = (ImageView) mLabelLayout
							.findViewById(itemCode128.id);
					if (itemCode128.make()) {
						iv.setImageBitmap(itemCode128.getBitmap());
					}

				}
			}

		}
	}

	/**
	 * 保存裁剪之后的图片数据，只有当数据正确的时候，才添加ItemBitma
	 * 
	 * @param picdata
	 */
	private void setPicToView(Bitmap photo) {
		if (photo != null) {
			// 这个bitmap的宽度，已经向8对齐了。
			Bitmap bmp = Pos.getBWedBitmap(photo, photo.getWidth());

			// 添加Bitmap到布局
			ItemBitmap itemBitmap = new ItemBitmap(0, mLabelPage.height / 2,
					bmp);
			itemBitmap.id = mLabelPage.getNextId();
			mLabelPage.items.add(itemBitmap);

			mItem = itemBitmap; // 这一个，要不要还待确认。

			addItemBitmapToLabelLayout(itemBitmap, mLabelLayout);
		}
	}

	private void addItemBitmapToLabelLayout(ItemBitmap itemBitmap,
			LabelLayout layout) {

		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				itemBitmap.getWidth(), itemBitmap.getHeight());
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemBitmap.startx;
		lp.topMargin = itemBitmap.starty;
		iv.setImageBitmap(itemBitmap.image.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemBitmap.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);

		Log.i(TAG, "ItemBitmap " + itemBitmap.startx + "," + itemBitmap.starty
				+ "," + itemBitmap.getWidth() + "," + itemBitmap.getHeight());
	}

	private void ShowDialogModifyItemBitmap(final ItemBitmap itemBitmap) {

		// 加载布局
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		inflater.inflate(R.layout.dialog_edit_label_advanced_change_bitmap, rl);

		// 初始化
		final EditText editTextBitmapWidth = (EditText) rl
				.findViewById(R.id.editTextBitmapWidth);
		editTextBitmapWidth.setText("" + itemBitmap.getWidth());

		// 设置回调及显示对话框
		AlertDialog dialog = new AlertDialog.Builder(rl.getContext())
				.setTitle(strSetPictureWidth[Language.GetIndex()])
				.setView(rl)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String str = editTextBitmapWidth.getText()
										.toString();
								int width = 0;
								try {
									width = Integer.parseInt(str);
								} catch (Exception e) {
									width = itemBitmap.getWidth();
								}
								Bitmap bitmap = itemBitmap.image.getBitmap();
								bitmap = Pos.getBWedBitmap(bitmap, width);
								itemBitmap.image.setBitmap(bitmap);

								ImageView iv = (ImageView) mLabelLayout
										.findViewById(itemBitmap.id);
								RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv
										.getLayoutParams();
								lp.width = itemBitmap.getWidth();
								lp.height = itemBitmap.getHeight();
								iv.setLayoutParams(lp);
								iv.setImageBitmap(bitmap);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	// 当输入了确定的文字的时候，才把ItemText添加进去。
	private void ShowDialogAddHardtext() {
		final EditText editText = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputText[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {
							// 本来说，想按照位图来，但是那样，保存到文件的时候会有问题，这里就用EditText吧。
							ItemHardtext itemHardtext = new ItemHardtext(0,
									mLabelPage.height / 2, str);
							itemHardtext.id = mLabelPage.getNextId();
							mLabelPage.items.add(itemHardtext);

							mItem = itemHardtext;

							addItemHardtextToLabelLayout(itemHardtext,
									mLabelLayout);
						}
					}
				});
		builder.show();
	}

	private void addItemHardtextToLabelLayout(ItemHardtext itemHardtext,
			LabelLayout layout) {

		TextView tv = new TextView(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				itemHardtext.getWidth(), itemHardtext.getHeight());
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemHardtext.startx;
		lp.topMargin = itemHardtext.starty;
		tv.setText(itemHardtext.strText);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setTypeface(Typeface.MONOSPACE); // 等宽字体
		tv.setSingleLine(true);
		tv.setBackgroundColor(Color.WHITE);
		tv.setId(itemHardtext.id);
		tv.setClickable(true);
		tv.setOnClickListener(this);
		tv.setOnTouchListener(this);
		tv.setFocusableInTouchMode(true);
		tv.setOnFocusChangeListener(this);

		layout.addView(tv, lp);
	}

	private void ShowDialogModifyItemHardtext(final ItemHardtext itemHardtext) {

		final EditText editText = new EditText(this);
		editText.setText(itemHardtext.strText);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputText[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {
							// Item需要更新，对应的View也需要更新
							itemHardtext.strText = str;

							TextView tv = (TextView) mLabelLayout
									.findViewById(itemHardtext.id);
							RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv
									.getLayoutParams();
							lp.width = itemHardtext.getWidth();
							lp.height = itemHardtext.getHeight();
							tv.setLayoutParams(lp);
							tv.setText(str);
						}
					}
				});
		builder.show();
	}

	private void ShowDialogAddPlaintext() {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_text, rl);

		final EditText editTextContent = (EditText) view
				.findViewById(R.id.editTextContent);
		final Spinner spinnerFontType = (Spinner) view
				.findViewById(R.id.spinnerFontType);
		final Spinner spinnerFontSize = (Spinner) view
				.findViewById(R.id.spinnerFontSize);
		final CheckBox checkBoxContentAutoIncrease = (CheckBox) view
				.findViewById(R.id.checkBoxContentAutoIncrease);

		AssetManager assetManager = getAssets();
		String[] fontNames = null;
		try {
			fontNames = assetManager.list("font");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		List<String> list = new ArrayList<String>();
		for (String fontName : fontNames)
			list.add(fontName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		spinnerFontType.setAdapter(adapter);
		spinnerFontType.setSelection(0);
		spinnerFontSize.setSelection(0);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetTextFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，保存到ItemPlaintext，并且更新显示
								String str = editTextContent.getText()
										.toString();
								String fontType = (String) spinnerFontType
										.getSelectedItem();
								String fontSize = (String) spinnerFontSize
										.getSelectedItem();
								boolean autoinc = checkBoxContentAutoIncrease
										.isChecked();
								if (null == str || "".equals(str)) {
									Toast.makeText(mContext, "文本内容不能为空",
											Toast.LENGTH_LONG).show();
									return;
								}
								if (null == fontType) {
									return;
								}
								if (null == fontSize) {
									return;
								}

								ItemPlaintext itemPlaintext = new ItemPlaintext(
										0, mLabelPage.height / 2, str);
								itemPlaintext.id = mLabelPage.getNextId();
								mLabelPage.items.add(itemPlaintext);

								mItem = itemPlaintext;

								itemPlaintext.fontType = fontType;
								try {
									itemPlaintext.fontSize = Integer
											.parseInt(fontSize);
								} catch (Exception e) {
									e.printStackTrace();
									return;
								}
								itemPlaintext.autoinc = autoinc;

								addItemPlaintextToLabelLayout(itemPlaintext,
										mLabelLayout);
							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void addItemPlaintextToLabelLayout(ItemPlaintext itemPlaintext,
			LabelLayout layout) {

		EditText et = new EditText(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemPlaintext.startx;
		lp.topMargin = itemPlaintext.starty;
		Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font"
				+ File.separator + itemPlaintext.fontType);
		if (null != tf) {
			et.setTypeface(tf);
			Log.i(TAG, "typeface:" + itemPlaintext.fontType);
		} else {
			et.setTypeface(Typeface.MONOSPACE);
		}
		et.setText(itemPlaintext.strText);
		et.setTextSize(itemPlaintext.fontSize);
		et.setGravity(Gravity.CENTER_VERTICAL);
		et.setSingleLine(false);
		et.setBackgroundColor(Color.WHITE);
		et.setId(itemPlaintext.id);
		et.setClickable(true);
		et.setOnClickListener(this);
		et.setOnTouchListener(this);
		et.setFocusableInTouchMode(true);
		et.setOnFocusChangeListener(this);
		layout.addView(et, lp);
		et.requestFocus();
		et.clearFocus();
		et.invalidate();
		layout.invalidate();
	}

	// 在EditText失焦的时候，要转换成图片
	private Bitmap convertToBitmap(EditText et) {
		Bitmap bitmap = UIUtils.ViewToBitmap(et);
		if (null != bitmap)
			bitmap = Pos.getBWedBitmap(bitmap, bitmap.getWidth());
		return bitmap;
	}

	private void ShowDialogModifyItemPlaintext(final ItemPlaintext itemPlaintext) {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_text, rl);

		final EditText editTextContent = (EditText) view
				.findViewById(R.id.editTextContent);
		final Spinner spinnerFontType = (Spinner) view
				.findViewById(R.id.spinnerFontType);
		final Spinner spinnerFontSize = (Spinner) view
				.findViewById(R.id.spinnerFontSize);
		final CheckBox checkBoxContentAutoIncrease = (CheckBox) view
				.findViewById(R.id.checkBoxContentAutoIncrease);

		AssetManager assetManager = getAssets();
		String[] fontNames = null;
		try {
			fontNames = assetManager.list("font");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		List<String> list = new ArrayList<String>();
		for (String fontName : fontNames)
			list.add(fontName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		spinnerFontType.setAdapter(adapter);

		for (int i = 0; i < spinnerFontType.getCount(); ++i) {
			String fontType = ""
					+ (String) spinnerFontType.getItemAtPosition(i);
			if (fontType.equals("" + itemPlaintext.fontType)) {
				spinnerFontType.setSelection(i);
				break;
			}
		}

		for (int i = 0; i < spinnerFontSize.getCount(); ++i) {
			String fontSize = ""
					+ (String) spinnerFontSize.getItemAtPosition(i);
			if (fontSize.equals("" + itemPlaintext.fontSize)) {
				spinnerFontSize.setSelection(i);
				break;
			}
		}

		editTextContent.setText(itemPlaintext.strText);
		checkBoxContentAutoIncrease.setChecked(itemPlaintext.autoinc);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetTextFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，将修改的保存到ItemPlaintext，并且更新显示
								// 点击确定之后，保存到ItemPlaintext，并且更新显示
								String str = editTextContent.getText()
										.toString();
								String fontType = (String) spinnerFontType
										.getSelectedItem();
								String fontSize = (String) spinnerFontSize
										.getSelectedItem();
								boolean autoinc = checkBoxContentAutoIncrease
										.isChecked();

								if (null == str || "".equals(str)) {
									Toast.makeText(mContext, "文本内容不能为空",
											Toast.LENGTH_LONG).show();
									return;
								}
								if (null == fontType) {
									return;
								}
								if (null == fontSize) {
									return;
								}

								itemPlaintext.strText = str;
								itemPlaintext.fontType = fontType;
								try {
									itemPlaintext.fontSize = Integer
											.parseInt(fontSize);
								} catch (Exception e) {
									e.printStackTrace();
									return;
								}
								itemPlaintext.autoinc = autoinc;

								// 数据也已经更新，现在更新视图即可
								EditText et = (EditText) mLabelLayout
										.findViewById(itemPlaintext.id);
								et.setText(itemPlaintext.strText);
								Typeface tf = Typeface.createFromAsset(
										mContext.getAssets(), "font"
												+ File.separator
												+ itemPlaintext.fontType);
								if (null != tf) {
									et.setTypeface(tf);
									Log.i(TAG, "" + tf.toString());
								} else {
									et.setTypeface(Typeface.MONOSPACE);
								}
								et.setTextSize(itemPlaintext.fontSize);
							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	int[] sel_barcodetype = { 0, 1, 5 };
	int[] barcodetype_sel = { 0, 1, -0, -0, -0, 2 };
	int[] sel_unitwidth = { 1, 2, 3, 4 };
	int[] unitwidth_sel = { 0, 0, 1, 2, 3 };

	@SuppressWarnings("unused")
	private void ShowDialogAddBarcode() {

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_barcode, rl);

		final EditText editTextContent = (EditText) view
				.findViewById(R.id.editTextContent);
		final Spinner spinnerBarcodeType = (Spinner) view
				.findViewById(R.id.spinnerBarcodeType);
		final Spinner spinnerBarcodeUnitWidth = (Spinner) view
				.findViewById(R.id.spinnerBarcodeUnitWidth);
		final EditText editTextBarcodeHeight = (EditText) view
				.findViewById(R.id.editTextBarcodeHeight);
		// final CheckBox cbRotate = (CheckBox)
		// view.findViewById(R.id.cbRotate);

		editTextContent.setHint(strInputBarcodeContent[Language.GetIndex()]);
		editTextBarcodeHeight.setText("64");
		spinnerBarcodeUnitWidth.setSelection(unitwidth_sel[4]);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetBarcodeFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，将修改的保存到barcode，并且更新显示
								/**
								 * 0 对应 5 I25
								 */
								String strText = editTextContent.getText()
										.toString();
								int barcodetype = sel_barcodetype[spinnerBarcodeType
										.getSelectedItemPosition()];
								int height = 0;
								int unitwidth = sel_unitwidth[spinnerBarcodeUnitWidth
										.getSelectedItemPosition()];
								try {
									height = Integer
											.parseInt(editTextBarcodeHeight
													.getText().toString());
								} catch (Exception e) {

								}

								if (0 == strText.length()) {
									Toast.makeText(mContext,
											strNoEmpty[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								if (height <= 0) {
									Toast.makeText(mContext,
											strNoEmpty[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								Barcode barcode = null;
								Bitmap barcodeBitmap = null;
								if (BarcodeType.I25.getIntValue() == barcodetype) {
									barcode = new I25Barcode(mLabelPage.width,
											height, unitwidth, strText);
								} else if (BarcodeType.UPCA.getIntValue() == barcodetype) {
									barcode = new UPCABarcode(mLabelPage.width,
											height, unitwidth, strText);
								} else if (BarcodeType.UPCE.getIntValue() == barcodetype) {
									barcode = new UPCEBarcode(mLabelPage.width,
											height, unitwidth, strText);
								} else {
									Toast.makeText(
											mContext,
											strBarcodeTypeNotSupport[Language
													.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								if (!barcode.isDataValid()) {
									Toast.makeText(
											mContext,
											strCannotEncode[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								if (!barcode.genCheckSum()) {
									Toast.makeText(
											mContext,
											strChecksumError[Language
													.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								barcode.make();
								barcodeBitmap = barcode.generateBitmap();

								if ((null == barcode)
										|| (null == barcodeBitmap)) {
									Toast.makeText(
											mContext,
											strCannotGenerateBarcode[Language
													.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								// 条码已经生成，现在添加即可
								ItemBarcode itemBarcode = new ItemBarcode(0,
										mLabelPage.height / 2, barcode,
										barcodeBitmap);
								itemBarcode.id = mLabelPage.getNextId();

								mLabelPage.items.add(itemBarcode);

								mItem = itemBarcode;

								addItemBarcodeToLabelLayout(itemBarcode,
										mLabelLayout);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void addItemBarcodeToLabelLayout(ItemBarcode itemBarcode,
			LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				itemBarcode.getWidth(), itemBarcode.getHeight());
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemBarcode.startx;
		lp.topMargin = itemBarcode.starty;
		Pos.saveMyBitmap(itemBarcode.image.getBitmap(), "UPCA.png");
		iv.setImageBitmap(itemBarcode.image.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemBarcode.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemBarcode(final ItemBarcode itemBarcode) {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_change_barcode, rl);

		final EditText editTextContent = (EditText) view
				.findViewById(R.id.editTextContent);
		final EditText editTextBarcodeHeight = (EditText) view
				.findViewById(R.id.editTextBarcodeHeight);
		final Spinner spinnerBarcodeUnitWidth = (Spinner) view
				.findViewById(R.id.spinnerBarcodeUnitWidth);

		editTextContent.setText("" + itemBarcode.strText);
		editTextBarcodeHeight.setText("" + itemBarcode.height);
		spinnerBarcodeUnitWidth
				.setSelection(unitwidth_sel[itemBarcode.unitwidth]);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetBarcodeFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，将修改的保存到barcode，并且更新显示

								String strText = editTextContent.getText()
										.toString();
								int unitwidth = sel_unitwidth[spinnerBarcodeUnitWidth
										.getSelectedItemPosition()];
								int height = 0;
								try {
									height = Integer
											.parseInt(editTextBarcodeHeight
													.getText().toString());
								} catch (Exception e) {

								}

								if (0 == strText.length()) {
									Toast.makeText(mContext,
											strNoEmpty[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								if (height <= 0) {
									Toast.makeText(mContext,
											strNoEmpty[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								Barcode barcode = itemBarcode.barcode;
								Bitmap bitmap;
								barcode.strText = strText;
								barcode.height = height;
								barcode.unitwidth = unitwidth;

								if (!barcode.isDataValid()) {
									Toast.makeText(
											mContext,
											strCannotEncode[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								if (!barcode.genCheckSum()) {
									Toast.makeText(
											mContext,
											strChecksumError[Language
													.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								barcode.make();
								bitmap = barcode.generateBitmap();
								bitmap = Pos.getBWedBitmap(bitmap,
										bitmap.getWidth());
								if ((null == itemBarcode.barcode)
										|| (null == bitmap)) {
									Toast.makeText(
											mContext,
											strCannotGenerateBarcode[Language
													.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								itemBarcode.setBarcode(barcode, bitmap);

								// 条码已经生成，数据也已经更新，现在更新视图即可

								ImageView iv = (ImageView) mLabelLayout
										.findViewById(itemBarcode.id);
								RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv
										.getLayoutParams();
								lp.width = itemBarcode.getWidth();
								lp.height = itemBarcode.getHeight();
								iv.setLayoutParams(lp);
								iv.setImageBitmap(bitmap);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void ShowDialogAddGrid() {

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_grid, rl);

		final EditText editTextRow = (EditText) view
				.findViewById(R.id.editTextRow);
		final EditText editTextCol = (EditText) view
				.findViewById(R.id.editTextCol);

		editTextCol.setText("0,30,60,90,120");
		editTextRow.setText("0,40,60,80");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入表格行列").setIcon(android.R.drawable.ic_dialog_info)
				.setView(view)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						String strRow = editTextRow.getText().toString();
						String strCol = editTextCol.getText().toString();

						if ("".equals(strRow) || "".equals(strCol)) {
							Toast.makeText(mContext, "不能为空", Toast.LENGTH_LONG)
									.show();
							return;
						}

						ItemGrid itemGrid = new ItemGrid(0,
								mLabelPage.height / 2, strRow, strCol, 2);
						itemGrid.id = mLabelPage.getNextId();
						if (!itemGrid.make()) // 提前make
						{
							Toast.makeText(mContext, "无法解析", Toast.LENGTH_LONG)
									.show();
							return;
						}
						mLabelPage.items.add(itemGrid);

						mItem = itemGrid;

						addItemGridToLabelLayout(itemGrid, mLabelLayout);
					}
				});
		builder.show();
	}

	private void addItemGridToLabelLayout(ItemGrid itemGrid, LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemGrid.startx;
		lp.topMargin = itemGrid.starty;
		iv.setImageBitmap(itemGrid.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemGrid.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemGrid(final ItemGrid itemGrid) {

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_grid, rl);

		final EditText editTextRow = (EditText) view
				.findViewById(R.id.editTextRow);
		final EditText editTextCol = (EditText) view
				.findViewById(R.id.editTextCol);
		editTextRow.setText(itemGrid.row);
		editTextCol.setText(itemGrid.col);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入表格行列").setIcon(android.R.drawable.ic_dialog_info)
				.setView(view)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						String strRow = editTextRow.getText().toString();
						String strCol = editTextCol.getText().toString();

						if ("".equals(strRow) || "".equals(strCol)) {
							Toast.makeText(mContext, "不能为空", Toast.LENGTH_LONG)
									.show();
							return;
						}

						// Item需要更新，对应的View也需要更新
						itemGrid.row = strRow;
						itemGrid.col = strCol;
						if (!itemGrid.make()) // 提前make
						{
							Toast.makeText(mContext, "无法解析", Toast.LENGTH_LONG)
									.show();
							return;
						}

						ImageView iv = (ImageView) mLabelLayout
								.findViewById(itemGrid.id);
						iv.setImageBitmap(itemGrid.getBitmap());
					}
				});
		builder.show();
	}

	// 当输入了确定的文字的时候，才把ItemText添加进去。
	private void ShowDialogAddCode128() {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_code128, rl);

		final EditText editTextContent = (EditText) view
				.findViewById(R.id.editTextContent);
		final EditText editTextHeight = (EditText) view
				.findViewById(R.id.editTextBarcodeHeight);
		final CheckBox checkBoxContentAutoIncrease = (CheckBox) view
				.findViewById(R.id.checkBoxContentAutoIncrease);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle("添加条码")
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								String str = editTextContent.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();
								boolean autoinc = checkBoxContentAutoIncrease
										.isChecked();

								int height = 80;
								if (str.length() == 0) {
									Toast.makeText(mContext,
											strNoEmpty[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								try {
									height = Integer.parseInt(strHeight);
									if (height <= 0 || height >= 1000)
										return;
								} catch (Exception ex) {
									ex.toString();
									Toast.makeText(mContext, "请输入条码高度",
											Toast.LENGTH_LONG).show();
									return;
								}

								ItemCode128 itemCode128 = new ItemCode128(0,
										mLabelPage.height / 2, str);
								itemCode128.height = height;
								itemCode128.autoinc = autoinc;
								itemCode128.id = mLabelPage.getNextId();
								if (!itemCode128.make()) // 提前make
								{
									Toast.makeText(
											mContext,
											strCannotEncode[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								mLabelPage.items.add(itemCode128);

								mItem = itemCode128;

								addItemCode128ToLabelLayout(itemCode128,
										mLabelLayout);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();
		dialog.show();
	}

	private void addItemCode128ToLabelLayout(ItemCode128 itemCode128,
			LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemCode128.startx;
		lp.topMargin = itemCode128.starty;
		iv.setImageBitmap(itemCode128.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemCode128.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemCode128(final ItemCode128 itemCode128) {

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_code128, rl);

		final EditText editTextContent = (EditText) view
				.findViewById(R.id.editTextContent);
		final EditText editTextHeight = (EditText) view
				.findViewById(R.id.editTextBarcodeHeight);
		final CheckBox checkBoxContentAutoIncrease = (CheckBox) view
				.findViewById(R.id.checkBoxContentAutoIncrease);

		editTextContent.setText(itemCode128.strText);
		editTextHeight.setText("" + itemCode128.height);
		checkBoxContentAutoIncrease.setChecked(itemCode128.autoinc);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle("修改条码")
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								String str = editTextContent.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();
								boolean autoinc = checkBoxContentAutoIncrease
										.isChecked();

								int height = 80;
								if (str.length() == 0) {
									Toast.makeText(mContext,
											strNoEmpty[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}
								try {
									height = Integer.parseInt(strHeight);
									if (height <= 0 || height >= 1000)
										return;
								} catch (Exception ex) {
									ex.toString();
									Toast.makeText(mContext, "请输入条码高度",
											Toast.LENGTH_LONG).show();
									return;
								}

								// Item需要更新，对应的View也需要更新
								itemCode128.strText = str;
								itemCode128.height = height;
								itemCode128.autoinc = autoinc;
								if (!itemCode128.make()) // 提前make
								{
									Toast.makeText(
											mContext,
											strCannotEncode[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								ImageView iv = (ImageView) mLabelLayout
										.findViewById(itemCode128.id);
								iv.setImageBitmap(itemCode128.getBitmap());

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();
		dialog.show();
	}

	// 当输入了确定的文字的时候，才把ItemText添加进去。
	private void ShowDialogAddQrcode() {
		final EditText editText = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputQRCodeContent[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {

							ItemQrcode itemQrcode = new ItemQrcode(0,
									mLabelPage.height / 2, str);
							itemQrcode.id = mLabelPage.getNextId();
							if (!itemQrcode.make()) // 提前make
							{
								Toast.makeText(mContext,
										strTextTooLong[Language.GetIndex()],
										Toast.LENGTH_LONG).show();
								return;
							}
							mLabelPage.items.add(itemQrcode);

							mItem = itemQrcode;

							addItemQrcodeToLabelLayout(itemQrcode, mLabelLayout);
						}
					}
				});
		builder.show();
	}

	private void addItemQrcodeToLabelLayout(ItemQrcode itemQrcode,
			LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemQrcode.startx;
		lp.topMargin = itemQrcode.starty;
		iv.setImageBitmap(itemQrcode.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemQrcode.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemQrcode(final ItemQrcode itemQrcode) {

		final EditText editText = new EditText(this);
		editText.setText(itemQrcode.strText);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputQRCodeContent[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {
							// Item需要更新，对应的View也需要更新
							itemQrcode.strText = str;
							if (!itemQrcode.make()) // 提前make
							{
								Toast.makeText(mContext,
										strTextTooLong[Language.GetIndex()],
										Toast.LENGTH_LONG).show();
								return;
							}

							ImageView iv = (ImageView) mLabelLayout
									.findViewById(itemQrcode.id);
							iv.setImageBitmap(itemQrcode.getBitmap());
						}
					}
				});
		builder.show();
	}

	// 当输入了确定的文字的时候，才把ItemText添加进去。
	private void ShowDialogAddPDF417() {
		final EditText editText = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputPDF417Content[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {

							ItemPDF417 itemPDF417 = new ItemPDF417(0,
									mLabelPage.height / 2, str);
							itemPDF417.id = mLabelPage.getNextId();
							if (!itemPDF417.make()) // 提前make
							{
								Toast.makeText(mContext,
										strTextTooLong[Language.GetIndex()],
										Toast.LENGTH_LONG).show();
								return;
							}
							mLabelPage.items.add(itemPDF417);

							mItem = itemPDF417;

							addItemPDF417ToLabelLayout(itemPDF417, mLabelLayout);
						}
					}
				});
		builder.show();
	}

	private void addItemPDF417ToLabelLayout(ItemPDF417 itemPDF417,
			LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemPDF417.startx;
		lp.topMargin = itemPDF417.starty;
		iv.setImageBitmap(itemPDF417.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemPDF417.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemPDF417(final ItemPDF417 itemPDF417) {

		final EditText editText = new EditText(this);
		editText.setText(itemPDF417.strText);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputPDF417Content[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {
							// Item需要更新，对应的View也需要更新
							itemPDF417.strText = str;
							if (!itemPDF417.make()) // 提前make
							{
								Toast.makeText(mContext,
										strTextTooLong[Language.GetIndex()],
										Toast.LENGTH_LONG).show();
								return;
							}

							ImageView iv = (ImageView) mLabelLayout
									.findViewById(itemPDF417.id);
							iv.setImageBitmap(itemPDF417.getBitmap());
						}
					}
				});
		builder.show();
	}

	private void ShowDialogAddBox() {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_box, rl);

		final EditText editTextWidth = (EditText) view
				.findViewById(R.id.editTextWidth);
		final EditText editTextHeight = (EditText) view
				.findViewById(R.id.editTextHeight);
		final EditText editTextBorderWidth = (EditText) view
				.findViewById(R.id.editTextBorderWidth);

		editTextWidth.setText("200");
		editTextHeight.setText("100");
		editTextBorderWidth.setText("5");

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetBoxFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，获取数据，并且更新显示
								String strWidth = editTextWidth.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();
								String strBorderWidth = editTextBorderWidth
										.getText().toString();

								int width = 200;
								int height = 100;
								int border = 5;

								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
									border = Integer.parseInt(strBorderWidth);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if ((width <= 0) || (height <= 0)
										|| (border <= 0)) {
									Toast.makeText(
											mContext,
											strCannotBeZero[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								ItemBox itemBox = new ItemBox(0,
										mLabelPage.height / 2, width, height);
								itemBox.borderwidth = border;
								itemBox.id = mLabelPage.getNextId();
								mLabelPage.items.add(itemBox);

								mItem = itemBox;

								addItemBoxToLabelLayout(itemBox, mLabelLayout);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void addItemBoxToLabelLayout(ItemBox itemBox, LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemBox.startx;
		lp.topMargin = itemBox.starty;
		iv.setImageBitmap(itemBox.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemBox.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemBox(final ItemBox itemBox) {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_box, rl);

		final EditText editTextWidth = (EditText) view
				.findViewById(R.id.editTextWidth);
		final EditText editTextHeight = (EditText) view
				.findViewById(R.id.editTextHeight);
		final EditText editTextBorderWidth = (EditText) view
				.findViewById(R.id.editTextBorderWidth);

		editTextWidth.setText("" + itemBox.width);
		editTextHeight.setText("" + itemBox.height);
		editTextBorderWidth.setText("" + itemBox.borderwidth);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetBoxFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，获取数据，并且更新显示
								String strWidth = editTextWidth.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();
								String strBorderWidth = editTextBorderWidth
										.getText().toString();

								int width = 200;
								int height = 100;
								int border = 5;

								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
									border = Integer.parseInt(strBorderWidth);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if ((width <= 0) || (height <= 0)
										|| (border <= 0)) {
									Toast.makeText(
											mContext,
											strCannotBeZero[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								itemBox.width = width;
								itemBox.height = height;
								itemBox.borderwidth = border;

								Bitmap bitmap = itemBox.getBitmap();
								ImageView iv = (ImageView) mLabelLayout
										.findViewById(itemBox.id);
								iv.setImageBitmap(bitmap);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void ShowDialogAddRect() {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_rect, rl);

		final EditText editTextWidth = (EditText) view
				.findViewById(R.id.editTextWidth);
		final EditText editTextHeight = (EditText) view
				.findViewById(R.id.editTextHeight);

		editTextWidth.setText("200");
		editTextHeight.setText("100");

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetRectFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，获取数据，并且更新显示
								String strWidth = editTextWidth.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();

								int width = 200;
								int height = 100;

								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if ((width <= 0) || (height <= 0)) {
									Toast.makeText(
											mContext,
											strCannotBeZero[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								ItemRect itemRect = new ItemRect(0,
										mLabelPage.height / 2, width, height);
								itemRect.id = mLabelPage.getNextId();
								mLabelPage.items.add(itemRect);

								mItem = itemRect;

								addItemRectToLabelLayout(itemRect, mLabelLayout);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void addItemRectToLabelLayout(ItemRect itemRect, LabelLayout layout) {
		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemRect.startx;
		lp.topMargin = itemRect.starty;
		iv.setImageBitmap(itemRect.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemRect.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemRect(final ItemRect itemRect) {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_rect, rl);

		final EditText editTextWidth = (EditText) view
				.findViewById(R.id.editTextWidth);
		final EditText editTextHeight = (EditText) view
				.findViewById(R.id.editTextHeight);

		editTextWidth.setText("" + itemRect.width);
		editTextHeight.setText("" + itemRect.height);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetRectFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，获取数据，并且更新显示
								String strWidth = editTextWidth.getText()
										.toString();
								String strHeight = editTextHeight.getText()
										.toString();

								int width = 200;
								int height = 100;
								try {
									width = Integer.parseInt(strWidth);
									height = Integer.parseInt(strHeight);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if ((width <= 0) || (height <= 0)) {
									Toast.makeText(
											mContext,
											strCannotBeZero[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								itemRect.width = width;
								itemRect.height = height;

								Bitmap bitmap = itemRect.getBitmap();
								ImageView iv = (ImageView) mLabelLayout
										.findViewById(itemRect.id);
								iv.setImageBitmap(bitmap);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void ShowDialogAddLine() {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_line, rl);

		final EditText editTextStartX = (EditText) view
				.findViewById(R.id.editTextStartX);
		final EditText editTextStartY = (EditText) view
				.findViewById(R.id.editTextStartY);
		final EditText editTextStopX = (EditText) view
				.findViewById(R.id.editTextStopX);
		final EditText editTextStopY = (EditText) view
				.findViewById(R.id.editTextStopY);
		final EditText editTextLineWidth = (EditText) view
				.findViewById(R.id.editTextLineWidth);

		editTextStartX.setText("" + 0);
		editTextStartY.setText("" + mLabelPage.height / 2);
		editTextStopX.setText("" + mLabelPage.width / 2);
		editTextStopY.setText("" + mLabelPage.height / 2);
		editTextLineWidth.setText("" + 5);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetLineFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，获取数据，并且更新显示
								String strStartX = editTextStartX.getText()
										.toString();
								String strStartY = editTextStartY.getText()
										.toString();
								String strStopX = editTextStopX.getText()
										.toString();
								String strStopY = editTextStopY.getText()
										.toString();
								String strLineWdith = editTextLineWidth
										.getText().toString();

								int startx, starty, stopx, stopy, lineWidth;

								try {
									startx = Integer.parseInt(strStartX);
									starty = Integer.parseInt(strStartY);
									stopx = Integer.parseInt(strStopX);
									stopy = Integer.parseInt(strStopY);
									lineWidth = Integer.parseInt(strLineWdith);
								} catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(
											mContext,
											strInputInteger[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								ItemLine itemLine = new ItemLine(startx,
										starty, stopx, stopy);
								itemLine.linewidth = lineWidth;
								itemLine.id = mLabelPage.getNextId();
								mLabelPage.items.add(itemLine);

								mItem = itemLine;

								addItemLineToLabelLayout(itemLine, mLabelLayout);

							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	private void addItemLineToLabelLayout(ItemLine itemLine, LabelLayout layout) {

		ImageView iv = new ImageView(layout.getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.leftMargin = itemLine.getLeft();
		lp.topMargin = itemLine.getTop();
		iv.setImageBitmap(itemLine.getBitmap());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setId(itemLine.id);
		iv.setClickable(true);
		iv.setOnClickListener(this);
		iv.setOnTouchListener(this);
		iv.setFocusableInTouchMode(true);
		iv.setOnFocusChangeListener(this);

		layout.addView(iv, lp);
	}

	private void ShowDialogModifyItemLine(final ItemLine itemLine) {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout rl = new RelativeLayout(this);
		final View view = inflater.inflate(
				R.layout.dialog_edit_label_advanced_add_line, rl);

		final EditText editTextStartX = (EditText) view
				.findViewById(R.id.editTextStartX);
		final EditText editTextStartY = (EditText) view
				.findViewById(R.id.editTextStartY);
		final EditText editTextStopX = (EditText) view
				.findViewById(R.id.editTextStopX);
		final EditText editTextStopY = (EditText) view
				.findViewById(R.id.editTextStopY);
		final EditText editTextLineWidth = (EditText) view
				.findViewById(R.id.editTextLineWidth);

		editTextStartX.setText("" + itemLine.startx);
		editTextStartY.setText("" + itemLine.starty);
		editTextStopX.setText("" + itemLine.stopx);
		editTextStopY.setText("" + itemLine.stopy);
		editTextLineWidth.setText("" + itemLine.linewidth);

		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(strSetLineFormat[Language.GetIndex()])
				.setView(view)
				.setPositiveButton(strOK[Language.GetIndex()],
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								// 点击确定之后，获取数据，并且更新显示
								String strStartX = editTextStartX.getText()
										.toString();
								String strStartY = editTextStartY.getText()
										.toString();
								String strStopX = editTextStopX.getText()
										.toString();
								String strStopY = editTextStopY.getText()
										.toString();
								String strLineWdith = editTextLineWidth
										.getText().toString();

								int startx, starty, stopx, stopy, lineWidth;

								try {
									startx = Integer.parseInt(strStartX);
									starty = Integer.parseInt(strStartY);
									stopx = Integer.parseInt(strStopX);
									stopy = Integer.parseInt(strStopY);
									lineWidth = Integer.parseInt(strLineWdith);
								} catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(
											mContext,
											strInputInteger[Language.GetIndex()],
											Toast.LENGTH_LONG).show();
									return;
								}

								itemLine.startx = startx;
								itemLine.starty = starty;
								itemLine.stopx = stopx;
								itemLine.stopy = stopy;
								itemLine.linewidth = lineWidth;

								ImageView iv = (ImageView) mLabelLayout
										.findViewById(itemLine.id);
								RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv
										.getLayoutParams();
								lp.leftMargin = itemLine.getLeft();
								lp.topMargin = itemLine.getTop();
								iv.setLayoutParams(lp);
								iv.setImageBitmap(itemLine.getBitmap());
							}
						})
				.setNegativeButton(strCancel[Language.GetIndex()], null)
				.create();

		dialog.show();
	}

	// 较高层次的封装
	private void AddItemToLabelLayout(LabelItem item, LabelLayout layout) {

		if (LabelItemType.BITMAP == item.type) {
			mItem = item;
			addItemBitmapToLabelLayout((ItemBitmap) item, layout);
		} else if (LabelItemType.PLAINTEXT == item.type) {
			mItem = item;
			addItemPlaintextToLabelLayout((ItemPlaintext) item, layout);
		} else if (LabelItemType.HARDTEXT == item.type) {
			mItem = item;
			addItemHardtextToLabelLayout((ItemHardtext) item, layout);
		} else if (LabelItemType.BARCODE == item.type) {
			mItem = item;
			addItemBarcodeToLabelLayout((ItemBarcode) item, layout);
		} else if (LabelItemType.QRCODE == item.type) {
			mItem = item;
			addItemQrcodeToLabelLayout((ItemQrcode) item, layout);
		} else if (LabelItemType.PDF417 == item.type) {
			mItem = item;
			addItemPDF417ToLabelLayout((ItemPDF417) item, layout);
		} else if (LabelItemType.BOX == item.type) {
			mItem = item;
			addItemBoxToLabelLayout((ItemBox) item, layout);
		} else if (LabelItemType.RECTANGLE == item.type) {
			mItem = item;
			addItemRectToLabelLayout((ItemRect) item, layout);
		} else if (LabelItemType.LINE == item.type) {
			mItem = item;
			addItemLineToLabelLayout((ItemLine) item, layout);
		} else if (LabelItemType.MYCODE128 == item.type) {
			mItem = item;
			ItemCode128 itemCode128 = (ItemCode128) item;
			if (itemCode128.make())
				addItemCode128ToLabelLayout(itemCode128, layout);
		} else if (LabelItemType.GRID == item.type) {
			mItem = item;
			addItemGridToLabelLayout((ItemGrid) item, layout);
		}
	}

	private void ShowDialogSaveTemplate() {
		final EditText editText = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(strInputFileName[Language.GetIndex()])
				.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
				.setNegativeButton(strCancel[Language.GetIndex()], null);
		builder.setPositiveButton(strOK[Language.GetIndex()],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String str = editText.getText().toString();
						if (str.length() == 0) {
							Toast.makeText(mContext,
									strNoEmpty[Language.GetIndex()],
									Toast.LENGTH_LONG).show();
							return;
						} else {
							// 本来说，想按照位图来，但是那样，保存到文件的时候会有问题，这里就用EditText吧。
							LabelPage.SaveToFile(mLabelPage, DIR_MAIN + str
									+ ".lb");
						}
					}
				});
		builder.show();
	}

	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onDoubleTap");
			if (null != mItem) {
				if (LabelItemType.PLAINTEXT == mItem.type) {
					EditText et = (EditText) mLabelLayout
							.findViewById(mItem.id);
					et.setInputType(InputType.TYPE_CLASS_TEXT);
					et.setSingleLine(false);

					return true;
				}
			}
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onDown");
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY"
					+ velocityY);
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onLongPress");
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
					+ distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onSingleTapUp");
			return super.onSingleTapUp(e);
		}

	}

	static class MHandler extends Handler {

		WeakReference<ActivityLable> mActivity;

		MHandler(ActivityLable activity) {
			mActivity = new WeakReference<ActivityLable>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ActivityLable theActivity = mActivity.get();
			switch (msg.what) {

			case Global.CMD_LABEL_PAGEFINSIH_RESULT:
			case Global.CMD_POS_PRINTBWPICTURE: {
				int result = msg.arg1;
				Toast.makeText(
						theActivity,
						(result == 1) ? strDone[Language.GetIndex()]
								: strFailed[Language.GetIndex()],
						Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Result: " + result);

				break;
			}

			case MSG_SETLABELPAGETOLAYOUT: { // 纸张
				LabelPage labelPage = theActivity.mLabelPage;
				LabelLayout labelLayout = theActivity.mLabelLayout;

				theActivity.SetPaperSize(labelPage.width, labelPage.height);
				// 添加
				for (LabelItem item : labelPage.items) {
					theActivity.AddItemToLabelLayout(item, labelLayout);
				}

				break;
			}

			}

		}
	}
}
