package dev.payxd.userservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "desk_roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "name", unique = true)
    var name: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    var permissions: Set<Permission> = setOf()
) {
    constructor() : this(
        id = null,
        name = "",
        permissions = setOf()
    )
}