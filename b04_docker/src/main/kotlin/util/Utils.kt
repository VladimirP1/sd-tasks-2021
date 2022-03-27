package util

import reactor.netty.http.server.HttpServerRequest

fun HttpServerRequest.par(name: String): String = params()!![name]!!
