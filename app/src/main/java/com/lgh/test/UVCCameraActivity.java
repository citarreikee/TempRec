package com.lgh.test;
import java.io.FileInputStream;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.guide.guidecore.GuideInterface;
import com.guide.guidecore.UsbStatusInterface;
import com.guide.guidecore.view.IrSurfaceView;
import com.lgh.uvccamera.UVCCameraProxy;
import com.lgh.uvccamera.bean.PicturePath;
import com.lgh.uvccamera.callback.ConnectCallback;
import com.lgh.uvccamera.callback.PhotographCallback;
import com.lgh.uvccamera.callback.PictureCallback;
import com.lgh.uvccamera.callback.PreviewCallback;
import com.serenegiant.usb.Size;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import org.openalpr.OpenALPR;
import org.openalpr.model.Results;
import org.openalpr.model.ResultsError;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class UVCCameraActivity extends AppCompatActivity {

    private TextView text5;
    private TextView text6;
    private TextView text7;
    private TextView text8;
    private int w1;
    private int h1;
    private Bitmap mybitmap_face;
    private static final String TAG1 = "OPENCV";
    private ImageView logo;
    private static final String TAG = "MainActivity";
    private SurfaceView mySurfaceView;
    private SurfaceView mySurfaceView_text;
    private UVCCameraProxy mUVCCamera;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private TextView[] text_title=new TextView[5] ;
    private boolean isFirst = true;
    private String path1;
    private Mat myMat;
    private MatOfRect myMatOfRect;
    private CascadeClassifier myCascadeClassifier;
    private Mat myMatGray;
    private int mAbsoluteFaceSize = 0;
    private static final int SRC_WIDTH = 90;
    private static final int SRC_HEIGHT = 120;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private int width_dect=640;
    private int height_dect=480;
    private int rawWidth;
    private int rawHeight;
    private GuideInterface myGuideInterface;
    private FrameLayout myFrameLayout;
    private IrSurfaceView myIrSurfaceView;
    private Bitmap mybitmap;
    private short[] myY16Frame;
    private int maxIndex =0;
    private int minIndex =0;
    private float currentTemp;
    private float currentHumidity;
    private float temperror=4;

    private int[] face_x=new int[5];
    private int[] face_y=new int[5];
    private int[] face_width=new int[5];
    private int[] face_height=new int[5];
    private Float[] point_temp=new Float[5];

    private String ANDROID_DATA_DIR;
    private String Presult;


    private int paiduan=0;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            Log.i(TAG, "mLoaderCallback:status"+status);
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };
    /*********************************************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_uvc_camera);
        File folder = new File(Environment.getExternalStorageDirectory() + "/OpenALPR/");
        if (!folder.exists()) {
            folder.mkdir();
        }
        ANDROID_DATA_DIR = folder.getAbsolutePath();

        initView();
        text5.setText("fffff");
        initOpenCV();
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                //模拟点击click事件
                while (true)
                {
                    try
                    {
                        Instrumentation mInst = new Instrumentation();
                        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                                1360, 600, 0));
                        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                                1360, 600, 0));
                        Thread.sleep(1000);
                    }catch (InterruptedException a ){}
                }
            }
        }).start();
        */
    }
    /*********************************************************************************************************************************/
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        uvcCamera_view();
        new PRthread().start();
        //temp_dectection();
        //Text_follow();
        //temp_dectection_kuang();


    }
    /*********************************************************************************************************************************/
    @Override
    protected void onPause() {
        super.onPause();
        myGuideInterface.stopGetImage();
        myGuideInterface.unRigistUsbStatus();
        myGuideInterface.unRegistUsbPermissions();
    }
    /*********************************************************************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myGuideInterface.guideCoreDestory();
    }
    /*********************************************************************************************************************************/
    private void initView() {
        logo = findViewById(R.id.logo);
        myGuideInterface = new GuideInterface();
        mySurfaceView = findViewById(R.id.mySurfaceView);
        mySurfaceView_text = findViewById(R.id.mySurfaceView_text);
        myFrameLayout = (FrameLayout) findViewById(R.id.myFrameLayout1);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);
        text7 = findViewById(R.id.text7);
        text8 = findViewById(R.id.text8);
        text_title[0]=findViewById(R.id.text_title1);
        text_title[1]=findViewById(R.id.text_title2);
        text_title[2]=findViewById(R.id.text_title3);
        text_title[3]=findViewById(R.id.text_title4);
        text_title[4]=findViewById(R.id.text_title5);
        getwebinfo();
        logo.bringToFront();
        text_title[0].bringToFront();
        text_title[1].bringToFront();
        text_title[2].bringToFront();
        text_title[3].bringToFront();
        text_title[4].bringToFront();


        myGuideInterface.guideCoreInit(UVCCameraActivity.this, ImageInitParameter.getPaletteIndex(), ImageInitParameter.getScale(), ImageInitParameter.getRotateState());
        if (ImageInitParameter.getRotateState() == 1 || ImageInitParameter.getRotateState() == 3) {
            //宽是90，高是120
            rawWidth = SRC_HEIGHT;
            rawHeight = SRC_WIDTH;
        } else {
            //宽是120，高是90
            rawWidth = SRC_WIDTH;
            rawHeight = SRC_HEIGHT;
        }
        myIrSurfaceView = new IrSurfaceView(UVCCameraActivity.this);
        FrameLayout.LayoutParams ifrSurfaceViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        myIrSurfaceView.setLayoutParams(ifrSurfaceViewLayoutParams);
        myIrSurfaceView.setMatrix(dip2px(720) / 360, 0, 0);
        myFrameLayout.addView(myIrSurfaceView);
    }
    /*********************************************************************************************************************************/
    private void initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library not found!");
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    /*********************************************************************************************************************************/
    private void uvcCamera_view() {
        mUVCCamera = new UVCCameraProxy(UVCCameraActivity.this);
        // 已有默认配置，不需要可以不设置
        mUVCCamera.getConfig()
                .isDebug(true)
                .setPicturePath(PicturePath.APPCACHE)
                .setDirName("uvccamera")
                .setProductId(0)
                .setVendorId(0);
        mUVCCamera.setPreviewSurface(mySurfaceView);

        mUVCCamera.setConnectCallback(new ConnectCallback() {
            @Override
            public void onAttached(UsbDevice usbDevice) {
                mUVCCamera.requestPermission(usbDevice);
            }

            @Override
            public void onGranted(UsbDevice usbDevice, boolean granted) {
                if (granted) {
                    mUVCCamera.connectDevice(usbDevice);
                }
            }
            @Override
            public void onConnected(UsbDevice usbDevice) {
                mUVCCamera.openCamera();
            }

            @Override
            public void onCameraOpened() {
                mUVCCamera.setPreviewSize(640, 480);
                mUVCCamera.startPreview();
            }
            @Override
            public void onDetached(UsbDevice usbDevice) {
                mUVCCamera.closeCamera();
            }
        });
        mUVCCamera.setPreviewCallback(new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] yuv) {
                //获得Bitmap图像：mybitmap_face
                mybitmap_face = rawByteArray2RGBABitmap2(yuv, width_dect, height_dect);
                mybitmap_face = mybitmap_face.copy(Bitmap.Config.RGB_565, true);
                //获得Mat图像：myMat
                myMat = new Mat(mybitmap_face.getHeight(), mybitmap_face.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(mybitmap_face, myMat);
                //获得Mat灰度图像：myMatGray
                myMatGray = new Mat(mybitmap_face.getHeight(), mybitmap_face.getWidth(), CvType.CV_8UC4);
                Imgproc.cvtColor(myMat, myMatGray, Imgproc.COLOR_BGRA2GRAY);
                //执行人脸检测
                myMatOfRect = new MatOfRect();
                myCascadeClassifier = createDetector(getApplicationContext(), R.raw.lbpcascade_frontalface);
                myCascadeClassifier.detectMultiScale(myMatGray, myMatOfRect);
                //得到人脸检测结果：faces（数组），长度（人数）：length
                final Rect[] faces = myMatOfRect.toArray();
                final int length = faces.length;
                //text5.setText(String.valueOf(length));
                //输出人脸位置坐标，供红外温度测试模块进行相应位置温度测量
                for(int i=0;i<5;i++  )
                {
                    if(i<length)
                    {
                        face_x[i]=faces[i].x+faces[i].width/2;
                        face_y[i]=faces[i].y+faces[i].height/2;
                        face_width[i]=faces[i].width;

                    }else
                    {
                        face_x[i]=-1;
                        face_y[i]=-1;
                        face_width[i]=-1;
                    }
                }
                //************************(车牌识别）******************************//

                //************************(车牌识别）******************************//
            }
        });
    }
    /*********************************************************************************************************************************/
    public class PRthread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    Thread.currentThread().sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                while(mybitmap_face != null){
                Presult = PlateRec(mybitmap_face);
                text6.setText(Presult);}
            }
        }
    }
    /*********************************************************************************************************************************/
    public String PlateRec(Bitmap bitmap){
        final File myCaptureFile = new File(ANDROID_DATA_DIR,"Capture.png");
        saveBitmapFile(bitmap,myCaptureFile);
        //final String openAlprConfFile =  folder.getAbsolutePath() + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
        final String openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
        String result = OpenALPR.Factory.create(UVCCameraActivity.this, ANDROID_DATA_DIR).recognizeWithCountryRegionNConfig("eu", "", myCaptureFile.getAbsolutePath(), openAlprConfFile, 1);

        //Log.d("OPEN ALPR", result);

        try {
            final Results results = new Gson().fromJson(result, Results.class);
            if (results == null || results.getResults() == null || results.getResults().size() == 0) {
                return("No licence plate detected.");

            } else {
                return( results.getResults().get(0).getPlate());
            }
        } catch (JsonSyntaxException exception) {
            final ResultsError resultsError = new Gson().fromJson(result, ResultsError.class);
            return(resultsError.getMsg());
        }
    }
    /*********************************************************************************************************************************/
    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }
    /*********************************************************************************************************************************/
    public void Text_follow()
    {
        mySurfaceView_text.setZOrderMediaOverlay(true);
        final SurfaceHolder mySurfaceHolder=mySurfaceView_text.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        final Paint myPaint=new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setTextSize(60);
        new Thread(){
            @Override
            public void run() {
                try
                {
                    Thread.sleep(1000);
                }catch (InterruptedException b){}
                while (true)
                {
                    try
                    {
                        Thread.sleep(10);
                    }catch (InterruptedException b){}
                    Canvas myCanvas=mySurfaceHolder.lockCanvas();
                    myCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    for(int i=0;i<5;i++)
                    {
                        if(face_x[i]!=-1&&face_y[i]!=-1)
                        {
                            if(point_temp[i]!=null)
                            {
                                if(point_temp[i]>34&&point_temp[i]<40)
                                {
                                    myCanvas.drawText(String.valueOf(point_temp[i])+"℃",face_x[i]*2-80,face_y[i]*2-200,myPaint);
                                }
                            }
                        }
                    }
                    mySurfaceHolder.unlockCanvasAndPost(myCanvas);
                }
            }
        }.start();
    }
    /*********************************************************************************************************************************/

    public void temp_dectection()
    {
        myGuideInterface.registUsbPermissions();
        myGuideInterface.registUsbStatus(new UsbStatusInterface() {
            @Override
            public void usbConnect() {
                Toast.makeText(UVCCameraActivity.this, "HongWai_USBconnect", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void usbDisConnect() {
                //finish();
            }
        });
        myGuideInterface.startGetImage(new GuideInterface.ImageCallBackInterface() {
            @Override
            public void callBackOneFrameBitmap(Bitmap bitmap, short[] shorts) {
                try {
                    Thread.sleep(200);
                }catch (InterruptedException r){}
                    mybitmap = bitmap;
                    myY16Frame = shorts;
                    //myIrSurfaceView.doDraw(mybitmap, myGuideInterface.getShutterStatus());
                    text7.setText(String.valueOf(Float.parseFloat(myGuideInterface.measureTemByY16(myY16Frame[getMaxY16(myY16Frame)])) + temperror) + "℃");
                    text8.setText(String.valueOf(Float.parseFloat(myGuideInterface.measureTemByY16(myY16Frame[getMinY16(myY16Frame)])) + temperror) + "℃");
                    for (int i = 0; i < 5; i++) {
                        if (face_x[i] != -1 && face_y[i] != -1) {
                            String tempSurface = tempMearsure(face_x[i], face_y[i], myY16Frame);
                            if(tempSurface!=null)
                            {
                                float temp = Float.parseFloat(tempSurface);
                                if (text5.getText() != null) {
                                    //读出环境温度（字符串）
                                    String e_temp = text5.getText().toString();
                                    String regEx = "[^0-9]+";
                                    Pattern pattern = Pattern.compile(regEx);
                                    String[] cs = pattern.split(e_temp);
                                    //得到环境温度（数字值）
                                    float e_temp_num = Float.parseFloat(cs[0]);
                                    //计算人体内部真实温度值
                                    String real_temp = myGuideInterface.getHumanTemp(temp, e_temp_num);
                                    temp = Float.parseFloat(real_temp);
                                }
                                //误差补偿
                                if(face_width[i]>80)
                                {
                                    temp=temp+(float)0.1;
                                }else if(face_width[i]<=80&&face_width[i]>50)
                                {
                                    temp=temp+(float)0.2;
                                }else if(face_width[i]<=50&&face_width[i]>30)
                                {
                                    temp=temp+(float)0.3;
                                }else
                                {
                                    temp=temp+(float)0.4;
                                }
                                point_temp[i]=Math.round(temp*10)/10f;
                            }else{
                            }
                        } else {
                        }
                    }
            }
        });
    }
    /*********************************************************************************************************************************/
    public void temp_dectection_kuang()
    {
        String autoShutterSwitch = SPUtils.getSwitch(UVCCameraActivity.this);
        long period = Long.valueOf(SPUtils.getPeriod(UVCCameraActivity.this));
        long delay = Long.valueOf(SPUtils.getDelay(UVCCameraActivity.this));
        myGuideInterface.registUsbPermissions();
        myGuideInterface.registUsbStatus(new UsbStatusInterface() {
            @Override
            public void usbConnect() {
                Toast.makeText(UVCCameraActivity.this, "HongWai_USBconnect", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void usbDisConnect() {
                //finish();
            }
        });
        myGuideInterface.setAutoShutter(TextUtils.equals(autoShutterSwitch, "开"), period, delay);
        myGuideInterface.startGetImage(new GuideInterface.ImageCallBackInterface() {
            @Override
            public void callBackOneFrameBitmap(Bitmap bitmap, short[] shorts) {
                mybitmap = bitmap;
                myY16Frame = shorts;
                myIrSurfaceView.doDraw(mybitmap, myGuideInterface.getShutterStatus());
                text7.setText(String.valueOf(Float.parseFloat(myGuideInterface.measureTemByY16(myY16Frame[getMaxY16(myY16Frame)])) + temperror) + "℃");
                text8.setText(String.valueOf(Float.parseFloat(myGuideInterface.measureTemByY16(myY16Frame[getMinY16(myY16Frame)])) + temperror) + "℃");
                //***********************************************************************************************************************//
                String[] s1=new String[10];
                s1[0]=myGuideInterface.measureTemByY16(myY16Frame[9*120+12]);
                s1[1]=myGuideInterface.measureTemByY16(myY16Frame[15*120+12]);
                s1[2]=myGuideInterface.measureTemByY16(myY16Frame[27*120+12]);
                s1[3]=myGuideInterface.measureTemByY16(myY16Frame[35*120+12]);
                s1[4]=myGuideInterface.measureTemByY16(myY16Frame[45*120+12]);
                s1[5]=myGuideInterface.measureTemByY16(myY16Frame[55*120+12]);
                s1[6]=myGuideInterface.measureTemByY16(myY16Frame[63*120+12]);
                s1[7]=myGuideInterface.measureTemByY16(myY16Frame[72*120+12]);
                s1[8]=myGuideInterface.measureTemByY16(myY16Frame[81*120+12]);
                s1[9]=myGuideInterface.measureTemByY16(myY16Frame[89*120+12]);
                for(int i=0;i<10;i++)
                {
                    String tempSurface=s1[i];
                    float temp = Float.parseFloat(tempSurface);

                    if (text5.getText() != null) {
                        //读出环境温度（字符串）
                        String e_temp = text5.getText().toString();
                        String regEx = "[^0-9]+";
                        Pattern pattern = Pattern.compile(regEx);
                        String[] cs = pattern.split(e_temp);
                        //得到环境温度（数字值）
                        float e_temp_num = Float.parseFloat(cs[0]);
                        //计算人体内部真实温度值
                        String real_temp = myGuideInterface.getHumanTemp(temp, e_temp_num);
                        temp = Float.parseFloat(real_temp);
                    }

                    if (temp <= 37.5&&temp>34) {
                        text_title[0].setBackgroundColor(Color.parseColor("#55000000"));
                        text_title[0].setTextColor(Color.WHITE);
                        text_title[0].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    } else if(temp > 37.5&&temp < 40){
                        text_title[0].setBackgroundColor(Color.parseColor("#FF000000"));
                        text_title[0].setTextColor(Color.RED);
                        text_title[0].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    }else {
                        text_title[0].setBackgroundColor(Color.parseColor("#00000000"));
                        text_title[0].setText(" ");
                    }
                }
                //***********************************************************************************************************************//
                //***********************************************************************************************************************//
                String[] s2=new String[10];
                s2[0]=myGuideInterface.measureTemByY16(myY16Frame[9*120+36]);
                s2[1]=myGuideInterface.measureTemByY16(myY16Frame[15*120+36]);
                s2[2]=myGuideInterface.measureTemByY16(myY16Frame[27*120+36]);
                s2[3]=myGuideInterface.measureTemByY16(myY16Frame[35*120+36]);
                s2[4]=myGuideInterface.measureTemByY16(myY16Frame[45*120+36]);
                s2[5]=myGuideInterface.measureTemByY16(myY16Frame[55*120+36]);
                s2[6]=myGuideInterface.measureTemByY16(myY16Frame[63*120+36]);
                s2[7]=myGuideInterface.measureTemByY16(myY16Frame[72*120+36]);
                s2[8]=myGuideInterface.measureTemByY16(myY16Frame[81*120+36]);
                s2[9]=myGuideInterface.measureTemByY16(myY16Frame[89*120+36]);
                for(int i=0;i<10;i++)
                {
                    String tempSurface=s2[i];
                    float temp = Float.parseFloat(tempSurface);

                    if (text5.getText() != null) {
                        //读出环境温度（字符串）
                        String e_temp = text5.getText().toString();
                        String regEx = "[^0-9]+";
                        Pattern pattern = Pattern.compile(regEx);
                        String[] cs = pattern.split(e_temp);
                        //得到环境温度（数字值）
                        float e_temp_num = Float.parseFloat(cs[0]);
                        //计算人体内部真实温度值
                        String real_temp = myGuideInterface.getHumanTemp(temp, e_temp_num);
                        temp = Float.parseFloat(real_temp);
                    }

                    if (temp <= 37.5&&temp>34) {
                        text_title[1].setBackgroundColor(Color.parseColor("#55000000"));
                        text_title[1].setTextColor(Color.WHITE);
                        text_title[1].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    } else if(temp > 37.5&&temp < 40){
                        text_title[1].setBackgroundColor(Color.parseColor("#FF000000"));
                        text_title[1].setTextColor(Color.RED);
                        text_title[1].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    }else {
                        text_title[1].setBackgroundColor(Color.parseColor("#00000000"));
                        text_title[1].setText(" ");
                    }
                }
                //***********************************************************************************************************************//
                //***********************************************************************************************************************//
                String[] s3=new String[10];
                s3[0]=myGuideInterface.measureTemByY16(myY16Frame[9*120+60]);
                s3[1]=myGuideInterface.measureTemByY16(myY16Frame[15*120+60]);
                s3[2]=myGuideInterface.measureTemByY16(myY16Frame[27*120+60]);
                s3[3]=myGuideInterface.measureTemByY16(myY16Frame[35*120+60]);
                s3[4]=myGuideInterface.measureTemByY16(myY16Frame[45*120+60]);
                s3[5]=myGuideInterface.measureTemByY16(myY16Frame[55*120+60]);
                s3[6]=myGuideInterface.measureTemByY16(myY16Frame[63*120+60]);
                s3[7]=myGuideInterface.measureTemByY16(myY16Frame[72*120+60]);
                s3[8]=myGuideInterface.measureTemByY16(myY16Frame[81*120+60]);
                s3[9]=myGuideInterface.measureTemByY16(myY16Frame[89*120+60]);
                for(int i=0;i<10;i++)
                {
                    String tempSurface=s3[i];
                    float temp = Float.parseFloat(tempSurface);

                    if (text5.getText() != null) {
                        //读出环境温度（字符串）
                        String e_temp = text5.getText().toString();
                        String regEx = "[^0-9]+";
                        Pattern pattern = Pattern.compile(regEx);
                        String[] cs = pattern.split(e_temp);
                        //得到环境温度（数字值）
                        float e_temp_num = Float.parseFloat(cs[0]);
                        //计算人体内部真实温度值
                        String real_temp = myGuideInterface.getHumanTemp(temp, e_temp_num);
                        temp = Float.parseFloat(real_temp);
                    }

                    if (temp <= 37.5&&temp>34) {
                        text_title[2].setBackgroundColor(Color.parseColor("#55000000"));
                        text_title[2].setTextColor(Color.WHITE);
                        text_title[2].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    } else if(temp > 37.5&&temp < 40){
                        text_title[2].setBackgroundColor(Color.parseColor("#FF000000"));
                        text_title[2].setTextColor(Color.RED);
                        text_title[2].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    }else {
                        text_title[2].setBackgroundColor(Color.parseColor("#00000000"));
                        text_title[2].setText(" ");
                    }
                }
                //***********************************************************************************************************************//
                //***********************************************************************************************************************//
                String[] s4=new String[10];
                s4[0]=myGuideInterface.measureTemByY16(myY16Frame[9*120+84]);
                s4[1]=myGuideInterface.measureTemByY16(myY16Frame[15*120+84]);
                s4[2]=myGuideInterface.measureTemByY16(myY16Frame[27*120+84]);
                s4[3]=myGuideInterface.measureTemByY16(myY16Frame[35*120+84]);
                s4[4]=myGuideInterface.measureTemByY16(myY16Frame[45*120+84]);
                s4[5]=myGuideInterface.measureTemByY16(myY16Frame[55*120+84]);
                s4[6]=myGuideInterface.measureTemByY16(myY16Frame[63*120+84]);
                s4[7]=myGuideInterface.measureTemByY16(myY16Frame[72*120+84]);
                s4[8]=myGuideInterface.measureTemByY16(myY16Frame[81*120+84]);
                s4[9]=myGuideInterface.measureTemByY16(myY16Frame[89*120+84]);
                for(int i=0;i<10;i++)
                {
                    String tempSurface=s4[i];
                    float temp = Float.parseFloat(tempSurface);

                    if (text5.getText() != null) {
                        //读出环境温度（字符串）
                        String e_temp = text5.getText().toString();
                        String regEx = "[^0-9]+";
                        Pattern pattern = Pattern.compile(regEx);
                        String[] cs = pattern.split(e_temp);
                        //得到环境温度（数字值）
                        float e_temp_num = Float.parseFloat(cs[0]);
                        //计算人体内部真实温度值
                        String real_temp = myGuideInterface.getHumanTemp(temp, e_temp_num);
                        temp = Float.parseFloat(real_temp);
                    }

                    if (temp <= 37.5&&temp>34) {
                        text_title[3].setBackgroundColor(Color.parseColor("#55000000"));
                        text_title[3].setTextColor(Color.WHITE);
                        text_title[3].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    } else if(temp > 37.5&&temp < 40){
                        text_title[3].setBackgroundColor(Color.parseColor("#FF000000"));
                        text_title[3].setTextColor(Color.RED);
                        text_title[3].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    }else {
                        text_title[3].setBackgroundColor(Color.parseColor("#00000000"));
                        text_title[3].setText(" ");
                    }
                }
                //***********************************************************************************************************************//
                //***********************************************************************************************************************//
                String[] s5=new String[10];
                s5[0]=myGuideInterface.measureTemByY16(myY16Frame[9*120+108]);
                s5[1]=myGuideInterface.measureTemByY16(myY16Frame[15*120+108]);
                s5[2]=myGuideInterface.measureTemByY16(myY16Frame[27*120+108]);
                s5[3]=myGuideInterface.measureTemByY16(myY16Frame[35*120+108]);
                s5[4]=myGuideInterface.measureTemByY16(myY16Frame[45*120+108]);
                s5[5]=myGuideInterface.measureTemByY16(myY16Frame[55*120+108]);
                s5[6]=myGuideInterface.measureTemByY16(myY16Frame[63*120+108]);
                s5[7]=myGuideInterface.measureTemByY16(myY16Frame[72*120+108]);
                s5[8]=myGuideInterface.measureTemByY16(myY16Frame[81*120+108]);
                s5[9]=myGuideInterface.measureTemByY16(myY16Frame[89*120+108]);
                for(int i=0;i<10;i++)
                {
                    String tempSurface=s5[i];
                    float temp = Float.parseFloat(tempSurface);

                    if (text5.getText() != null) {
                        //读出环境温度（字符串）
                        String e_temp = text5.getText().toString();
                        String regEx = "[^0-9]+";
                        Pattern pattern = Pattern.compile(regEx);
                        String[] cs = pattern.split(e_temp);
                        //得到环境温度（数字值）
                        float e_temp_num = Float.parseFloat(cs[0]);
                        //计算人体内部真实温度值
                        String real_temp = myGuideInterface.getHumanTemp(temp, e_temp_num);
                        temp = Float.parseFloat(real_temp);
                    }
                    if (temp <= 37.5&&temp>34) {
                        text_title[4].setBackgroundColor(Color.parseColor("#55000000"));
                        text_title[4].setTextColor(Color.WHITE);
                        text_title[4].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    } else if(temp > 37.5&&temp < 40){
                        text_title[4].setBackgroundColor(Color.parseColor("#FF000000"));
                        text_title[4].setTextColor(Color.RED);
                        text_title[4].setText(String.valueOf(Math.round(temp*10)/10f) + "℃");
                        break;
                    }else {
                        text_title[4].setBackgroundColor(Color.parseColor("#00000000"));
                        text_title[4].setText(" ");
                    }
                }
                //***********************************************************************************************************************//
            }
        });
    }
    /*********************************************************************************************************************************/
    public void saveBitmapFile(Bitmap bitmap,File file)
    {
        try
        {
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /*********************************************************************************************************************************/
    public int getMaxY16(short[] y16Arr){

        short maxY16 = y16Arr[0];

        int length = y16Arr.length;

        for(int i =0 ; i<length ;i++){

            if(maxY16 < y16Arr[i]){
                maxY16 = y16Arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;

    }
    /*********************************************************************************************************************************/
    public int getMinY16(short[] y16Arr){

        short minY16 = y16Arr[0];

        int length = y16Arr.length;

        for(int i =0 ; i<length ;i++){

            if(minY16 >y16Arr[i]){
                minY16 = y16Arr[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
    /*********************************************************************************************************************************/
    public String tempMearsure(int x,int y,short[] myY16Frame)
    {
        int x1=120-(x*120)/width_dect;
        int y1=(y*90)/height_dect;
        if(x1>0&&x1<=rawHeight&&y1>0&&y1<=rawWidth)
        {
            int index=x1+(y1-1)*rawHeight;
            String pointTemp=myGuideInterface.measureTemByY16(myY16Frame[index]);
            return pointTemp;

        }
        else
        {
            return null;
        }
    }
    /*********************************************************************************************************************************/
    @Override
    public Resources getResources() {
        return super.getResources();
    }
    /*********************************************************************************************************************************/
    private float dip2px(int dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale);
    }
    /*********************************************************************************************************************************/
    public Bitmap rawByteArray2RGBABitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0 , width, 0, 0, width, height);
        return bmp;
    }
    /*********************************************************************************************************************************/
    private CascadeClassifier createDetector(Context context, int id) {
        CascadeClassifier javaDetector;
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = context.getResources().openRawResource(id);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, id + ".xml");
            os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            javaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (javaDetector.empty()) {
                javaDetector = null;
            }

            boolean delete = cascadeDir.delete();
            return javaDetector;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != os) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*********************************************************************************************************************************/
    private void getwebinfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://api.k780.com/?app=weather.today&weaid=2816&appkey=49184&sign=a3a74f4163ea96d4f6c86e655251aab0&format=json");
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(5000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(5000);
                    //返回输入流
                    InputStream in = connection.getInputStream();
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    show(result.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    /*********************************************************************************************************************************/
    private void show(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject(result);

                    String temperature_curr = obj.getJSONObject("result").getString("temperature_curr");

                    String humidity = obj.getJSONObject("result").getString("humidity");

                    text5.setText(temperature_curr);
                    text6.setText(humidity);
                    humidity = humidity.replace("%", "");
                    temperature_curr = temperature_curr.replace("℃", "");
                    currentTemp = Float.parseFloat(temperature_curr);
                    currentHumidity = Float.parseFloat((humidity));
                } catch (JSONException j) {

                }
            }
        });
    }
    /*********************************************************************************************************************************/
}

