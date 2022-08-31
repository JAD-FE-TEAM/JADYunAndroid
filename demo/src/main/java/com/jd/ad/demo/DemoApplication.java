package com.jd.ad.demo;

import android.app.Application;
import android.os.Build;
import android.webkit.WebView;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
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
public class DemoApplication extends Application {

    /**
     * 接口用于获取 OAID
     */
    private IdSupplier mIdSupplier;

    @Override
    public void onCreate() {
        super.onCreate();

//        多进程支持
//        解决多进程下，28以上webview 崩溃问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = AppUtils.getCurProcessName(this);
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
        //初始化OAID
        initOaid();

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
     * 初始化OAID 目的在用户没有授权获取IMEI 权限时，
     * 广告SDK 可以通过 OAID 提高广告的填充率,
     * 具体接入方式，或aar 下载请到 移动安全联盟官网下载。
     * http://www.msa-alliance.cn/col.jsp?id=120
     */
    private void initOaid() {
        try {
            MdidSdkHelper.InitSdk(this, BuildConfig.DEBUG, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean b, final IdSupplier idSupplier) {
                    if (idSupplier != null && idSupplier.isSupported()) {
                        mIdSupplier = idSupplier;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
                .build();

        JADYunSdk.init(this, config);
    }

    /**
     * 创建 隐私信息控制 实例，媒体可以自主控制是否提供权限给 广告 sdk使用
     * 重要！！重要！！
     * 为了保证广告的填充率，注意oaid和imei的配置：
     * （1）oaid配置项必须实现，并传入有效值
     * (2)imei的配置与isCanUsePhoneState有关（isCanUsePhoneState配置项默认为BuildConfig.DEBUG）
     * 当isCanUsePhoneState 配置为false，不允许SDK主动获取imei，但是需要接入方主动传入imei
     * 当isCanUsePhoneState 配置为BuildConfig.DEBUG，允许SDK主动获取imei，但必须保证接入方已经动态申请了 Manifest.permission.READ_PHONE_STATE，否则SDK会获取失败
     * <p>
     * 其他可以采用默认配置(不实现相关方法)
     *
     * @return
     */
    private JADPrivateController createPrivateController() {
        return new JADPrivateController() {
            /**
             * 是否允许SDK主动使用手机硬件参数，如：imei
             *
             * @return BuildConfig.DEBUG可以使用，false禁止使用。默认为BuildConfig.DEBUG
             */
            public boolean isCanUsePhoneState() {
                return BuildConfig.DEBUG;
            }

            /**
             * 当 isCanUsePhoneState=false 时，
             * 可传入 imei 信息，sdk使用您传入的 imei 信息
             *
             * @return imei信息
             */
            public String getImei() {
                return "";
            }

            /**
             * 开发者可以传入OAID
             *
             * @return OAID
             */
            @Override
            public String getOaid() {
                return mIdSupplier != null ? mIdSupplier.getOAID() : "";
            }

            /**
             * 是否允许SDK主动使用地理位置信息
             *
             * @return BuildConfig.DEBUG可以获取，false禁止获取。默认为BuildConfig.DEBUG
             */
            public boolean isCanUseLocation() {
                return BuildConfig.DEBUG;
            }

            /**
             * 当isCanUseLocation=false时，可传入地理位置信息，sdk使用您传入的地理位置信息
             *
             * @return 地理位置参数
             */
            public JADLocation getLocation() {
                return new JADLocation();
            }

            /**
             * 是否允许SDK主动使用IP信息
             *
             * @return BuildConfig.DEBUG可以使用，false禁止使用。默认为BuildConfig.DEBUG
             */
            @Override
            public boolean isCanUseIP() {
                return BuildConfig.DEBUG;
            }

            /**
             * 当 isCanUseIP = false 时，
             * 可传入IP信息，sdk使用您传入的IP信息
             *
             * @return IP
             */
            @Override
            public String getIP() {
                return "";
            }
        };
    }

}
