package com.jailedbird.wired.lib_api.launcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.jailedbird.wired.lib_api.impl.SerializationServiceImpl
import java.io.Serializable

object WiredBuilder {

    class Builder {
        private var mBundle = Bundle()

        /**
         * Inserts a Boolean value into the mapping of this Bundle, replacing
         * any existing value for the given key.  Either key or value may be null.
         *
         * @param key   a String, or null
         * @param value a boolean
         * @return current
         */
        fun withBoolean(key: String?, value: Boolean): Builder {
            mBundle.putBoolean(key, value)
            return this
        }

        /**
         * Inserts a byte value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value a byte
         * @return current
         */
        fun withByte(key: String?, value: Byte): Builder {
            mBundle.putByte(key, value)
            return this
        }

        /**
         * Inserts a char value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value a char
         * @return current
         */
        fun withChar(key: String?, value: Char): Builder {
            mBundle.putChar(key, value)
            return this
        }

        /**
         * Inserts a short value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value a short
         * @return current
         */
        fun withShort(key: String?, value: Short): Builder {
            mBundle.putShort(key, value)
            return this
        }

        /**
         * Inserts an int value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value an int
         * @return current
         */
        fun withInt(key: String?, value: Int): Builder {
            mBundle.putInt(key, value)
            return this
        }

        /**
         * Inserts a long value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value a long
         * @return current
         */
        fun withLong(key: String?, value: Long): Builder {
            mBundle.putLong(key, value)
            return this
        }

        /**
         * Inserts a float value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value a float
         * @return current
         */
        fun withFloat(key: String?, value: Float): Builder {
            mBundle.putFloat(key, value)
            return this
        }

        /**
         * Inserts a double value into the mapping of this Bundle, replacing
         * any existing value for the given key.
         *
         * @param key   a String, or null
         * @param value a double
         * @return current
         */
        fun withDouble(key: String?, value: Double): Builder {
            mBundle.putDouble(key, value)
            return this
        }

        /**
         * Inserts a String value into the mapping of this Bundle, replacing
         * any existing value for the given key.  Either key or value may be null.
         *
         * @param key   a String, or null
         * @param value a String, or null
         * @return current
         */
        fun withString(key: String?, value: String?): Builder {
            mBundle.putString(key, value)
            return this
        }

        /**
         * Inserts a Parcelable value into the mapping of this Bundle, replacing
         * any existing value for the given key.  Either key or value may be null.
         *
         * @param key   a String, or null
         * @param value a Parcelable object, or null
         * @return current
         */
        fun withParcelable(key: String?, value: Parcelable?): Builder {
            mBundle.putParcelable(key, value)
            return this
        }

        /**
         * Inserts a Serializable value into the mapping of this Bundle, replacing
         * any existing value for the given key.  Either key or value may be null.
         *
         * @param key   a String, or null
         * @param value a Serializable object, or null
         * @return current
         */
        fun withSerializable(key: String?, value: Serializable?): Builder {
            mBundle.putSerializable(key, value)
            return this
        }

        /**
         * Set object value, the value will be convert to string by 'Fastjson'
         *
         * @param key   a String, or null
         * @param value a Object, or null
         * @return current
         */
        fun withObject(key: String?, value: Any?): Builder {
            mBundle.putString(key, SerializationServiceImpl.object2Json(value));
            return this
        }

        fun build(): Bundle {
            return mBundle
        }

        fun navigate(context: Context, clz: Class<*>) {
            val intent = Intent(context, clz).apply {
                putExtras(mBundle)
            }
            context.startActivity(intent)
        }
    }
}