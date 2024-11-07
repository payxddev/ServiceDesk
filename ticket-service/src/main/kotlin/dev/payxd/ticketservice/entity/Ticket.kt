package dev.payxd.ticketservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "desk_tickets")
data class Ticket(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val creator: Long,
    val creatorUsername: String,
    val title: String,
    val description: String,

    @ElementCollection
    @CollectionTable(name = "ticket_watchers", joinColumns = [JoinColumn(name = "ticket_id")])
    @Column(name = "user_id")
    val watchers: Set<Long> = emptySet(),

    @Enumerated(EnumType.STRING)
    var status: TicketStatus = TicketStatus.NEW,

    var responsibleUserId: Long? = null,
    var responsibleUsername: String? = null,

    @OneToMany(mappedBy = "ticket", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val messages: List<TicketMessage> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "ticket_files", joinColumns = [JoinColumn(name = "ticket_id")])
    @Column(name = "file_url")
    val files: MutableList<String> = mutableListOf()  // Сделали список изменяемым
) {
    constructor() : this(
        id = null,
        creator = 0,
        creatorUsername = "",
        title = "",
        description = "",
        watchers = emptySet(),
        status = TicketStatus.NEW,
        responsibleUserId = null,
        responsibleUsername = null,
        messages = mutableListOf(),
        files = mutableListOf()  // Сделали список изменяемым
    )
}
