package com.bf.xposedemo

import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * @author zjh
 * 2019/6/6
 */
class XposedHook : IXposedHookLoadPackage{
    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam?) {
        if(loadPackageParam!!.packageName.equals("com.plateno.botaoota")){
            hookBotao(loadPackageParam)
        }
    }

    fun hookBotao(loadPackageParam: XC_LoadPackage.LoadPackageParam?){

        XposedHelpers.findAndHookMethod("com.stub.StubApp",loadPackageParam!!.classLoader,"attachBaseContext",Context::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                XposedBridge.log("afterHookedMethod")
                val context: Context = param!!.args[0] as Context
                val classLoader: ClassLoader = context.classLoader
                hookUserNameAndPwd(classLoader)
                hookReq(classLoader)
                hookReqParam(classLoader)
                hookRsp(classLoader)
            }
        })
    }

    fun hookUserNameAndPwd(classLoader: ClassLoader){
        XposedHelpers.findAndHookConstructor("com.bestwehotel.app.whlogin.model.LoginForm",classLoader, String::class.java,String::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                XposedBridge.log("botao username=========>"+param!!.args[0])
                XposedBridge.log("botao password=========>"+param.args[1])
            }
        })
    }

    fun hookReq(classLoader: ClassLoader){
        XposedHelpers.findAndHookMethod("okhttp3.RealCall",classLoader,"getResponseWithInterceptorChain",object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val requestField = param!!.result::class.java.getDeclaredField("request")
                requestField.isAccessible = true
                val requestObj = requestField.get(param.result)
                val reqStrMethod = requestObj::class.java.getMethod("toString")
                val reqStr = reqStrMethod.invoke(requestObj)
                XposedBridge.log("req base info =========>" + reqStr)
            }
        })
    }

    fun hookReqParam(classLoader: ClassLoader){
        XposedHelpers.findAndHookConstructor("okhttp3.FormBody",classLoader, List::class.java,List::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val names: ArrayList<*> = param!!.args[0] as ArrayList<*>
                val values: ArrayList<*> = param.args[1] as ArrayList<*>
                XposedBridge.log("req param ==========>" + param!!.result)
                for (i in names.indices){
                    XposedBridge.log(names[i] as String + "=" + values[i] as String)
                }
            }
        })
    }

    fun hookRsp(classLoader: ClassLoader){
        XposedHelpers.findAndHookMethod("okhttp3.ResponseBody",classLoader,"string",object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                XposedBridge.log("rsp info ==========>" + param!!.result)
            }
        })
    }
}