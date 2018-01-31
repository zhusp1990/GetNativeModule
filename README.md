##ReactNative调用原生组件（Android）

###以[crazyboycode/react-native-splash-screen](https://github.com/crazycodeboy/react-native-splash-screen)为例，给一个RN应用添加一个应用启动屏，以掩盖app启动白屏的问题。
####说明：该组件应用场景是在app启动时，由于RN渲染需要时间，因手机性能的问题可能会导致应用2到3秒的白屏时间。因此为了解决该问题，我们给RN应用添加一个Native启动屏，以掩盖这种因启动白屏而引起的不友好的用户交互。
###**准备工作**
####开发环境：window10
####开发工具：Android Studio、WenStorm
###**开发步骤**
+ **用WebStorm打开一个RN工程，并用Android Studio打开该工程下对应的android项目**
* **创建原生组件SplashScreen，此组件为RN中将要调用到的原生组件**
```
package com.myunionpaydemo;

import android.app.Activity;
import android.app.Dialog;

import java.lang.ref.WeakReference;

public class SplashScreen {
    private static Dialog mSplashDialog;
    private static WeakReference<Activity> mActivity;

    public static void show(final Activity activity, final boolean fullScreen) {
        if (activity != null) {
            mActivity=new WeakReference<>(activity);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!activity.isFinishing()) {
                        mSplashDialog = new Dialog(activity);
                        mSplashDialog.setContentView(R.layout.layout_splash_screen);
                        mSplashDialog.setCancelable(false);
                        if (!mSplashDialog.isShowing()) {
                            mSplashDialog.show();
                        }
                    }
                }
            });
        }
    }

    /**
     * 关闭启动屏
     */
    public static void hide(Activity activity) {
        if (activity == null)
            activity = mActivity.get();

        if (activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSplashDialog != null && mSplashDialog.isShowing()) {
                    mSplashDialog.dismiss();
                }
            }
        });
    }

}
```

* **向JS模块提供SplashScreen组件**
JS不能直接调用Java，所以我们需要为他们搭建一个桥梁**Native Modules**
**首先，创建一个`ReactContextBaseJavaModule`类型的类，供JS调用**
```
package com.myunionpaydemo;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class SplashScreenModule extends ReactContextBaseJavaModule {

    public SplashScreenModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SplashScreen";
    }

    /**打开启动屏*/
    @ReactMethod
    public void show(){
        SplashScreen.show(getCurrentActivity(),true);
    }

    /**关闭启动屏*/
    @ReactMethod
    public void hide(){
        SplashScreen.hide(getCurrentActivity());
    }
}

```
*注意，这里的`show()`和`hide()`是最终在RN的JS代码中调用的方法，因此需要添加`@ReactMethod`注解*

* **向`MainApplication`中注册`SplashScreenModule`组件**
```
@Override
protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new SplashScreenReactPackage()
     );
}
```
2.在RN应用首页调用相应的方法（此处根据示例要求，我们在`componentDidMount`方法调用`hide()`方法，即首页画面已经渲染完成，可以关闭加载启动屏）
```
import SplashScreen from "./SplashScreen";
...
//渲染之前调用原生组件Dialog遮盖白屏
componentWillMount(){
        SplashScreen.show();
    }

componentDidMount(){
        SplashScreen.hide();
    }
render() {
        return (
            <View style={styles.container}>
                <Text style={styles.welcome}>
                    Welcome to React Native!
                </Text>
                <Text style={styles.instructions}>
                    To get started, edit App.js
                </Text>
                <Text style={styles.instructions}>
                    {instructions}
                </Text>
                {this.getLastView()}
            </View>
        );
    }
    
    getLastView(){
        let i=500000000;
        //添加循环，模拟复杂页面渲染时的耗时时间
        while (i){
            i--;
        }
        return(<Text style={styles.welcome}>
            Welcome to React Native!
        </Text>);
    }
...
```
至此，调用原生组件的工作就全部完成,最后运行看下效果：
![这里写图片描述](http://img.blog.csdn.net/20180131095201022?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvenNwNzY1MDk4MDg0/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
短暂的启动屏一闪而过，效果还不是很好，还是会有短暂的白屏，不过调用原生组件的功能成功实现了Y(·_·)Y

**参考资料**
1. [crazycodeboy/react-native-splash-screen](https://github.com/crazycodeboy/react-native-splash-screen)
2. [React-Native-启动白屏问题解决方案,教程](http://www.devio.org/2016/09/30/React-Native-%E5%90%AF%E5%8A%A8%E7%99%BD%E5%B1%8F%E9%97%AE%E9%A2%98%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88,%E6%95%99%E7%A8%8B/)