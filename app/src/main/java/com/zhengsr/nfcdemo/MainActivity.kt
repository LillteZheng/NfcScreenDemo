package com.zhengsr.nfcdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.excshare.nfclib.NfcApi
import com.excshare.nfclib.bean.NfcBean
import com.excshare.nfclib.bean.ScreenNfcBean
import com.excshare.nfclib.callback.INfcReadListener
import com.excshare.nfclib.status.NfcErrorCode
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.annotations.NotNull
import kotlin.experimental.and

class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    private lateinit var cardAdapter:CardAdapter
    private val datas = mutableListOf<CardBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NfcApi.getScreenNfc().makeActivityEnableForeground(this)

        card_recy.layoutManager = LinearLayoutManager(this)
        cardAdapter = CardAdapter(R.layout.item_layout,datas);
        card_recy.adapter = cardAdapter
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
                        datas.add(CardBean("Ssid: ",""))
                        datas.add(CardBean("password: ",""))
                        datas.add(CardBean("apIp: ",""))
                        datas.add(CardBean("port: ",""))
                        datas.add(CardBean("hideSsid: ",""))
                        datas.add(CardBean("hidePassword: ",""))
                        datas.add(CardBean("downloadUrl: ",""))
                        datas.add(CardBean("packageName: ",""))
                        cardAdapter.notifyDataSetChanged()
                    }

                    override fun onSuccess(bean: NfcBean?) {
                        bean?.apply {
                            datas.add(CardBean("Ssid: ",ssid))
                            datas.add(CardBean("password: ",password))
                            datas.add(CardBean("apIp: ",apIp))
                            datas.add(CardBean("port: ","$port"))
                            datas.add(CardBean("hideSsid: ",hideSsid))
                            datas.add(CardBean("hidePassword: ",hidePassword))
                            cardAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onScreenNfcInfo(bean: ScreenNfcBean?) {
                        super.onScreenNfcInfo(bean)
                        runOnUiThread {
                            bean?.apply {
                                card_id.text = "卡号: ${nfcId.toHexString()}"
                                card_size.text = "总容量: $maxSize kb，已写入: $payloadSize kb"

                                card_size.postDelayed({
                                    datas.add(CardBean("downloadUrl: ",url))
                                    if (!pkgLists.isNullOrEmpty()) {
                                        datas.add(CardBean("packageName: ",pkgLists[0]))
                                    }
                                    cardAdapter.notifyDataSetChanged()
                                },200)
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
data class CardBean(var title:String,var msg:String?)
class CardAdapter(val layoutId:Int,val datas:MutableList<CardBean>)
    : BaseQuickAdapter<CardBean,BaseViewHolder>(layoutId,datas){
    override fun convert(holder: BaseViewHolder, item: CardBean) {
        holder.setText(R.id.item_title,item.title);
        item.msg?.let {
            holder.setText(R.id.item_msg,it)
        }
        if (item.msg.isNullOrEmpty()){
            holder.setTextColorRes(R.id.item_title,R.color.read)
        }else{
            holder.setTextColorRes(R.id.item_title,R.color.teal_700)
        }
    }

}