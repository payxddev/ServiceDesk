package dev.payxd.storageservice.service

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class MinioService(private val minioClient: MinioClient, @Value("\${minio.bucketName}") private val bucketName: String) {

    fun uploadFile(fileName: String, file: InputStream, contentType: String): String {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .stream(file, file.available().toLong(), -1)
                .contentType(contentType)
                .build()
        )
        return fileName
    }

    fun downloadFile(fileName: String): InputStream {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .build()
        )
    }

    fun deleteFile(fileName: String) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .build()
        )
    }
}
