package com.jd.ad.demo;

import android.os.Build;
import android.webkit.WebView;

import androidx.multidex.MultiDexApplication;

import com.jd.ad.demo.utils.AppUtils;
import com.jd.ad.demo.utils.Constants;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.bl.initsdk.JADPrivateController;
import com.jd.ad.sdk.bl.initsdk.JADYunSdk;
import com.jd.ad.sdk.bl.initsdk.JADYunSdkConfig;
import com.jd.ad.sdk.dl.baseinfo.JADLocation;

/**
 * OAID 怎么获取请 参看 移动安全联盟?(http://www.msa-alliance.cn/col.jsp?id=120)
 * <p>
 * 接入详情 请参看 ：https://help-sdk-doc.jd.com/ansdkDoc/access_docs/Android/SDK%E9%9B%86%E6%88%90/Android%20SDK%E7%9A%84%E4%B8%8B%E8%BD%BD%E4%B8%8E%E9%9B%86%E6%88%90.html
 */
public class DemoApplication extends MultiDexApplication {

    /**
     * 接口用于获取 OAID
     */
    private String oaid = "";

    @Override
    public void onCreate() {
        super.onCreate();

        // android 9.0 中为了改善应用稳定性和数据完整性，应用没法再让多个进程共用同一 WebView 数据目录。
        // 多进程涉及WebView的使用，建议在SDK初始化之前调用WebView.setDataDirectorySuffix(),自定义数据路径，防止WebView异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = AppUtils.getCurProcessName(this);
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
        //初始化京东广告 SDK
        if (ThreadChooseUtils.isMainThread(this)) {
            initJDSdk();
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    initJDSdk();
                }
            });
        }

    }


    /**
     * 初始化AN SDK
     */
    private void initJDSdk() {
        JADYunSdkConfig config = new JADYunSdkConfig
                .Builder()
                .setAppId(Constants.APP_ID) //媒体在平台申请的 APP ID
                .setEnableLog(BuildConfig.DEBUG) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .setPrivateController(createPrivateController()) //隐私信息控制设置，此项必须设置！！
                .setSupportMultiProcess(true)//是否支持多进程，true表示支持，默认不支持
                .build();

        JADYunSdk.init(this, config);
    }

    /**
     * 创建 隐私信息控制 实例，媒体可以自主控制是否提供权限给 广告 sdk使用
     * 创建 隐私信息控制 实例，媒体可以自主控制是否提供权限给 广告 sdk使用
     * 重要！！重要！！
     * 为了保证广告的填充率，oaid配置项必须实现，并传入有效值
     * <p>
     * 其他可以采用默认配置(不实现相关方法)
     *
     * @return
     */
    private JADPrivateController createPrivateController() {
        return new JADPrivateController() {
            /**
             * 开发者可以传入OAID
             *
             * @return OAID
             */
            @Override
            public String getOaid() {
                return oaid;
            }

            /**
             * 是否允许SDK主动使用地理位置信息
             *
             * @return true可以获取，false禁止获取。默认为true
             */
            public boolean isCanUseLocation() {
                return true;
            }

            /**
             * 是否允许SDK主动使用IP信息
             *
             * @return true可以使用，false禁止使用。默认为true
             */
            @Override
            public boolean isCanUseIP() {
                return true;
            }

        };
    }
}
