package com.coofee.shadowapp.test;

public class ScriptUtil {
    public static final String SCRIPT = "'use strict';\n" +
            "\n" +
            "// rpc.exports = {\n" +
            "//     init: function (stage, parameters) {\n" +
            "//         console.log('[init]', stage, JSON.stringify(parameters));\n" +
            "\n" +
            "//         Interceptor.attach(Module.getExportByName(null, 'open'), {\n" +
            "//             onEnter: function (args) {\n" +
            "//                 var path = args[0].readUtf8String();\n" +
            "//                 console.log('open(\"' + path + '\")');\n" +
            "//             }\n" +
            "//         });\n" +
            "//     },\n" +
            "//     dispose: function () {\n" +
            "//         console.log('[dispose]');\n" +
            "//     }\n" +
            "// };\n" +
            "\n" +
            "console.log(\"Waiting for Java..\");\n" +
            "\n" +
            "while(!Java.available) {\n" +
            "    console.log(\"Not available...\");\n" +
            "}\n" +
            "\n" +
            "function main() {\n" +
            "    Java.perform(function() {\n" +
            "        console.log('load hook script...')\n" +
            "\n" +
            "        var Log = Java.use(\"android.util.Log\")\n" +
            "        var Throwable = Java.use(\"java.lang.Throwable\")\n" +
            "\n" +
            "        function log(msg) {\n" +
            "            Log.d(\"ScriptUtil\", msg)\n" +
            "            console.log(msg)\n" +
            "            send(msg)\n" +
            "        }\n" +
            "\n" +
            "        // hook Activity\n" +
            "        var Activity = Java.use(\"android.app.Activity\");\n" +
            "        Activity.onCreate.overload('android.os.Bundle').implementation = function(bundle) {\n" +
            "            log(\"Activity onCreate: this=\" + this)\n" +
            "            this.onCreate(bundle);\n" +
            "        }\n" +
            "        Activity.onResume.implementation = function() {\n" +
            "            log(\"Activity onResume: this=\" + this)\n" +
            "            this.onResume();\n" +
            "        }\n" +
            "\n" +
            "        // hook Thread\n" +
            "        var Thread =  Java.use(\"java.lang.Thread\");\n" +
            "        Thread.start.implementation = function(){\n" +
            "            log(\"Thread start: this=\" + this + \", by \" + Thread.currentThread() + \", stacktrace=\" + Log.getStackTraceString(Throwable.$new()))\n" +
            "            return this.start();\n" +
            "        }\n" +
            "    })\n" +
            "}\n" +
            "\n" +
            "setImmediate(main)";
}
