package dev.payxd.ticketservice.controller

import dev.payxd.ticketservice.entity.Ticket
import dev.payxd.ticketservice.service.StorageService
import dev.payxd.ticketservice.service.TicketService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/tickets")
class TicketController(
    private val ticketService: TicketService,
    private val storageService: StorageService
) {

    @PostMapping("/{ticketId}/files")
    fun uploadFiles(@PathVariable ticketId: Long, @RequestParam files: List<MultipartFile>): ResponseEntity<Ticket> {
        val ticket = ticketService.findTicketById(ticketId)

        val fileUrls = files.map { file ->
            storageService.uploadFile(file)
        }

        ticket.files.addAll(fileUrls)
        ticketService.saveTicket(ticket)

        return ResponseEntity.ok(ticket)
    }

    // Другие методы...
}
