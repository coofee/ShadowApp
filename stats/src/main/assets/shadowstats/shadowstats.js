'use strict';

function createBridge() {
    const Log = Java.use("android.util.Log")
    const Throwable = Java.use("java.lang.Throwable")
    const ShadowStatsBridge = Java.use('com.coofee.shadow.stats.ShadowStatsBridge');

    const bridge = {};

    bridge.getStackTraceString = function (msg) {
        if (msg == undefined || msg == null || msg == '') {
            return Log.getStackTraceString(Throwable.$new());
        }

        if (bridge.isInstance('java.lang.Throwable', msg)) {
            return Log.getStackTraceString(msg);
        }

        return Log.getStackTraceString(Throwable.$new(msg));
    }

    bridge.log = function (msg) {
        if (Shadow.DEBUG && Shadow.OPEN_LOG) {
            Log.d(Shadow.TAG, msg)
            console.log(msg)
        }

        send({type: 'log', 'msg': msg})
    }

    bridge.on = function (type, json) {
        if (json === undefined || json == null) {
            json = {};
        }

        let originJson = json;
        if (typeof json != 'string') {
            json = JSON.stringify(json)
        }

        if (Shadow.DEBUG && Shadow.OPEN_LOG) {
            Log.d(Shadow.TAG, "type=" + type + ", json=" + json)
            console.log(type, json)
        }

        ShadowStatsBridge.on(type, json)
        send({type, json: originJson})
    }

    bridge.isInstance = function (className, javaObject) {
        return ShadowStatsBridge.isInstance(className, javaObject)
    }

    return bridge;
}

function hookOverloads(className, func) {
    let clazz = Java.use(className);
    let overloads = clazz[func].overloads;
    for (var i in overloads) {
        if (overloads[i].hasOwnProperty('argumentTypes') || overloads[i]['argumentTypes'] != undefined) {
            var parameters = [];

            var curArgumentTypes = overloads[i].argumentTypes, args = [], argLog = '[';
            for (var j in curArgumentTypes) {
                var cName = curArgumentTypes[j].className;
                parameters.push(cName);
                argLog += "'(" + cName + ") ' + v" + j + ",";
                args.push('v' + j);
            }
            argLog += ']';

            var script = "var ret = this." + func + '(' + args.join(',') + ");\n"
                + "console.log(JSON.stringify(" + argLog + "));\n"
                + "var statsJson = {type: 'hook', name: '" + className + "', event: '" + func + "', className: '" + className + "', func: '" + func + "', args:" + argLog + ", stacktrace:Shadow.Bridge.getStackTraceString(this)" + "};\n"
                + "Shadow.Bridge.on('stats', statsJson);\n"
                + "return ret;"

            args.push(script);

            // console.log('args=' + args + ', parameters=' + parameters);

            // javascript创建函数
            // var function_name = new Function(arg1, arg2, ..., argN, function_body)
            // 与上面等效 Function.apply(null , [arg1, arg2, ..., argN, function_body])
            clazz[func].overload.apply(clazz[func], parameters).implementation = Function.apply(null, args)
            // let implementation = clazz[func].overload.apply(clazz[func], parameters).implementation = Function.apply(null, args)
            // console.log('args=' + args + ', parameters=' + parameters + ', implementation=' + implementation + ', typeof(implementation)=' + (typeof implementation))
        }
    }
}

const Shadow = {}
Object.defineProperty(Shadow, "Bridge", {

    get: function () {
        this.bridge = this.bridge || createBridge();
        return this.bridge;
    }
});

rpc.exports = {
    init: function (stage, parameters) {
        console.log('[init]', stage, JSON.stringify(parameters));

        Shadow[stage] = parameters;
        Shadow.parameters = parameters
        if (parameters === undefined) {
            Shadow.OPEN_LOG = true
            Shadow.DEBUG = true;
            Shadow.TAG = 'ShadowStatsManager'
        } else {
            Shadow.OPEN_LOG = parameters.open_log
            Shadow.DEBUG = !(parameters.is_release || false)
            Shadow.TAG = parameters.tag || 'ShadowStatsManager'
        }

        Shadow.Bridge.on("init", {type: 'init'})

        Interceptor.attach(Module.getExportByName(null, 'open'), {
            onEnter: function (args) {
                var path = args[0].readUtf8String();
                console.log('open("' + path + '")');
            }
        });
    },
    dispose: function () {
        console.log('[dispose]');
        Shadow.Bridge.on("dispose", {type: 'dispose'})
    }
};

console.log("Waiting for Java.; ");

while (!Java.available) {
    console.log("Not available...");
}

console.log("Java available.")

function main() {
    Java.perform(function () {
        const Log = Java.use("android.util.Log")
        const Throwable = Java.use("java.lang.Throwable")

        Shadow.Bridge.log('load hook script; Shadow=' + JSON.stringify(Shadow))

        // hook Activity
        const Activity = Java.use("android.app.Activity");
        Activity.onCreate.overload('android.os.Bundle').implementation = function (bundle) {
            Shadow.Bridge.on("stats", {type: 'activity', name: this.toString(), event: 'onCreate'})
            return this.onCreate(bundle);
        }
        Activity.onResume.implementation = function () {
            Shadow.Bridge.on("stats", {type: 'activity', name: this.toString(), event: 'onResume'})
            return this.onResume();
        }

        // hook Thread
        const Thread = Java.use("java.lang.Thread");
        Thread.start.implementation = function () {
            let thread = this.toString();
            let event = "start"
            let by = Thread.currentThread().toString();
            let stacktrace = Log.getStackTraceString(Throwable.$new());
            Shadow.Bridge.on("stats", {type: 'thread', name: thread, event, by, stacktrace})
            return this.start();
        }
        Thread.exit.implementation = function () {
            var thread = this.toString();
            var event = "exit"
            Shadow.Bridge.on("stats", {type: 'thread', name: thread, event})
            return this.exit()
        }

        // 访问未授权的代码时，会抛出SecurityException，所以拦截SecurityException的构造函数，然后打印其堆栈信息，就是访问权限的地方。
        // hook SecurityException
        hookOverloads('java.lang.SecurityException', '$init');

        // const SecurityException = Java.use('java.lang.SecurityException')
        // SecurityException.$init.overload('java.lang.String').implementation = function(msg) {
        //     let stacktrace = Log.getStackTraceString(Throwable.$new());
        //     Shadow.Bridge.log('create SecurityException with msg=' + msg + ', stacktrace=' + stacktrace)
        //     return this.$init(msg)
        // }
    })
}

setImmediate(function () {
    try {
        main()
    } catch (e) {
        console.log(e)
        const Log = Java.use("android.util.Log")
        Log.e(Shadow.TAG, "e=" + e + ", json=" + JSON.stringify(e))
    }
})
