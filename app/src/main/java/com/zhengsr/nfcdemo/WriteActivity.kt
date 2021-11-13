package com.zhengsr.nfcdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.excshare.nfclib.NfcApi
import com.excshare.nfclib.bean.NfcBean
import com.excshare.nfclib.callback.INfcWriteListener
import com.excshare.nfclib.status.NfcErrorCode
import kotlinx.android.synthetic.main.activity_write.*

private const val TAG = "WriteActivity"
class WriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        NfcApi.getScreenNfc().makeActivityEnableForeground(this)
        NfcApi.getScreenNfc().config("https://excshare.com/nfc/",packageName, "excshare");
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        writeNfc(intent)
    }
    private fun writeNfc(intent: Intent?) {
        intent?.let {
            if (NfcApi.getScreenNfc().isIntentSupportNfc(it)) {
                var bean = NfcBean()
                bean.ssid = "NFC-test-ssid"
                bean.password = "12345678"
                bean.port = 7382
                bean.apIp = "192.168.154.1"
                NfcApi.getScreenNfc().writeNfc(intent, bean, object : INfcWriteListener {
                    override fun onFail(p0: NfcErrorCode?, p1: String?) {
                        Log.d(TAG, "zsr onFail: ")
                        runOnUiThread {
                           card_text.text = "读取失败啦\n错误码: $p0\n错误信息：$p1"
                        }
                    }

                    override fun onStart() {
                        super.onStart()
                        runOnUiThread {
                            card_text.text = "正在写入，请不要移动手机"
                        }
                    }

                    override fun onSuccess() {
                        Log.d(TAG, "zsr onSuccess: ")
                        runOnUiThread {
                            card_text.text = "写入成功，可以读取数据了!"
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