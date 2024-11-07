package dev.payxd.userservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "desk_permissions")
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @Column(name = "name", unique = true)
    var name: String
) {
    constructor() : this(
        id = null,
        name = ""
    )
}
