package android.shadow;

import android.os.IBinder;

public class ShadowServiceEntry {

    public static final String SERVICE_NAME_PACKAGE = "package";
    public static final String SERVICE_NAME_PERMISSIONMGR = "permissionmgr";
    public static final String SERVICE_NAME_ACTIVITY = "activity";
    public static final String SERVICE_NAME_ACTIVITY_TASK = "activity_task";
    public static final String SERVICE_NAME_OS = "os";

    public static enum State {
        UNKNOWN("unknown"),
        SUCCESS("success"),

        CANNOT_GET_ORIGIN_SERVICE("cannot get origin service"),
        CANNOT_GET_INTERFACE_DESCRIPTOR("cannot get interface descriptor"),

        CANNOT_LOAD_INTERFACE_CLASS("cannot load interface class"),
        CANNOT_LOAD_STUB_CLASS("cannot load stub class"),

        STUB_CLASS_DONT_HAVE_METHOD_AS_INTERFACE("stub class dont have method asInterface"),
        CANNOT_GET_ORIGIN_INTERFACE("cannot get origin interface"),

        FAIL_CREATE_PROXY("fail create proxy"),

        ;

        State(String name) {
            this.name = name;
        }

        public final String name;
    }

    public final String name;

    public final State state;

    public final IBinder originService;
    public final IBinder originServiceWrapper;
    public final IBinder proxyService;

    public final String interfaceDescriptor;
    public final String interfaceClassName;
    public final String stubClassName;
    public final String stubProxyClassName;

    public final Class<?> interfaceClass;
    public final Class<?> stubClass;
    public final Class<?> stubProxyClass;

    public final Object originInterface;
    public final Object proxyInterface;

    public final ShadowServiceInvocationHandler handler;

    public ShadowServiceEntry(
            String name,
            State state,

            IBinder originService,
            IBinder originServiceWrapper,
            IBinder proxyService,

            String interfaceDescriptor,
            String interfaceClassName,
            String stubClassName,
            String stubProxyClassName,

            Class<?> interfaceClass,
            Class<?> stubClass,
            Class<?> stubProxyClass,

            Object originInterface,
            Object proxyInterface,

            ShadowServiceInvocationHandler handler
    ) {
        this.name = name;

        this.state = state;

        this.originService = originService;
        this.originServiceWrapper = originServiceWrapper;
        this.proxyService = proxyService;

        this.interfaceDescriptor = interfaceDescriptor;
        this.interfaceClassName = interfaceClassName;
        this.stubClassName = stubClassName;
        this.stubProxyClassName = stubProxyClassName;

        this.interfaceClass = interfaceClass;
        this.stubClass = stubClass;
        this.stubProxyClass = stubProxyClass;

        this.originInterface = originInterface;
        this.proxyInterface = proxyInterface;

        this.handler = handler;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        public final String name;

        public State state = State.UNKNOWN;

        public IBinder originService;
        public IBinder originServiceWrapper;
        public IBinder proxyService;

        public String interfaceDescriptor;
        public String interfaceClassName;
        public String stubClassName;
        public String stubProxyClassName;

        public Class<?> interfaceClass;
        public Class<?> stubClass;
        public Class<?> stubProxyClass;

        public Object originInterface;
        public Object proxyInterface;

        public ShadowServiceInvocationHandler handler;

        public Builder(String name) {
            this.name = name;
        }

        public Builder(ShadowServiceEntry serviceEntry) {
            this.name = serviceEntry.name;

            this.state = serviceEntry.state;

            this.originService = serviceEntry.originService;
            this.originServiceWrapper = serviceEntry.originServiceWrapper;
            this.proxyService = serviceEntry.proxyService;

            this.interfaceDescriptor = serviceEntry.interfaceDescriptor;
            this.interfaceClassName = serviceEntry.interfaceClassName;
            this.stubClassName = serviceEntry.stubClassName;
            this.stubProxyClassName = serviceEntry.stubProxyClassName;

            this.interfaceClass = serviceEntry.interfaceClass;
            this.stubClass = serviceEntry.stubClass;
            this.stubProxyClass = serviceEntry.stubProxyClass;

            this.originInterface = serviceEntry.originInterface;
            this.proxyInterface = serviceEntry.proxyInterface;

            this.handler = serviceEntry.handler;
        }

        public Builder state(State state) {
            this.state = state;
            return this;
        }

        public Builder originService(IBinder originService) {
            this.originService = originService;
            return this;
        }

        public Builder originServiceWrapper(IBinder originServiceWrapper) {
            this.originServiceWrapper = originServiceWrapper;
            return this;
        }

        public Builder proxyService(IBinder proxyService) {
            this.proxyService = proxyService;
            return this;
        }

        public Builder interfaceDescriptor(String interfaceDescriptor) {
            this.interfaceDescriptor = interfaceDescriptor;
            return this;
        }

        public Builder interfaceClassName(String interfaceClassName) {
            this.interfaceClassName = interfaceClassName;
            return this;
        }

        public Builder stubClassName(String stubClassName) {
            this.stubClassName = stubClassName;
            return this;
        }

        public Builder stubProxyClassName(String stubProxyClassName) {
            this.stubProxyClassName = stubProxyClassName;
            return this;
        }

        public Builder interfaceClass(Class<?> interfaceClass) {
            this.interfaceClass = interfaceClass;
            return this;
        }

        public Builder stubClass(Class<?> stubClass) {
            this.stubClass = stubClass;
            return this;
        }

        public Builder stubProxyClass(Class<?> stubProxyClass) {
            this.stubProxyClass = stubProxyClass;
            return this;
        }

        public Builder originInterface(Object originInterface) {
            this.originInterface = originInterface;
            return this;
        }

        public Builder proxyInterface(Object proxyInterface) {
            this.proxyInterface = proxyInterface;
            return this;
        }

        public Builder handler(ShadowServiceInvocationHandler handler) {
            this.handler = handler;
            return this;
        }

        public ShadowServiceEntry build() {
            return new ShadowServiceEntry(
                    name,
                    state,

                    originService,
                    originServiceWrapper,
                    proxyService,

                    interfaceDescriptor,
                    interfaceClassName,
                    stubClassName,
                    stubProxyClassName,

                    interfaceClass,
                    stubClass,
                    stubProxyClass,

                    originInterface,
                    proxyInterface,
                    handler
            );
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShadowServiceEntry that = (ShadowServiceEntry) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (state != that.state) return false;
        if (originService != null ? !originService.equals(that.originService) : that.originService != null)
            return false;
        if (originServiceWrapper != null ? !originServiceWrapper.equals(that.originServiceWrapper) : that.originServiceWrapper != null)
            return false;
        if (proxyService != null ? !proxyService.equals(that.proxyService) : that.proxyService != null)
            return false;
        if (interfaceDescriptor != null ? !interfaceDescriptor.equals(that.interfaceDescriptor) : that.interfaceDescriptor != null)
            return false;
        if (interfaceClassName != null ? !interfaceClassName.equals(that.interfaceClassName) : that.interfaceClassName != null)
            return false;
        if (stubClassName != null ? !stubClassName.equals(that.stubClassName) : that.stubClassName != null)
            return false;
        if (stubProxyClassName != null ? !stubProxyClassName.equals(that.stubProxyClassName) : that.stubProxyClassName != null)
            return false;
        if (interfaceClass != null ? !interfaceClass.equals(that.interfaceClass) : that.interfaceClass != null)
            return false;
        if (stubClass != null ? !stubClass.equals(that.stubClass) : that.stubClass != null)
            return false;
        if (stubProxyClass != null ? !stubProxyClass.equals(that.stubProxyClass) : that.stubProxyClass != null)
            return false;
        if (originInterface != null ? !originInterface.equals(that.originInterface) : that.originInterface != null)
            return false;
        if (proxyInterface != null ? !proxyInterface.equals(that.proxyInterface) : that.proxyInterface != null)
            return false;
        return handler != null ? handler.equals(that.handler) : that.handler == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (originService != null ? originService.hashCode() : 0);
        result = 31 * result + (originServiceWrapper != null ? originServiceWrapper.hashCode() : 0);
        result = 31 * result + (proxyService != null ? proxyService.hashCode() : 0);
        result = 31 * result + (interfaceDescriptor != null ? interfaceDescriptor.hashCode() : 0);
        result = 31 * result + (interfaceClassName != null ? interfaceClassName.hashCode() : 0);
        result = 31 * result + (stubClassName != null ? stubClassName.hashCode() : 0);
        result = 31 * result + (stubProxyClassName != null ? stubProxyClassName.hashCode() : 0);
        result = 31 * result + (interfaceClass != null ? interfaceClass.hashCode() : 0);
        result = 31 * result + (stubClass != null ? stubClass.hashCode() : 0);
        result = 31 * result + (stubProxyClass != null ? stubProxyClass.hashCode() : 0);
        result = 31 * result + (originInterface != null ? originInterface.hashCode() : 0);
        result = 31 * result + (proxyInterface != null ? proxyInterface.hashCode() : 0);
        result = 31 * result + (handler != null ? handler.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShadowServiceEntry{" +
                "name='" + name + '\'' +
                ", state=" + state +
                ", originService=" + originService +
                ", originServiceWrapper=" + originServiceWrapper +
                ", proxyService=" + proxyService +
                ", interfaceDescriptor='" + interfaceDescriptor + '\'' +
                ", interfaceClassName='" + interfaceClassName + '\'' +
                ", stubClassName='" + stubClassName + '\'' +
                ", stubProxyClassName='" + stubProxyClassName + '\'' +
                ", interfaceClass=" + interfaceClass +
                ", stubClass=" + stubClass +
                ", stubProxyClass=" + stubProxyClass +
                ", originInterface=" + originInterface +
                ", proxyInterface=" + proxyInterface +
                ", handler=" + handler +
                '}';
    }
}
