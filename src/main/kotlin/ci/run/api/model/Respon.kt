package ci.run.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class Respon(var code: Int, var mes: String?, var data: String?)

inline fun <reified T> success(data: T): Respon {
    return Respon(200, "", Json.encodeToString(data))
}

fun errorWithCode(code: Int): Respon {
    return Respon(code, "", null)
}

fun errorWithMsg(msg: String?): Respon {
    return Respon(0, msg, null)
}

fun error(code: Int, msg: String?): Respon {
    return Respon(code, msg, null)
}