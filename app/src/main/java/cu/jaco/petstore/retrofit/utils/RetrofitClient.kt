package cu.jaco.petstore.retrofit.utils

import android.os.Build
import android.util.Log
import cu.jaco.petstore.BuildConfig
import cu.jaco.petstore.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

abstract class RetrofitClient<T>() {

    private var service: T? = null

    /**
     * Este metodo se usará para invalidar el cliente cuando sea @Singleton en hilt
     */
    fun invalidate() {
        service = serviceCreator()
    }

    protected fun providerOkHttpBuilder(): OkHttpClient.Builder {
        val client = OkHttpClient().newBuilder()
            .connectTimeout(15000L, TimeUnit.MILLISECONDS)
            .readTimeout(15000L, TimeUnit.MILLISECONDS)
            .writeTimeout(15000L, TimeUnit.MILLISECONDS)
            .configSsl(providerCertificate())

        client.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val headers = headers()

            for (header in headers) {
                requestBuilder.addHeader(header.key, header.value)
            }
            chain.proceed(requestBuilder.build())
        }

        if (BuildConfig.DEBUG)
            client.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

        return client
    }

    private fun providerCertificate(): Certificate {

        val cf = CertificateFactory.getInstance("X.509")

        val classLoader = this@RetrofitClient::class.java.classLoader

        //hacer una referencia al raw del certificado para que proguard vea que se esta usando el recurso y no lo elimine
        val certId = R.raw.certificate
        Log.d("Proguard", "$certId")

//        TODO: Crear un objeto File para cargar el certificado desde un archivo almacenado si fuera necesario
        val certificateFile: File? = null

        val cert = if (certificateFile?.exists() == true)
            FileInputStream(certificateFile)
        else
            classLoader?.getResourceAsStream("res/raw/certificate.crt")

        return cert.use {
            cf.generateCertificate(cert)
        }
    }

    @Throws(
        CertificateException::class,
        IOException::class,
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class,
        IllegalStateException::class
    )
    private fun OkHttpClient.Builder.configSsl(ca: Certificate?): OkHttpClient.Builder {

        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

        if (tmf.trustManagers.size != 1 || tmf.trustManagers[0] == null
            || tmf.trustManagers[0] !is X509TrustManager
        ) {
            throw IllegalStateException(
                "Unexpected default trust managers: ${
                    Arrays.toString(
                        tmf.trustManagers
                    )
                }"
            )
        }
        val trustManager = tmf.trustManagers[0] as X509TrustManager

        if (Build.VERSION.SDK_INT < 22)
            sslSocketFactory(
                Tls12SocketFactory(
                    sslContext!!.socketFactory
                ), trustManager
            )
        else
            sslSocketFactory(sslContext!!.socketFactory, trustManager)

        hostnameVerifier(HostnameVerifier { hostname, session -> true })

        return this
    }

    fun providerServices(): T {
        if (service == null)
            service = serviceCreator()
        return service!!
    }

    protected abstract fun headers(): HashMap<String, String>

    protected abstract fun serviceCreator(): T

}