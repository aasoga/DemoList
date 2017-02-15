package com.example.v_yanligang.nuomidemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_yanligang on 2016/11/30.
 */

public class JsBrigeActivity extends Activity implements View.OnClickListener {
    public static final String TAG = JsBrigeActivity.class.getSimpleName();
    private BridgeWebView mBridgeWv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsbrige);
        initView();
    }

    private void initView() {
        Button towebBt = (Button) findViewById(R.id.bt_toweb);
        towebBt.setOnClickListener(this);
        Button defaultBt = (Button) findViewById(R.id.bt_default);
        defaultBt.setOnClickListener(this);
        mBridgeWv = (BridgeWebView) findViewById(R.id.bwv);
        mBridgeWv.loadUrl("http://cq01-ps-dev201.cq01.baidu.com:8060/fileup.html");
        // 接收数据
        mBridgeWv.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "DefaultHandler接收全部来自web的数据："+data);
                function.onCallBack("接收到数据以后，回传到web");
            }
        });
        WebSettings settings = mBridgeWv.getSettings();
        settings.setGeolocationEnabled(true);

        setWebview();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_default:
                // 发送数据给web默认接收
                mBridgeWv.send("发送数据给web默认接收", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        Log.e(TAG, "接收來自web的数据" + data);
                    }
                });
                break;
            case R.id.bt_toweb:
                // 发送数据给web指定接收
                mBridgeWv.callHandler("functionJs", "发送数据给web指定接收", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        Log.e(TAG, "来自web的回传数据" + data);
                    }
                });
                break;
        }
    }


    private void setWebview() {
        mBridgeWv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                take();
                return true;
            }


            //<3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                take();
            }
            //>3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                take();
            }
            //>4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                take();
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin,true,false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });

    }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data);
                } else if (mUploadMessage != null) {
                    Log.e("result", result + "");
                    if (result == null) {
                        mUploadMessage.onReceiveValue(imageUri);
                        mUploadMessage = null;
                        Log.e("imageUri", imageUri + "");
                    } else {
                        mUploadMessage.onReceiveValue(result);
                        mUploadMessage = null;
                    }
                }
            }
        }




        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
                return;
            }


            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    results = new Uri[]{imageUri};
                } else {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }
            if (results != null) {
                mUploadCallbackAboveL.onReceiveValue(results);
                mUploadCallbackAboveL = null;
            } else {
                results = new Uri[]{imageUri};
                mUploadCallbackAboveL.onReceiveValue(results);
                mUploadCallbackAboveL = null;
            }
            return;
        }


        private void take() {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            imageUri = Uri.fromFile(file);
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent i = new Intent(captureIntent);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                i.setPackage(packageName);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntents.add(i);
            }
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }


        private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
        private ValueCallback<Uri[]> mUploadCallbackAboveL;
        private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调
        private Uri imageUri;


}
