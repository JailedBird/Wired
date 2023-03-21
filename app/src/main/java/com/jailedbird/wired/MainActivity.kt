package com.jailedbird.wired

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jailedbird.wired.lib_api.launcher.WiredBuilder
import com.jailedbird.wired.model.TestEnum
import com.jailedbird.wired.model.TestObj
import com.jailedbird.wired.model.TestParcelable
import com.jailedbird.wired.model.TestSerializable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_hello).setOnClickListener {
            gotoSecond()
        }
    }

    private fun gotoSecond() {
        val bundle = WiredBuilder.Builder()
            .withString("name", "abelzhao111")
            .withInt("age", 23)
            .withObject("testObj", TestObj("p1", "p2"))
            .withObject("nameList", listOf("1", "2", "3"))
            //.withParcelable("testParcelable", TestParcelable("p1", "p2"))
            .withObject(
                "testParcelableList",
                listOf(
                    TestParcelable("p1", "p2"),
                    TestParcelable("p1", "p2"),
                    TestParcelable("p1", "p2")
                )
            )
            .withSerializable("testSerializable", TestSerializable("p1", "p2"))
            .withObject(
                "testSerializableList",
                listOf(
                    TestSerializable("p1", "p2"),
                    TestSerializable("p1", "p2"),
                    TestSerializable("p1", "p2")
                )
            )
            .withObject("testEnum", TestEnum.A)
            .build()
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}