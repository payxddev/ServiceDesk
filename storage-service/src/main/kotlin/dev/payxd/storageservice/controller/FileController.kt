package dev.payxd.storageservice.controller

import dev.payxd.storageservice.service.MinioService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
class FileController(private val minioService: MinioService) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("files") files: List<MultipartFile>): ResponseEntity<List<String>> {
        val uploadedFiles = files.map { file ->
            val fileName = file.originalFilename ?: throw IllegalArgumentException("File name is missing")
            minioService.uploadFile(fileName, file.inputStream, file.contentType ?: "application/octet-stream")
        }
        return ResponseEntity.ok(uploadedFiles)
    }

    @GetMapping("/download/{fileName}")
    fun downloadFile(@PathVariable fileName: String): ResponseEntity<ByteArray> {
        val inputStream = minioService.downloadFile(fileName)
        val fileBytes = inputStream.readAllBytes()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(fileBytes)
    }

    @DeleteMapping("/delete/{fileName}")
    fun deleteFile(@PathVariable fileName: String): ResponseEntity<String> {
        minioService.deleteFile(fileName)
        return ResponseEntity.ok("File deleted successfully")
    }
}
