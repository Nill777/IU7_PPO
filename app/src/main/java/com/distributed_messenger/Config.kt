package com.distributed_messenger

import android.content.Context
import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParser

object Config {
    lateinit var logFileName: String
    lateinit var dbName: String
    var dbVersion: Int = 1

    fun initialize(context: Context) {
        context.resources.getXml(R.xml.app_config).use { parser ->
            while (parser.eventType != XmlResourceParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "logging" -> logFileName = parser.getAttributeValue(null, "file")
                        "database" -> {
                            dbName = parser.getAttributeValue(null, "name")
                            dbVersion = parser.getAttributeValue(null, "version")?.toIntOrNull() ?: 1
                        }
                    }
                }
                parser.next()
            }
        }
    }
}