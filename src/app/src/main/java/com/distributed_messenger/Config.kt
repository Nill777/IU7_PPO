package com.distributed_messenger

import android.content.Context
import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParser

object Config {
    lateinit var logDir: String
    lateinit var databaseType: String
    lateinit var dbName: String
    lateinit var mongoUri: String
    var dbVersion: Int = 1

    fun initialize(context: Context) {
        context.resources.getXml(R.xml.app_config).use { parser ->
            while (parser.eventType != XmlResourceParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "logDir" -> logDir = parser.getAttributeValue(null, "dir")
                        "dbType" -> databaseType = parser.getAttributeValue(null, "type")
                        "db" -> {
                            dbName = parser.getAttributeValue(null, "name")
                            dbVersion = parser.getAttributeValue(null, "version")?.toIntOrNull() ?: dbVersion
                        }
                        "mongoUri" -> mongoUri = parser.getAttributeValue(null, "uri")
                    }
                }
                parser.next()
            }
        }
    }
}