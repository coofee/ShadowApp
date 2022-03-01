如何使用`Shadow`库拦截Android系统服务？

下面我们将以Android系统的`TelephonyManager`服务的`getDeviceId()`方法为例，一步步实现服务拦截。

# 0x00 添加Shadow依赖项

添加`Shadow`和`FreeReflection`依赖到项目中，其中`Shadow`提供拦截系统服务功能，`FreeReflection`则用于解决访问Android系统的`hidden-apis`。

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation 'com.github.tiann:FreeReflection:3.1.0'
    implementation 'com.github.coofee.ShadowApp:shadow:<latest version>'
}
```

最新版本：[![](https://jitpack.io/v/coofee/ShadowApp.svg)](https://jitpack.io/#coofee/ShadowApp)

# 0x01 初始化Shadow库

在`Application`的`attachBaseContext()`方法中，初始化`Shadow`库，模板代码如下：

```java
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initShadowManager(base);
    }

    private void initShadowManager(Context base) {
        if (Reflection.unseal(base) != 0) {
            Log.e(ShadowServiceManager.TAG, "fail Reflection.unseal().");
            return;
        }
        Log.e(ShadowServiceManager.TAG, "success Reflection.unseal().");

        ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this)
                   .debug(BuildConfig.DEBUG)
                   .devTools(ShadowDevTools.DEFAULT_DEV_TOOLS)
                  .logMode(ShadowLog.DEBUG)
                  .interceptAll(true)
//                .addPackageOrClassNamePrefix("com.coofee.shadowapp")
                  .add(new ServiceInterceptor())
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }
}
```

## 1. `ShadowConfig`说明

**ShadowConfig的配置仅对通过`add`方法添加的`Interceptor`生效**

* `debug`方法用于设置deubg模式; debug模式下devTools生效。
* `devTools`方法可以设置`ShadowDevTools`, 仅在debug模式下生效，当有服务方法被调用时，就会被触发，可以用来监控服务方法调用。
* `logMode`方法指定日志开关。
* `logImpl`方法指定日志实现类，可以更换为自己的实现类，默认会打印日志到控制台，日志tag为`ShadowServiceManager`。
* `add`方法添加针对Service的拦截器，在拦截器的实现中会指定Service的名字。
* `interceptAll`为`true`时，所有已添加的拦截器生效; 默认值为`false`。
* `addPackageOrClassNamePrefix`指定包名前缀列表，已添加的拦截器只拦截包含包名前缀的Service调用(通过运行期间分析Service的调用栈，当Service的调用栈包含任一指定包名前缀时，则使用拦截器进行拦截)；默认为空列表。


> 注意：
> 当`interceptAll`为`false`，同时`addPackageOrClassNamePrefix`未添加包名前缀列表时，拦截器不生效。
> 当`intercepAll`为`true`时，会忽略`addPackageOrClassNamePrefix`添加的包名前缀列表，拦截器生效。

## 2. `ShadowServiceManager`说明

`ShadowServiceManager`只拦截通过`ShadowConfig`添加的拦截器中指定的系统服务，通过`init`方法传入`ShadowConfig`进行初始化。

## 3. `ShadowServiceInterceptor`说明

在`Shadow`中，所有的拦截器均需要实现`ShadowServiceInterceptor`接口，

```java
public interface ShadowServiceInterceptor {

    // 拦截系统服务方法
    Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable;

    // 指定待拦截的系统服务名称
    String provideInterceptServiceName();

    // 指定拦截的系统服务方法名称
    Set<String> provideInterceptMethodNames();

    // true=拦截指定系统服务的全部方法; 默认为false，此时会使用`provideInterceptMethodNames()`返回值确定拦截的方法。
    default boolean interceptAllMethod() {
        return false;
    }
}
```

在编写拦截器时，最重要的是获取以下两点：

1. 获取待拦截服务的名称。
2. 获取待拦截服务对应的方法。

# 0x02 编写`TelephonyManager`拦截器

下面我们以`TelephonyManager.getDeviceId()`为例，分析如果获取待拦截的服务名称和方法，从而实现拦截器。

## 1. 获取待拦截服务方法名

以`android-30`代码为基准，打开`TelephonyManager`的源代码，可以看到`getDeviceId`有两个方法，其中一个无参数，一个有参数，
我们这里以无参的方法1进行分析，方法2可以参考方法1的分析，自行实现，两个方法的代码如下：

> 注意: 不同android版本的服务实现不同，需要进行适配，避免遗漏。如getDeviceId()方法的实现在`android-26`中，调用的服务方法名称就是`getDeviceId`.

```java

// 方法1:
public String getDeviceId() {
    try {
        ITelephony telephony = getITelephony();
        if (telephony == null)
            return null;
        return telephony.getDeviceIdWithFeature(mContext.getOpPackageName(),
                mContext.getAttributionTag());
    } catch (RemoteException ex) {
        return null;
    } catch (NullPointerException ex) {
        return null;
    }
}

// 方法2:
public String getDeviceId(int slotIndex) {
    // FIXME this assumes phoneId == slotIndex
    try {
        IPhoneSubInfo info = getSubscriberInfoService();
        if (info == null)
            return null;
        return info.getDeviceIdForPhone(slotIndex, mContext.getOpPackageName(),
                mContext.getAttributionTag());
    } catch (RemoteException ex) {
        return null;
    } catch (NullPointerException ex) {
        return null;
    }
}
```

从方法1的代码中可以看到，`TelephonyManager.getDeviceId()`是通过调用`ITelephony.getDeviceIdWithFeature()`方法获取的，所以我们需要拦截的系统服务为`ITelephony`，需要拦截的方法为`getDeviceIdWithFeature`。

## 2. 获取待拦截服务名称

通过分析代码可以看到调用链如下，最终可以知道`ITelephony`对应的服务名称为`Context.TELEPHONY_SERVICE`。

```java
class TelephonyManager {

    public String getDeviceId() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony == null)
                return null;

            return telephony.getDeviceIdWithFeature(mContext.getOpPackageName(),     // ①
                    mContext.getAttributionTag());
        } catch (RemoteException ex) {
            return null;
        } catch (NullPointerException ex) {
            return null;
        }
    }
                |
                ∨
    private ITelephony getITelephony() {
        return ITelephony.Stub.asInterface(TelephonyFrameworkInitializer              // ②
                .getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
    }
}
                |
                ∨
class TelephonyFrameworkInitializer {
    public static TelephonyServiceManager getTelephonyServiceManager() {              // ③
        return sTelephonyServiceManager;
    }
}
                |
                ∨
class TelephonyServiceManager {

    public ServiceRegisterer getTelephonyServiceRegisterer() {
        return new ServiceRegisterer(Context.TELEPHONY_SERVICE);                      // ④
    }

    public static final class ServiceRegisterer {
        private final String mServiceName;

        public ServiceRegisterer(String serviceName) {
            mServiceName = serviceName;                                               // ⑤
        }

                        |
                        ∨
        public void register(@NonNull IBinder service) {
            ServiceManager.addService(mServiceName, service);                         // ⑥
        }

        public IBinder get() {
            return ServiceManager.getService(mServiceName);
        }
    }
}
```

## 3. 创建对应拦截器

知道了需要拦截的服务名称和方法，创建`ITelephonyInterceptor`类，然后令其实现`android.shadow.ShadowServiceInterceptor`接口，

1. 在`provideInterceptServiceName()`方法中返回拦截的服务名称`Context.TELEPHONY_SERVICE`。
2. 在`provideInterceptMethodNames()`方法中返回拦截的服务方法`getDeviceIdWithFeature`。
3. 在`invoke()`方法中对服务方法进行拦截，这里我们只是打印一条日志，然后执行服务原有逻辑。

```java
public class ITelephonyInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "getDeviceId", // android-26 8.0.0
            "getDeviceIdWithFeature"
    )));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("ITelephonyInterceptor intercept method=" + method.getName());
        return ReflectUtil.wrapReturnValue(method.invoke(service, args), method.getReturnType());
    }

    @Override
    public String provideInterceptServiceName() {
        return Context.TELEPHONY_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }

}
```

## 4. 快速获取待拦截服务名字和方法

通过在`ShadowConfig`配置中启用`devTools`和`debug`参数

```java
ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this)
           .debug(true)
           .devTools(ShadowDevTools.DEFAULT_DEV_TOOLS)
           .logMode(ShadowLog.DEBUG)
           .interceptAll(true)
           .build();

ShadowServiceManager.init(shadowConfigBuilder.build());
```

然后在app代码中直接调用需要拦截的服务方法，如`telephonyManager.getDeviceId()`。

```java
findViewById(R.id.test_get_device_id).setOnClickListener(v -> {
    try {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        ShadowLog.e("MainActivity.test_get_device_id; getDeviceId=" + telephonyManager.getDeviceId());
    } catch (Throwable e) {
        ShadowLog.e("fail test_get_device_id.getDeviceId", e);
    }
});
```

运行app，触发`telephonyManager.getDeviceId()`相关逻辑。最后再通过`ShadowDevTools`关键字过滤日志，即可获取到需要拦截的服务名字和方法。

```
D/ShadowServiceManager: ShadowDevTools; serviceName=phone, service=com.android.internal.telephony.ITelephony$Stub$Proxy@2370e51, method=public abstract java.lang.String com.android.internal.telephony.ITelephony.getDeviceId(java.lang.String) throws android.os.RemoteException, args=[com.coofee.shadowapp]
```

可以看到待拦截服务名称为`phone`，方法名为`getDeviceId`。


# 0x03 注册与运行

## 1. 添加拦截器到配置

将编写好的拦截器`ITelephonyInterceptor`添加到`ShadowConfig`配置中。

```java

ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this)
          ...
          .add(new ITelephonyInterceptor())
;

```

## 2. 运行

编译运行App，可以看到日志输出如下：

```
// android-26
D/ShadowServiceManager: ITelephonyInterceptor intercept method=getDeviceId

// android-30
D/ShadowServiceManager: ITelephonyInterceptor intercept method=getDeviceIdWithFeature
```
