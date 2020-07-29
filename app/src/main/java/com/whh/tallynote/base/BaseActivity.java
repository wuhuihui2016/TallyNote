package com.whh.tallynote.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.activity.NewDayActivity;
import com.whh.tallynote.activity.NewIncomeActivity;
import com.whh.tallynote.activity.NewMemoActivity;
import com.whh.tallynote.activity.NewMonthActivity;
import com.whh.tallynote.activity.NewNotePadActivity;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.BitmapUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.DialogListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.utils.ViewUtils;
import com.whh.tallynote.utils.WPSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.nio.ByteBuffer;

import butterknife.ButterKnife;


/**
 * Created by wuhuihui on 2017/3/24.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected Context context;//获取当前对象
    protected Activity activity;//获取当前对象
    protected String TAG;//当前界面输出log时的标签字段

    private RelativeLayout top_layout; //头布局
    private ImageButton return_btn; //返回按钮
    private TextView titleView; //标题
    private TextView right_btn; //右按钮
    private ImageView right_imgbtn; //右图片按钮
    protected FrameLayout content_layout; //中间内容布局
    private ImageButton addNote; //显示/隐藏新增项目按钮
    protected PopupWindow popupWindow; //新增项目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();
        initBundleData(savedInstanceState); //接收传递的数据
        ButterKnife.bind(this);
        initView(); //初始化界面
        initEvent(); //初始化事件操作
    }

    //接收传递的数据
    protected abstract void initBundleData(Bundle bundle);

    //初始化界面
    protected abstract void initView();

    //初始化事件操作
    protected abstract void initEvent();

    /**
     * 初始化Activity
     */
    private void initActivity() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
        setContentView(R.layout.activity_base_layout);

        context = this;
        activity = this;
        TAG = getLocalClassName(); //初始化常量

        Log.i(TAG, TAG + " is onCreated!");

        AppManager.getAppManager().addActivity(activity);

        top_layout = (RelativeLayout) findViewById(R.id.top_layout);
        return_btn = (ImageButton) findViewById(R.id.return_btn);
        titleView = (TextView) findViewById(R.id.title);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_imgbtn = (ImageView) findViewById(R.id.right_imgbtn);
        content_layout = (FrameLayout) findViewById(R.id.content_layout);
        addNote = (ImageButton) findViewById(R.id.addNote);

        if (TAG.contains("New") || TAG.contains("ForgetPwdActivity")
                || TAG.contains("LoginUserActivity")) {
            addNote.setVisibility(View.GONE);

        } else {
            addNote.setVisibility(View.VISIBLE);
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initPopupWindow();
                }
            });
        }

        if (TAG.contains("List4") && !TAG.contains("Of")) { //仅一级列表页注册EventBus
            if (!EventBus.getDefault().isRegistered(activity))
                EventBus.getDefault().register(activity); //注册事件,不可重复注册
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(String msg) {
    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mLayoutInflater.inflate(R.layout.layout_add_note_pop, null);
            popupWindow = new PopupWindow(layout, 400, 1000);
            ViewUtils.setPopupWindow(activity, popupWindow);
            // 相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上
            popupWindow.showAsDropDown(addNote, 50, 20);
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);

            layout.findViewById(R.id.newDNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewDayActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newMNote).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewMonthActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newIncome).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewIncomeActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newMemo).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewMemoActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.newNotepad).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            AppManager.transfer(activity, NewNotePadActivity.class);
                        }
                    }
            );
            layout.findViewById(R.id.screenShot).setOnClickListener(
                    new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            takeScreenShot();
                        }
                    }
            );
        }
    }

    /**
     * 截屏相关代码 start
     */
    private final int EVENT_SCREENSHOT = 1;
    private MediaProjectionManager mediaProjectionManager;
    private Image image = null;
    private ImageReader mImageReader;
    private VirtualDisplay virtualDisplay;
    private MediaProjection mediaProjection;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void takeScreenShot() {
        mediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), EVENT_SCREENSHOT);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("whh0914", "onActivityResult requestCode=" + requestCode);
        if (requestCode == EVENT_SCREENSHOT) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            if(mediaProjection == null) { //用户在弹窗点了“取消”
                ToastUtils.showWarningLong(activity, "截图已取消！");
                return;
            }
            virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror", width, height,
                    displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    try {
                        if(mImageReader == null) return;
                        image = mImageReader.acquireLatestImage();
                        if (image != null) {
                            final Image.Plane[] planes = image.getPlanes();
                            final ByteBuffer buffer = planes[0].getBuffer();
                            int width = image.getWidth();
                            int height = image.getHeight();
                            int pixelStride = planes[0].getPixelStride();
                            int rowStride = planes[0].getRowStride();
                            int rowPadding = rowStride - pixelStride * width;
                            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(buffer);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
                            if (bitmap != null) {
                                LogUtils.e("whh0914", "屏幕截图成功!");
                                File screenShotDir = new File(FileUtils.screenShot);
                                if (!screenShotDir.exists()) screenShotDir.mkdirs();
                                String filePath = FileUtils.screenShot + "screenShot_" + DateUtils.formatDate4fileName() + ".png";
                                BitmapUtils.saveBitmap(bitmap, filePath);
                                DialogUtils.showMsgDialog(activity,"截图成功！保存在\n" + filePath);
                            } else {
                                ToastUtils.showErrorLong(activity, "截图失败！");
                            }
                            bitmap.recycle();
                        }
                    } catch (Exception e) {
                        LogUtils.e("whh0914", "截图出现异常：" + e.toString());
                        ToastUtils.showErrorLong(activity, "截图失败！");
                    } finally {
                        stopScreenShot();
                    }
                }
            },null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stopScreenShot() {
        if (image != null) {
            image.close();
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader.setOnImageAvailableListener(null, null);
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if(mediaProjection != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection.stop();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        stopScreenShot();
    }
    //截屏相关代码 end

    /**
     * 设置中间内容布局
     *
     * @param title
     */
    protected void setTitle(String title) {

        if (isOtherActivity()) {
            return_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TAG.contains("New")) {
                        DialogUtils.showMsgDialog(activity, "退出本次编辑",
                                "退出", new DialogListener() {
                                    @Override
                                    public void onClick() {
                                        finish();
                                    }
                                },
                                "取消", new DialogListener() {
                                    @Override
                                    public void onClick() {

                                    }
                                });
                    } else {
                        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                        else finish();
                    }
                }
            });
        } else {
            return_btn.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(title)) {
            //设置当前界面的title
            titleView.setText(title);
        } else {

            top_layout.setVisibility(View.GONE);
        }
    }

    protected void setReturnBtnClickLitener(View.OnClickListener listener) {
        if (listener != null) {
            return_btn.setOnClickListener(listener);
        }
    }

    /**
     * 设置中间内容布局
     *
     * @param layoutID
     * @param title
     */
    protected void setContentView(String title, int layoutID) {

        setTitle(title);

        //加载中间布局
        content_layout.removeAllViews();
        View view = LayoutInflater.from(this).inflate(layoutID, null);
        content_layout.addView(view);

    }

    /**
     * 设置界面右上角按钮的点击事件
     *
     * @param text
     * @param listener
     */
    protected void setRightBtnListener(CharSequence text, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(text)) {
            right_btn.setVisibility(View.VISIBLE);
            right_btn.setText(text);
            right_btn.setOnClickListener(listener);
            right_imgbtn.setVisibility(View.GONE);
        }
    }

    /**
     * 设置界面右上角图片按钮的点击事件
     *
     * @param resId
     * @param listener
     */
    protected void setRightImgBtnListener(int resId, View.OnClickListener listener) {
        right_imgbtn.setVisibility(View.VISIBLE);
        right_imgbtn.setImageResource(resId);
        right_imgbtn.setOnClickListener(listener);
        right_btn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (SystemUtils.isFastDoubleClick()) {
            return;
        }
    }

    /**
     * 导出结果回调
     */
    protected ExcelUtils.ICallBackExport callBackExport = new ExcelUtils.ICallBackExport() {
        @Override
        public void callback(boolean sucess, final String fileName) {
            if (sucess) {
                DialogUtils.showMsgDialog(activity, "导出成功\n" + fileName,
                        "查看", new DialogListener() {
                            @Override
                            public void onClick() {
                                WPSUtils.openFile(context, fileName);
                            }
                        },
                        "忽略", new DialogListener() {
                            @Override
                            public void onClick() {
                            }
                        });
            } else ToastUtils.showErrorLong(activity, "导出失败！");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "lifecycle---" + TAG + " onResume");
    }

    @Override
    public void finish() {
        super.finish();
        if (isOtherActivity()) {
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    @Override
    public void onBackPressed() {
        if (isOtherActivity()) {
            if (TAG.contains("New")) {
                DialogUtils.showMsgDialog(activity, "退出本次编辑",
                        "退出", new DialogListener() {
                            @Override
                            public void onClick() {
                                finish();
                            }
                        },
                        "取消", new DialogListener() {
                            @Override
                            public void onClick() {

                            }
                        });
            } else {
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                else super.onBackPressed();
            }

        }
    }


    /**
     * 判断当前activity是不是非MainActivity
     *
     * @return
     */
    private boolean isOtherActivity() {
        return !TAG.contains("MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "lifecycle---" + TAG + " onDestroy");
        //获取栈里所有的Activity
//        List<Activity> allActivitys = SystemUtils.getAllActivitys();
//        StringBuffer sb = new StringBuffer();
//        sb.append("栈里的Activity==>");
//        for (int i = 0; i < allActivitys.size(); i++) {
//            sb.append(allActivitys.get(i).getLocalClassName().replace("com.whh.tallynote.activity.", "") + "---");
//        }
//        LogUtils.d(TAG, "lifecycle---" + sb.toString());
//
//        try {
//            MyApplication.dbHelper.db.close();
//        } catch (Exception e) {
//            LogUtils.e(TAG + "-onDestroy", e.toString());
//        }

        if (TAG.contains("List4") && !TAG.contains("Of")) {
            if (EventBus.getDefault().isRegistered(activity))
                EventBus.getDefault().unregister(activity); //注销事件
        }
    }

}
