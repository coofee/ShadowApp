package android.shadow;

import android.os.IBinder;
import android.os.Parcel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ShadowIBinderInvocationHandler implements InvocationHandler {
    private final String mServiceName;

    private final IBinder mOriginBinder;

    private final Object mProxyInterface;

    public ShadowIBinderInvocationHandler(String serviceName, IBinder originBinder) {
        this(serviceName, originBinder, null);
    }

    public ShadowIBinderInvocationHandler(String serviceName, IBinder originBinder, Object proxyInterface) {
        this.mServiceName = serviceName;
        this.mOriginBinder = originBinder;
        this.mProxyInterface = proxyInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            final String name = method.getName();
            if (mProxyInterface != null && "queryLocalInterface".equals(name)) {
                ShadowLog.e("invoke service=" + mServiceName + " queryLocalInterface, return " + mProxyInterface);
                return mProxyInterface;
            }

            if (mOriginBinder != null && "transact".equals(name)) {
                // TODO check TransactionTooLargeException
                Parcel data = (Parcel) args[1];
                if (data != null && data.dataSize() > 0) {
                    ShadowLog.d("service=" + mServiceName + " transact request data parcelSize=" + data.dataSize(), new Throwable());
                }

                Object result = ReflectUtil.wrapReturnValue(method.invoke(mOriginBinder, args), method.getReturnType());

                Parcel reply = (Parcel) args[2];
                if (reply != null && reply.dataSize() > 0) {
                    ShadowLog.d("service=" + mServiceName + " transact reply data parcelSize=" + reply.dataSize(), new Throwable());
                }
                return result;
            }

            return ReflectUtil.wrapReturnValue(method.invoke(mOriginBinder, args), method.getReturnType());
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
