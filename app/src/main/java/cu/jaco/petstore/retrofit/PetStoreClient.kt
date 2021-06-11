package cu.jaco.petstore.retrofit

import cu.jaco.petstore.retrofit.utils.RetrofitClient
import io.swagger.petstore.ApiClient
import io.swagger.petstore.api.StoreApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetStoreClient @Inject constructor() : RetrofitClient<StoreApi>() {

    override fun serviceCreator(): StoreApi {
        val client = ApiClient()
        client.configureFromOkclient(providerOkHttpBuilder().build())
        return client.createService(StoreApi::class.java)
    }

    override fun headers(): HashMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Accept"] = "application/json"
        return headers
    }

}