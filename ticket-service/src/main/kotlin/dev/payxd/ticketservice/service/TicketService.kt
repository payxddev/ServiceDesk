package dev.payxd.ticketservice.service

import dev.payxd.ticketservice.config.UserDetailsDTO
import dev.payxd.ticketservice.entity.Ticket
import dev.payxd.ticketservice.entity.TicketStatus
import dev.payxd.ticketservice.entity.dto.ApiResponse
import dev.payxd.ticketservice.repository.TicketRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class TicketService(
    private val ticketRepository: TicketRepository
) {

    fun createTicket(userDetails: UserDetailsDTO, title: String, description: String, watcherIds: Set<Long>): Ticket {
        val ticket = Ticket(
            creator = userDetails.id,
            creatorUsername = userDetails.username,
            title = title,
            description = description,
            watchers = watcherIds,
            status = TicketStatus.NEW
        )
        return ticketRepository.save(ticket)
    }

    fun assignTicket(ticketId: Long, responsibleUserId: Long, responsibleUsername: String): Ticket {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsDTO
        val ticket = ticketRepository.findById(ticketId).orElseThrow { NoSuchElementException("Ticket not found") }

        checkAdminOrOwner(userDetails, ticket)

        ticket.responsibleUserId = responsibleUserId
        ticket.responsibleUsername = responsibleUsername
        ticket.status = TicketStatus.IN_PROGRESS
        return ticketRepository.save(ticket)
    }

    fun updateTicketStatus(ticketId: Long, status: TicketStatus): Ticket {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsDTO
        val ticket = ticketRepository.findById(ticketId).orElseThrow { NoSuchElementException("Ticket not found") }

        checkAdminOrOwner(userDetails, ticket)

        ticket.status = status
        return ticketRepository.save(ticket)
    }

    fun getUserTickets(): Any {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsDTO
        val tickets: List<Ticket> = if (isAdmin(userDetails)) {
            ticketRepository.findAll()
        } else {
            ticketRepository.findByCreator(userDetails.id)
        }

        return if (tickets.isEmpty()) {
            ApiResponse(message = "У вас нет тикетов")
        } else {
            tickets
        }
    }


    fun getTicketById(ticketId: Long): Ticket {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsDTO
        val ticket = ticketRepository.findById(ticketId).orElseThrow { NoSuchElementException("Ticket not found") }
        checkAdminOrOwner(userDetails, ticket)
        return ticket
    }

    private fun isAdmin(userDetails: UserDetailsDTO): Boolean {
        return userDetails.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    private fun checkAdminOrOwner(userDetails: UserDetailsDTO, ticket: Ticket) {
        if (!isAdmin(userDetails) && ticket.creator != userDetails.id) {
            throw AccessDeniedException("You do not have permission to access this resource")
        }
    }
    fun findTicketById(id: Long): Ticket {
        return ticketRepository.findById(id)
            .orElseThrow { RuntimeException("Ticket not found") }
    }

    fun saveTicket(ticket: Ticket) {
        ticketRepository.save(ticket)
    }

    // Остальные методы...
}
