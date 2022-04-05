package com.emaintec.lib.network

import android.content.Context
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

object NetworkSSL {

    @Throws(Exception::class)
    fun setCertification(context: Context, crt_resouce_id: Int) {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val inputStream = context.resources.openRawResource(crt_resouce_id)
        val certificate: Certificate
        try {
            certificate = certificateFactory.generateCertificate(inputStream)
//            println("ca=" + (certificate as X509Certificate).subjectDN)
        } finally {
            inputStream.close()
        }

        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)

        // Create a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // Create an SSLContext that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    }

    fun trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {

            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Install the all-trusting trust manager
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())      // Google Play 에서 서비스 시 Google 측에서  X509TrustManager.checkServerTrusted 에 구현을 제대로 하지 않을 경우 제제(서비스 내림)를 가하는 것으로 판단.
            //sslContext.init(null, null, new java.security.SecureRandom())			// 정상적인 인증서인 경우 trustAllCerts 대신 null 로 처리하면 됨.
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
