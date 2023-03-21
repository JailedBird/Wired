package com.jailedbird.wired

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jailedbird.wired.lib_annotation.annotation.Wired
import com.jailedbird.wired.lib_api.launcher.WiredInjector
import com.jailedbird.wired.model.TestEnum
import com.jailedbird.wired.model.TestObj
import com.jailedbird.wired.model.TestParcelable
import com.jailedbird.wired.model.TestSerializable

class SecondActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SecondActivity1"
    }

    @Wired(required = true)
    var char: Char = 'c'

    @Wired
    var charNullable: Char? = null

    @Wired
    var int: Int = 1

    @Wired
    var intNullable: Int? = null

    @Wired
    var name: String = ""

    @Wired(required = true)
    var nameNullable: String? = ""

    @Wired
    var testEnum: TestEnum = TestEnum.OTHER

    @Wired
    var nameList: List<String> = emptyList()

    @Wired
    var age: Int = 0

    @Wired
    var testObj: Any? = null

    @Wired
    var testParcelable: TestParcelable? = null

    @Wired
    var testParcelableList: List<TestParcelable>? = null

    @Wired
    var testSerializable: TestSerializable? = null

    @Wired
    var testSerializableList: List<TestSerializable>? = null
    private val typeStr = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        typeStr.append("\n" + TestEnum::class.java.canonicalName)
        typeStr.append("\n" + TestObj::class.java.canonicalName)
        typeStr.append("\n" + TestSerializable::class.java.canonicalName)
        typeStr.append("\n" + TestParcelable::class.java.canonicalName)

        WiredInjector.inject(this)


        findViewById<View>(R.id.tv_hello).setOnClickListener {
            showContent()
        }
        Log.d(TAG, "onCreate: $name : $age")
        Log.d(TAG, "onCreate: testObj is ${testObj}")
        Log.d(TAG, "onCreate: testObj is ${testObj}")
        Log.d(TAG, "onCreate: testParcelable is ${testParcelable}")
        Log.d(TAG, "onCreate: testSerializable is ${testSerializable}")
        Log.d(TAG, "onCreate: nameList is ${nameList.toString()}")
        Log.d(TAG, "onCreate: testParcelableList is ${testParcelableList.toString()}")
        Log.d(TAG, "onCreate: testSerializableList is ${testSerializableList.toString()}")
        Log.d(TAG, "onCreate: testEnum is ${testEnum}")
    }

    private fun showContent() {
        val textView = findViewById<TextView>(R.id.tv_show)
        textView.text = ""
        val stringBuilder = StringBuilder()
        stringBuilder.append(typeStr.toString())
        stringBuilder.append("\nonCreate: $name : $age")
        stringBuilder.append("\nonCreate: testObj is ${testObj}")
        stringBuilder.append("\nonCreate: testObj is ${testObj}")
        stringBuilder.append("\nonCreate: testParcelable is ${testParcelable}, class is ${TestParcelable::class.java.canonicalName}")
        stringBuilder.append("\nonCreate: testSerializable is ${testSerializable}, class is ${TestSerializable::class.java.canonicalName}")
        stringBuilder.append("\nonCreate: nameList is ${nameList.toString()}")
        stringBuilder.append("\nonCreate: testParcelableList is ${testParcelableList.toString()}")
        stringBuilder.append("\nonCreate: testSerializableList is ${testSerializableList.toString()}")
        stringBuilder.append("\nonCreate: testEnum is ${testEnum}")
        textView.text = stringBuilder.toString()
    }


}