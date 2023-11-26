package com.github.mijamarko.apiforge.model

import com.intellij.util.net.HTTPMethod

class Controller(path: String) {

    val endpoints = HashMap<String, Endpoint>()

    class Endpoint {
        lateinit var path: String
        lateinit var requestMethod: HTTPMethod

        val params = ArrayList<HttpRequestParam>()

        class HttpRequestParam(val name: String, val type: HttpRequestParamType) {
            val paramDef = HashMap<String, HttpRequestParamSchema>()
        }
        class HttpRequestParamSchema(type: Any, value: Any) {}
    }

    enum class HttpRequestParamType(name: String) {
        REQUEST_BODY("RequestBody"),
        PATH_VARIABLE("PathVariable"),
        REQUEST_PARAMS("RequestParams")
    }
}