package com.zhengsr.nfcdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.excshare.nfclib.NfcApi
import com.excshare.nfclib.bean.NfcBean
import com.excshare.nfclib.bean.ScreenNfcBean
import com.excshare.nfclib.callback.INfcReadListener
import com.excshare.nfclib.callback.INfcWriteListener
import com.excshare.nfclib.status.NfcErrorCode
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.annotations.NotNull
import kotlin.experimental.and

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun write(view: android.view.View) {
        startActivity(Intent(this,WriteActivity::class.java))

    }
    fun read(view: android.view.View) {
        startActivity(Intent(this,ReadActivity::class.java))
    }


}
