package com.ke.dawaati.util

import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by @Kelvin Kioko on 02/04/19.
 */
class EncryptUtil(secretKey: String) {

    private val paramSpec: AlgorithmParameterSpec

    fun encryptFile(inFile: File?, outFile: File?, secretKey: String) {
        val key = generateCustomKey(secretKey)
        try {
            encrypt(key, paramSpec, FileInputStream(inFile), FileOutputStream(outFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun decryptFile(inFile: File?, outFile: File?, secretKey: String) {
        val key = generateCustomKey(secretKey)
        try {
            decrypt(key, paramSpec, FileInputStream(inFile), FileOutputStream(outFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun md5(s: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(
                    0xFF and messageDigest[i]
                        .toInt()
                )
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun generateCustomKey(secretKey: String): SecretKey {
        val keyData = md5(secretKey).toByteArray()
        val key: SecretKey = SecretKeySpec(keyData, 0, keyData.size, ALGO_SECRET_KEY_GENERATOR)
        val decodedKey = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        val finalKey = decodedKey.substring(decodedKey.length - 25)
        val encodedKey = Base64.decode(finalKey, Base64.DEFAULT)
        return SecretKeySpec(encodedKey, 0, encodedKey.size, "AES")
    }

    companion object {
        private const val IV_LENGTH = 16 // Default length with Default 128
        private const val ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG"
        private const val ALGO_SECRET_KEY_GENERATOR = "AES"
        private const val DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024
        private const val ALGO_VIDEO_ENCRYPTOR = "AES/CBC/PKCS5Padding"
        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            InvalidAlgorithmParameterException::class,
            IOException::class
        )
        fun encrypt(
            key: SecretKey?,
            paramSpec: AlgorithmParameterSpec?,
            `in`: InputStream,
            out: OutputStream
        ) {
            var out = out
            try {
                val c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR)
                c.init(Cipher.ENCRYPT_MODE, key, paramSpec)
                out = CipherOutputStream(out, c)
                var count: Int
                val buffer = ByteArray(DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE)
                while (`in`.read(buffer).also { count = it } >= 0) {
                    out.write(buffer, 0, count)
                }
            } finally {
                out.close()
            }
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            InvalidAlgorithmParameterException::class,
            IOException::class
        )
        fun decrypt(
            key: SecretKey?,
            paramSpec: AlgorithmParameterSpec?,
            `in`: InputStream,
            out: OutputStream
        ) {
            var out = out
            try {
                val c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR)
                c.init(Cipher.DECRYPT_MODE, key, paramSpec)
                out = CipherOutputStream(out, c)
                var count: Int
                val buffer = ByteArray(DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE)
                while (`in`.read(buffer).also { count = it } >= 0) {
                    out.write(buffer, 0, count)
                }
            } finally {
                out.close()
            }
        }
    }

    init {
        val keyData = md5(secretKey).toByteArray()
        val key: SecretKey = SecretKeySpec(keyData, 0, keyData.size, ALGO_SECRET_KEY_GENERATOR)
        val decodedKey = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        val finalKey = decodedKey.substring(decodedKey.length - 25)
        val iv = Base64.decode(finalKey, Base64.DEFAULT)
        paramSpec = IvParameterSpec(iv)
    }
}
