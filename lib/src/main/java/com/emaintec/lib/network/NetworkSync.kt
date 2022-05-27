package com.emaintec.lib.network

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.webkit.CookieManager
import androidx.annotation.WorkerThread
import com.emaintec.lib.base.Emaintec
import java.io.IOException
import java.io.InputStream
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class NetworkSync<T : TransmitterBase, R : ReceiverBase<*>> (val transmitter: T, val receiver: R) {

    @WorkerThread
    suspend fun get(onSuccessed: suspend ((NetworkSync<T, R>) -> Unit) = {}, onFailed: suspend ((NetworkSync<T, R>) -> Unit) = {}) : Boolean {
        var httpURLConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            if (transmitter.url.startsWith("https://")) {
                val httpsURLConnection = URL(transmitter.url).openConnection() as HttpsURLConnection
                NetworkSSL.trustAllHosts()
                httpsURLConnection.setHostnameVerifier { hostname, session -> true }
                httpURLConnection = httpsURLConnection
            } else {
                httpURLConnection = URL(transmitter.url).openConnection() as HttpURLConnection
            }
//톰켓 인증
//            var userCredentials = "tomcat:tomcat";
//            var basicAuth :String ?= null;
//            basicAuth = "Basic " + android.util.Base64.encodeToString(userCredentials.getBytes(), android.util.Base64.DEFAULT);
//            httpURLConnection.setRequestProperty ("Authorization", basicAuth);

            setCookieHeader(httpURLConnection)
            receiver.errorData = transmitter.process(httpURLConnection)
            if (receiver.errorData != null) {
                onFailed(this)
                return false
            }

            inputStream = httpURLConnection.inputStream
            receiver.errorData = receiver.process( inputStream!!, httpURLConnection.contentLength)

            getCookieHeader(httpURLConnection);
            if (receiver.errorData == null) {
                onSuccessed(this)
                return true
            } else {
                onFailed(this)
                return false
            }
        } catch (e: Exception) {
            receiver.errorData = "{\"resultSts\":\"F\", \"resultMsg\":\"" + e.localizedMessage + "\", \"success\":false, \"message\":\"" + e.localizedMessage.replace("\"","")+ "\"}"

            onFailed(this)
            return false
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            httpURLConnection?.disconnect()
        }
    }
    private fun getCookieHeader(con: HttpURLConnection) { //Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        val cookies = con.headerFields["Set-Cookie"]

        //cookies -> [JSESSIONID=D3F829CE262BC65853F851F6549C7F3E; Path=/smartudy; HttpOnly] -> []가 쿠키1개임.
        //Path -> 쿠키가 유효한 경로 ,/smartudy의 하위 경로에 위의 쿠키를 사용 가능.
        if(cookies != null) {
            CookieManager.getInstance().removeSessionCookie();
            for(cookie in cookies) {
                val sessionid = cookie.split(";\\s*").toTypedArray()[0]
                //JSESSIONID=FB42C80FC3428ABBEF185C24DBBF6C40를 얻음.
                //세션아이디가 포함된 쿠키를 얻었음.
                setSessionIdInSharedPref(sessionid)
                val cookieName: String = HttpCookie.parse(cookie).get(0).getName()
                val cookieValue: String = HttpCookie.parse(cookie).get(0).getValue()
                val cookieString = "$cookieName=$cookieValue"
                CookieManager.getInstance().setCookie("http://10.0.2.2", cookieString)
            }
        }

    }

    private fun setCookieHeader(con: HttpURLConnection) {
        val pref: SharedPreferences = Emaintec.application!!.getSharedPreferences("sessionCookie", MODE_PRIVATE)
        val sessionid = pref.getString("sessionid", null)
        if(sessionid != null) {
            Log.d("LOG", "세션 아이디" + sessionid + "가 요청 헤더에 포함 되었습니다.")
            con.setRequestProperty("Cookie", sessionid)
        }
    }

    private fun setSessionIdInSharedPref(sessionid: String) {
        val pref: SharedPreferences = Emaintec.application!!.getSharedPreferences("sessionCookie", MODE_PRIVATE)
        val edit = pref.edit()
        if(pref.getString("sessionid", null) == null) { //처음 로그인하여 세션아이디를 받은 경우
            Log.d("LOG", "처음 로그인하여 세션 아이디를 pref에 넣었습니다.$sessionid")
        } else if(pref.getString("sessionid", null) != sessionid) { //서버의 세션 아이디 만료 후 갱신된 아이디가 수신된경우
            Log.d("LOG", "기존의 세션 아이디" + pref.getString("sessionid", null) + "가 만료 되어서 " + "서버의 세션 아이디 " + sessionid + " 로 교체 되었습니다.")
        }
        edit.putString("sessionid", sessionid)
        edit.apply() //비동기 처리
    }
}
