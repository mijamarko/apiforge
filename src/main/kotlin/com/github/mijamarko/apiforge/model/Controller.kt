package com.github.mijamarko.apiforge.model

import com.intellij.util.net.HTTPMethod

class Controller(path: String) {

    val endpoints = HashMap<String, Endpoint>()

    class Endpoint(path: String, requestType: HTTPMethod, transferMethod: TransferMethod) {

        val params = HashMap<String, TransferMethodParam>()

        class TransferMethodParam(type: Any, value: Any) {}
    }

    enum class TransferMethod(name: String) {
        REQUEST_BODY("RequestBody"),
        PATH_VARIABLE("PathVariable"),
        REQUEST_PARAMS("RequestParams")
    }
}