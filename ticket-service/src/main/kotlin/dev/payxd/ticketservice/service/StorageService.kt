package dev.payxd.ticketservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

@Service
class StorageService(
    private val restTemplate: RestTemplate,  // Или WebClient для асинхронной работы
) {
    val storageServiceUrl = "lb://storage-service"
    fun uploadFile(file: MultipartFile): String {
        val response = restTemplate.postForObject(
            "$storageServiceUrl/upload",
            file.bytes,
            String::class.java
        )
        return response ?: throw RuntimeException("Failed to upload file")
    }

    fun deleteFile(fileUrl: String) {
        restTemplate.delete("$storageServiceUrl/delete?fileUrl=$fileUrl")
    }
}
