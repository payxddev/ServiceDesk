package dev.payxd.ticketservice.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "desk_ticket_messages")
data class TicketMessage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonIgnore
    var ticket: Ticket,

    var senderId: Long,
    var senderUsername: String,

    @Column(columnDefinition = "TEXT")
    var content: String,

    var timestamp: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        id = null,
        ticket = Ticket(),
        senderId = 0,
        senderUsername = "",
        content = "",
        timestamp = LocalDateTime.now() // Так как у нас нет поля для даты создания, проставляем текущую дату по умолчанию
    )
}