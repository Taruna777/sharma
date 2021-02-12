package com.omronhealthcare.foresight.database

import android.util.Base64
import com.google.gson.Gson
import com.omronhealthcare.foresight.global.datasource.network.signin.responsemodels.JWTModel
import java.io.UnsupportedEncodingException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Helper class to contain the functions used for Database encryption keys
 */
object DatabaseEncryptionHelper {
    val logger: Logger = Logger.getLogger(this::class.java.simpleName)
    
    /**
     * Parse jwt token and get the encryption key provider and salt- userName
     * @param idToken token id received from login api response
     * @return will return the array of key provider
     */
    fun getEncryptKeyProvider(idToken: String): Array<String?>? {
        val keyProvider = arrayOfNulls<String>(2)
        val jwtModel: JWTModel? = getJwtModel(idToken)
        if (jwtModel != null) {
            keyProvider[0] = jwtModel.username
            keyProvider[0] = keyProvider[0]!!.replace("-", "")
            keyProvider[1] = jwtModel.clientId
        }
        return keyProvider
    }

    /**
     * Method to get the JWT model from the token id
     * @param idToken token id received from login api response
     */
    private fun getJwtModel(idToken: String): JWTModel? {
        var jwtString: String
        var jwtModel: JWTModel? = null
        try {
            jwtString = decoded(idToken)!!
            jwtModel = Gson().fromJson(jwtString, JWTModel::class.java)
        } catch (e: Exception) {
            logger.log(Level.INFO, "Exception", e)
        }
        return jwtModel
    }

    /**
     * Method to decode the encoded string into a JWT model
     * @param JWTEncoded encoded JWT model
     */
    @Throws(java.lang.Exception::class)
    private fun decoded(JWTEncoded: String): String? {
        val split = JWTEncoded.split(".")
        try {
            return getJson(split[1])
        } catch (e: UnsupportedEncodingException) {
            logger.log(Level.INFO, "UnsupportedEncodingException", e)
        } catch (e: IndexOutOfBoundsException) {
            logger.log(Level.INFO, "IndexOutOfBoundsException", e)
        }
        return ""
    }

    /**
     * Method to decode the encoded string using UTF-8
     * @param strEncoded Encoded string
     */
    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String? {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes) //Decoding the bytes as UTF-8
    }
}