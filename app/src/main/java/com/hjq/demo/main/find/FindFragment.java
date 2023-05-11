package com.hjq.demo.main.find;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hjq.demo.R;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.main.MainActivity;


public class FindFragment extends TitleBarFragment<MainActivity> {
    private WebView myWebView = null;
    private SendingProgressDialog m_progressDialog;

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.find_fragment;
    }

    @Override
    protected void initView() {
        myWebView = (WebView) findViewById(R.id.find_web);
        CookieManager.getInstance().removeAllCookie();
        WebSettings webSettings = myWebView.getSettings();//获得webSettings设置对象
        //下面3行是WebView支持JS并能够和JS代码间进行交互的设置
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);//这里是支持flash的相关设置

        myWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.getSettings().setSupportMultipleWindows(true);
    }

    @Override
    protected void initData() {
        MyWebViewClient mWebClient = new MyWebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                if(url == null) return false;

                try {
                    if(url.startsWith("weixin://") || url.startsWith("alipays://") || url.startsWith("alipayqr://")||
                            url.startsWith("mailto://") || url.startsWith("tel://")
                        //其他自定义的scheme
                    ) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }

                //处理http和https开头的url
                wv.loadUrl(url);
                return true;
            }
        };
        myWebView.setWebViewClient(mWebClient);//向WebView提供一个WebViewClient，这样就可以在你的WebView中打开链接了

        myWebView.loadUrl("http://ctrlc.cc/index.html");

    }


    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.indexOf("tel:") >= 0) {// 页面上有数字会导致系统会自动连接电话,屏蔽此功能
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (m_progressDialog != null) {
                m_progressDialog.stop();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                initSendingDialog("正在加载,请稍后...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void initSendingDialog(String message) {
        // TODO Auto-generated method stub
        if (m_progressDialog == null) {
            m_progressDialog = new SendingProgressDialog(getContext(), message);
        }
        m_progressDialog.start();
    }

    /**
     * 按键响应，在WebView中查看网页时，按返回键的时候按浏览历史退回,如果不做此项处理则整个WebView返回退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack())
        {
            // 返回键退回
            myWebView.goBack();//返回上一个历史网页
            //finish();
            return true;
        }
        return onKeyDown(keyCode, event);
    }

}
