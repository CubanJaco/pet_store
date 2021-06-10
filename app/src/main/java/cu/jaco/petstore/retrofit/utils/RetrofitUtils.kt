package cu.jaco.petstore.retrofit.utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend inline fun <T> Call<T>.suspended(): T? = suspendCoroutine { cont ->
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            cont.resume(response.body())
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            cont.resume(null)
        }
    })
}