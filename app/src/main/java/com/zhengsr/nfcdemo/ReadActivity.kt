package com.zhengsr.nfcdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.excshare.nfclib.NfcApi
import com.excshare.nfclib.bean.NfcBean
import com.excshare.nfclib.bean.ScreenNfcBean
import com.excshare.nfclib.callback.INfcReadListener
import com.excshare.nfclib.status.NfcErrorCode
import kotlinx.android.synthetic.main.activity_read_actiivty.*

private const val TAG = "ReadActivity"
class ReadActivity : AppCompatActivity() {
    private val datas = mutableListOf<CardBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_actiivty)
        NfcApi.getScreenNfc().makeActivityEnableForeground(this)
        initRecycleView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        readNfc(intent)
        //writeNfc(intent)
    }
    private fun readNfc(intent: Intent?){
        datas.clear()
        intent?.let {
            if (NfcApi.getScreenNfc().isIntentSupportNfc(it)) {
                NfcApi.getScreenNfc().readNfc(it, object : INfcReadListener {
                    override fun onFail(code: NfcErrorCode?, errorMsg: String?) {
                        runOnUiThread {
                            datas.clear()
                            Log.d(TAG, "zsr onFail: $code,$errorMsg")
                            datas.add(CardBean("Ssid: ", ""))
                            datas.add(CardBean("password: ", ""))
                            datas.add(CardBean("apIp: ", ""))
                            datas.add(CardBean("port: ", ""))
                            datas.add(CardBean("hideSsid: ", ""))
                            datas.add(CardBean("hidePassword: ", ""))
                            datas.add(CardBean("downloadUrl: ", ""))
                            datas.add(CardBean("packageName: ", ""))
                            card_recy.adapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onSuccess(bean: NfcBean?) {
                        Log.d(TAG, "zsr onSuccess: $bean , ${bean?.password}")
                        runOnUiThread {
                            bean?.apply {
                                datas.add(CardBean("Ssid: ", ssid))
                                datas.add(CardBean("password: ", password))
                                datas.add(CardBean("apIp: ", apIp))
                                datas.add(CardBean("port: ", "$port"))
                                datas.add(CardBean("hideSsid: ", hideSsid))
                                datas.add(CardBean("hidePassword: ", hidePassword))
                                //  card_recy.adapter?.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onScreenNfcInfo(bean: ScreenNfcBean?) {
                        super.onScreenNfcInfo(bean)
                        runOnUiThread {
                            bean?.apply {
                                card_id.text = "卡号: ${nfcId.toHexString()}"
                                card_size.text = "总容量: $maxSize kb，已写入: $payloadSize kb"

                                datas.add(CardBean("downloadUrl: ",url))
                                if (!pkgLists.isNullOrEmpty()) {
                                    datas.add(CardBean("packageName: ",pkgLists[0]))
                                }
                                card_recy.adapter?.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onLog(log: String?) {
                        super.onLog(log)
                        Log.d(TAG, "zsr onLog: $log")
                    }

                })
            }
        }
    }

    private fun  initRecycleView(){
        card_recy.linear().setup {
            addType<CardBean>(R.layout.item_layout)
            onBind {
                val titleTv = findView<TextView>(R.id.item_title)
                val msgTv = findView<TextView>(R.id.item_msg)
                val title = getModel<CardBean>().title
                val msg = getModel<CardBean>().msg
                msg?.let {
                    msgTv.text = it
                }
                titleTv.text = title
                if (msg.isNullOrEmpty() || msg == "0"){
                    titleTv.setTextColor(this@ReadActivity.resources.getColor(R.color.read))
                }else{
                    titleTv.setTextColor(this@ReadActivity.resources.getColor(R.color.teal_700))
                }

            }
        }.models = datas
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
data class CardBean(var title:String,var msg:String?)
