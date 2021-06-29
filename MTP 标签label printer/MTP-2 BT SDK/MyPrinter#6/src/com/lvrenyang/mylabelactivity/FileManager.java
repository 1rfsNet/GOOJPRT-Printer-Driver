package com.lvrenyang.mylabelactivity;

import java.io.DataOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.lvrenyang.label.*;
import com.lvrenyang.mylabelwork.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileManager extends Activity implements OnItemClickListener {

	public static String EXTRA_INITIAL_DIRECTORY = "initial_directory";
	private static final int REQUEST_CODE_OPENTEMPLATE = 4;
	private static final String EXTRA_FILENAME = "extra.filename";

	private ListView mListView;
	private TextView mPathView;
	private FileListAdapter mFileAdpter;
	private TextView mItemCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_manager);
		initView();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.file_list);
		mPathView = (TextView) findViewById(R.id.path);
		mItemCount = (TextView) findViewById(R.id.item_count);
		mListView.setOnItemClickListener(this);

		// String apkRoot = "chmod 777 " + getPackageCodePath();
		// RootCommand(apkRoot);

		String initialDirectory = this.getIntent().getStringExtra(
				EXTRA_INITIAL_DIRECTORY);
		if (null == initialDirectory)
			initialDirectory = "/";
		File folder = new File(initialDirectory);
		initData(folder);
	}

	public static boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void initData(File folder) {
		boolean isRoot = folder.getParent() == null;
		mPathView.setText(folder.getAbsolutePath());
		ArrayList<File> files = new ArrayList<File>();
		if (!isRoot) {
			files.add(folder.getParentFile());
		}
		File[] filterFiles = folder.listFiles();
		if (null != filterFiles && filterFiles.length > 0) {
			mItemCount.setText(filterFiles.length + "项");
			for (File file : filterFiles) {
				files.add(file);
			}
		}
		mFileAdpter = new FileListAdapter(this, files, isRoot);
		mListView.setAdapter(mFileAdpter);
	}

	private class FileListAdapter extends BaseAdapter {

		@SuppressWarnings("unused")
		private Context context;
		private ArrayList<File> files;
		private boolean isRoot;
		private LayoutInflater mInflater;

		public FileListAdapter(Context context, ArrayList<File> files,
				boolean isRoot) {
			this.context = context;
			this.files = files;
			this.isRoot = isRoot;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return files.size();
		}

		@Override
		public Object getItem(int position) {
			return files.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.file_list_item, null);
				convertView.setTag(viewHolder);
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.file_title);
				viewHolder.type = (TextView) convertView
						.findViewById(R.id.file_type);
				viewHolder.data = (TextView) convertView
						.findViewById(R.id.file_date);
				viewHolder.size = (TextView) convertView
						.findViewById(R.id.file_size);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			File file = (File) getItem(position);
			if (position == 0 && !isRoot) {
				viewHolder.title.setText("返回上一级");
				viewHolder.data.setVisibility(View.GONE);
				viewHolder.size.setVisibility(View.GONE);
				viewHolder.type.setVisibility(View.GONE);
			} else {
				String fileName = file.getName();
				viewHolder.title.setText(fileName);
				if (file.isDirectory()) {
					viewHolder.size.setText("文件夹");
					viewHolder.size.setTextColor(Color.RED);
					viewHolder.type.setVisibility(View.GONE);
					viewHolder.data.setVisibility(View.GONE);
				} else {
					long fileSize = file.length();
					if (fileSize > 1024 * 1024) {
						float size = fileSize / (1024f * 1024f);
						viewHolder.size.setText(new DecimalFormat("#.00")
								.format(size) + "MB");
					} else if (fileSize >= 1024) {
						float size = fileSize / 1024;
						viewHolder.size.setText(new DecimalFormat("#.00")
								.format(size) + "KB");
					} else {
						viewHolder.size.setText(fileSize + "B");
					}
					int dot = fileName.indexOf('.');
					if (dot > -1 && dot < (fileName.length() - 1)) {
						viewHolder.type.setText(fileName.substring(dot + 1)
								+ "文件");
					}
					viewHolder.data.setText(new SimpleDateFormat(
							"yyyy/MM/dd HH:mm", Locale.US).format(file
							.lastModified()));
				}
			}
			return convertView;
		}

		class ViewHolder {
			private TextView title;
			private TextView type;
			private TextView data;
			private TextView size;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		File file = (File) mFileAdpter.getItem(position);
		if (!file.canRead()) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("权限不足")
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		} else if (file.isDirectory()) {
			initData(file);
		} else {
			// openFile(file);
			selectFile(file);
		}
	}

	private void selectFile(File file) {
		Intent intent = this.getIntent();
		intent.putExtra(EXTRA_FILENAME, file.getAbsolutePath());
		this.setResult(REQUEST_CODE_OPENTEMPLATE, intent);
		finish();
	}

	@SuppressWarnings("unused")
	private void openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		try {
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "未知类型，不能打开", Toast.LENGTH_SHORT).show();
		}
	}

	private String getMIMEType(File file) {
		String type = "*/*";
		String fileName = file.getName();
		int dotIndex = fileName.indexOf('.');
		if (dotIndex < 0) {
			return type;
		}
		String end = fileName.substring(dotIndex, fileName.length())
				.toLowerCase(Locale.US);
		if (end == "") {
			return type;
		}
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end == MIME_MapTable[i][0]) {
				type = MIME_MapTable[i][1];
			}
		}
		return type;
	}

	private final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };
}