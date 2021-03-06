package pers.icebounded.net.ssl

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * Created by Andy
 * on 2021/3/1
 *
 * 为了解决客户端不信任服务器数字证书的问题，网络上大部分的解决方案都是让客户端不对证书做任何检查，
 * 这是一种有很大安全漏洞的办法
 */
class UnSafeTrustManager: X509TrustManager {

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }

}