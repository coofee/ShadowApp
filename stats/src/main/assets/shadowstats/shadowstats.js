'use strict';

rpc.exports = {
    init: function (stage, parameters) {
        console.log('[init]', stage, JSON.stringify(parameters));

        Interceptor.attach(Module.getExportByName(null, 'open'), {
            onEnter: function (args) {
                var path = args[0].readUtf8String();
                console.log('open("' + path + '")');
            }
        });
    },
    dispose: function () {
        console.log('[dispose]');
    }
};

console.log("Waiting for Java..");

while (!Java.available) {
    console.log("Not available...");
}

console.log("Java available.")

// class Bridge {
//     Log = Java.use("android.util.Log");
//
//     ShadowStatsBridge = Java.use('com.coofee.shadow.stats.ShadowStatsBridge');
//
//     on(type, json) {
//         Log.d("Frida", msg + ", json=" + json)
//         console.log(type, msg)
//         send({type, json})
//
//         if (typeof json != 'string') {
//             json = JSON.stringify(json)
//         }
//
//         ShadowStatsBridge.on(type, json)
//     }
// }

function main() {
    Java.perform(function () {
        var Log = Java.use("android.util.Log")
        var Throwable = Java.use("java.lang.Throwable")

        function log(msg) {
            Log.d("Frida", msg)
            console.log(msg)
            send(msg)
        }

        log('load hook script...')

        // try {
            var ShadowStatsBridge = Java.use('com.coofee.shadow.stats.ShadowStatsBridge');

            function on(type, json) {
                if (typeof json != 'string') {
                    json = JSON.stringify(json)
                }

                ShadowStatsBridge.on(type, json)
            }

            on("attach", {status: true})
        // } catch (e) {
        //     log("fail use ShadowStatsBridge; msg=" + e + JSON.stringify(e))
        // }

        // hook Activity
        var Activity = Java.use("android.app.Activity");
        Activity.onCreate.overload('android.os.Bundle').implementation = function (bundle) {
            log("Activity onCreate: this=" + this)
            var activity = this.toString();
            var event = 'onCreate';
            try {
                on("activity", {activity, event})
            } catch (e) {
                Log.e('ShadowStatsManager', "e=" + e)
            }

            this.onCreate(bundle);
        }
        Activity.onResume.implementation = function () {
            log("Activity onResume: this=" + this)
            var activity = this.toString();
            var event = 'onResume';
            on("activity", {activity, event})
            this.onResume();
        }

        // hook Thread
        var Thread = Java.use("java.lang.Thread");
        Thread.start.implementation = function () {
            var thread = this.toString();
            var event = "start"
            var by = Thread.currentThread().toString();
            var stacktrace = Log.getStackTraceString(Throwable.$new());
            log("Thread start: this=" + thread + ", by " + by + ", stacktrace=" + stacktrace)
            on("thread", {thread, event, by, stacktrace})
            return this.start();
        }
    })
}

setImmediate(function () {
    try {
        main()
    } catch (e) {
        var Log = Java.use("android.util.Log")
        Log.e('ShadowStatsManager', "e=" + e + ", json=" + JSON.stringify(e))
    }
})
