package com.lvrenyang.mylabelactivity;

import java.io.UnsupportedEncodingException;

import com.lvrenyang.mylabelwork.Global;
import com.lvrenyang.mylabelwork.WorkService;
import com.lvrenyang.utils.Language;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class ActivityJS extends Activity {

	private WebView mWebView = null;
	private static final String strPrinting[] = { "Printing", "正在打印" };
	private static final String strNotConnected[] = { "Printer is not connected",
			"打印机未连接" };
	private static final String strHelpFile[] = {"file:///android_asset/help_en.html","file:///android_asset/help_zh.html"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void showWebView() { // webView与js交互代码
		try {
			mWebView = new WebView(this);
			setContentView(mWebView);

			mWebView.requestFocus();

			mWebView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int progress) {
					ActivityJS.this.setTitle("Loading...");
					ActivityJS.this.setProgress(progress);

					if (progress >= 80) {
						ActivityJS.this.setTitle("JsAndroid Test");
					}
				}
			});

			mWebView.setOnKeyListener(new View.OnKeyListener() { // webview can
																	// go back
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& mWebView.canGoBack()) {
						mWebView.goBack();
						return true;
					}
					return false;
				}
			});

			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDefaultTextEncodingName("utf-8");

			mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");
			mWebView.loadUrl(strHelpFile[Language.GetIndex()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object getHtmlObject() {
		Object insertObj = new Object() {

			@SuppressWarnings("unused")
			public String Print(final String param) {
				if (null == param)
					return "";

				byte[] buf = null;
				try {
					buf = param.getBytes("GBK");
				} catch (UnsupportedEncodingException e) {
					return e.toString();
				}

				if (WorkService.workThread.isConnected()) {
					Bundle data = new Bundle();
					data.putByteArray(Global.BYTESPARA1, buf);
					data.putInt(Global.INTPARA1, 0);
					data.putInt(Global.INTPARA2, buf.length);
					WorkService.workThread
							.handleCmd(Global.CMD_POS_WRITE, data);
					return strPrinting[Language.GetIndex()];
				} else {
					return strNotConnected[Language.GetIndex()];
				}
			}

			@SuppressWarnings("unused")
			public String HtmlcallJava() {
				return "Html call Java";
			}

			@SuppressWarnings("unused")
			public String HtmlcallJava2(final String param) {
				return "Html call Java : " + param;
			}

			@SuppressWarnings("unused")
			public void JavacallHtml() {
				runOnUiThread(new Runnable() {
					public void run() {
						mWebView.loadUrl("javascript: showFromHtml()");
						Toast.makeText(ActivityJS.this, "clickBtn",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@SuppressWarnings("unused")
			public void JavacallHtml2() {
				runOnUiThread(new Runnable() {
					public void run() {
						mWebView.loadUrl("javascript: showFromHtml2('IT-homer blog')");
						Toast.makeText(ActivityJS.this, "clickBtn2",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		};

		return insertObj;
	}

}
