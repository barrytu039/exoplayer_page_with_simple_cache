package com.barry.kotlin_code_base.network

class HeaderFactory {
    fun createHeader(contentType: ContentType) : Map<String, String> {
        var headerValue = HashMap<String, String>()
        if (contentType != ContentType.NONE) {
            headerValue.put("Content-Type", convertContentTypeString(contentType))
        }
        return headerValue
    }

    fun convertContentTypeString(contentType: ContentType) : String {
        return when(contentType) {
            ContentType.Json -> return "application/json; charset=utf-8"
            ContentType.UrlEncoded -> return "application/x-www-form-urlencoded"
            ContentType.MultipartFormData -> return "multipart/form-data;"
            else -> return ""
        }
    }
}