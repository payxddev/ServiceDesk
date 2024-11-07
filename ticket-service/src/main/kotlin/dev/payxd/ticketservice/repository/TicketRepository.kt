package dev.payxd.ticketservice.repository;

import dev.payxd.ticketservice.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long> {
    fun findByCreator(creator: Long): List<Ticket>
}