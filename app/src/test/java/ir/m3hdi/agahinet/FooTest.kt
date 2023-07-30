package ir.m3hdi.agahinet

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ir.m3hdi.agahinet.data.remote.model.chat.ErrorData
import ir.m3hdi.agahinet.data.remote.model.chat.WsResponse
import ir.m3hdi.agahinet.data.remote.model.chat.wsResponseAdapterFactory
import org.junit.Test

class FooTest {

    @Test
    fun test(){

        val moshi = Moshi.Builder().add(wsResponseAdapterFactory).addLast(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(WsResponse::class.java)

        val o = WsResponse.Error(ErrorData("hi"))
        println(adapter.toJson(o))

        val j="""
            {"response_type":"ERROR", "data": {"status":"hi"} }
        """.trimIndent()

        val obj = adapter.fromJson(j) as WsResponse.Error
        println(obj)

        assert(true)
    }
}