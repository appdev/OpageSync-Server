package ci.run.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Respon<T>(var code: Int, var msg: String?, var data: T?)


fun <T> success(data: T): Respon<T> {
    return Respon(200, "", data)
}

fun success(): Respon<String> {
    return Respon(200, "", null)
}

fun errorWithCode(code: Int): Respon<String> {
    return Respon(code, "", null)
}

fun errorWithMsg(msg: String?): Respon<String> {
    return Respon(0, msg, null)
}

fun error(code: Int, msg: String?): Respon<String> {
    return Respon(code, msg, null)
}