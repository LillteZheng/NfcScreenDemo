package com.zhengsr.nfcdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.excshare.nfclib.NfcApi
import com.excshare.nfclib.bean.NfcBean
import com.excshare.nfclib.bean.ScreenNfcBean
import com.excshare.nfclib.callback.INfcReadListener
import com.excshare.nfclib.status.NfcErrorCode
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.experimental.and

class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NfcApi.getScreenNfc().makeActivityEnableForeground(this)
        //写才需要
        //NfcApi.getScreenNfc().config("https://excshare.com/nfc", packageName,null)
        readNfc(intent)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        readNfc(intent)
    }
    private fun readNfc(intent: Intent?){

        intent?.let {
            if (NfcApi.getScreenNfc().isIntentSupportNfc(it)) {
                NfcApi.getScreenNfc().readNfc(it, object : INfcReadListener {
                    override fun onFail(code: NfcErrorCode?, errorMsg: String?) {
                        Log.d(TAG, "zsr onFail: $code,$errorMsg")
                    }

                    override fun onSuccess(bean: NfcBean?) {
                        Log.d(TAG, "zsr onSuccess: $bean")
                        runOnUiThread {
                            card_json.text = "传屏数据: ${bean?.toString()}"
                        }
                    }

                    override fun onScreenNfcInfo(bean: ScreenNfcBean?) {
                        super.onScreenNfcInfo(bean)
                        runOnUiThread {
                            bean?.apply {
                                card_id.text = "卡号: ${nfcId.toHexString()}"
                                card_size.text = "总容量: $maxSize kb,已写入: $payloadSize kb"
                                card_url.text = "url: $url"
                                card_pkg.text = "包名: ${pkgLists[0]}"
                                Log.d(TAG, "zsr onScreenNfcInfo: ${pkgLists.size}")
                                pkgLists.forEachIndexed { index, s ->
                                    if (index != 0){
                                        card_pkgs.append(s)
                                        card_pkgs.append("\t")
                                    }else{
                                        card_pkgs.text = "浏览器包名："
                                    }
                                }
                            }
                        }
                    }

                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        NfcApi.getScreenNfc().enableForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        NfcApi.getScreenNfc().disableForegroundDispatch(this)
    }
}
fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }