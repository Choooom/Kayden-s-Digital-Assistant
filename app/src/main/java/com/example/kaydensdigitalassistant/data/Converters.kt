package com.example.kaydensdigitalassistant.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        if (byteArray == null) return null
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @TypeConverter
    fun fromReceiptItemList(receiptItems: List<ReceiptItem>?): String? {
        if (receiptItems == null) return null
        return Gson().toJson(receiptItems)
    }

    @TypeConverter
    fun toReceiptItemList(json: String?): List<ReceiptItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<ReceiptItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        if (list == null) return null
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type)
    }
}
