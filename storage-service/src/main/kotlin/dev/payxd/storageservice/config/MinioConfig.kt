package dev.payxd.storageservice.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MinioConfig (
    @Value("\${minio.url}")  val minioUrl: String,
    @Value("\${minio.accessKey}")  val accessKey: String,
    @Value("\${minio.secretKey}")  val secretKey: String,
    @Value("\${minio.bucketName}")  val bucketName: String

){
    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioUrl)
            .credentials(accessKey, secretKey)
            .build()
    }
}