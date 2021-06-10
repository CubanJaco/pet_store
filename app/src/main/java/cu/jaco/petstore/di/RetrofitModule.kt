package cu.jaco.petstore.di

import cu.jaco.petstore.retrofit.PetStoreClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.swagger.petstore.api.StoreApi

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    fun providerDefaultApi(apiRetrofitClient: PetStoreClient): StoreApi {
        return apiRetrofitClient.providerServices()
    }

}