package com.example.untitled

import android.content.Intent
import android.os.Build
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {


    @JvmField
    val gps = "sample.flutter.io/gps"

    //    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState, persistentState)
//        Log.e("25", " ->  MainActivity -> onCreate : ");
//            Handler(Looper.getMainLooper()).postDelayed({
//            Log.e("26", " ->  MainActivity -> onCreate : Working");
//                flutterEngine?.let { callFlutter(it) }
//        }, 1000)
//
//    }
    companion object {
        lateinit var engine: FlutterEngine
    }

    @Override
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        engine = flutterEngine
        MethodChannel(
            flutterEngine.dartExecutor,
            gps
        ).setMethodCallHandler { call, result ->
            if (call.method == "getGps") {
                try {
                    val intent = Intent(applicationContext, LocationService::class.java)
//                    intent.action = ls.START_COMMAND
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else startService(intent)

                    result.success(getGps())
                } catch (e: Exception) {
                    result.error("100", e.message, "" + e.printStackTrace())
                }
            } else {
                result.notImplemented()
            }
        }

        super.configureFlutterEngine(flutterEngine)

    }
//    fun callFlutter(flutterEngine: FlutterEngine) {
//        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
//            channel.invokeMethod("getNativeCall", "data1")
//    }

    private fun getGps(): String {
        return "Location Service Started"
    }
}
