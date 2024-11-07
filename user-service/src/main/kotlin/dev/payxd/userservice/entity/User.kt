package dev.payxd.userservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "desk_users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? ,

    @Column(name = "username", unique = true)
    var username: String,

    @Column(name = "first_name")
    var firstname: String,

    @Column(name = "last_name")
    var lastname: String,

    @Column(name = "email", unique = true)
    var email: String,

    @Column(name = "password")
    var password: String,

    @ManyToMany(fetch = FetchType.EAGER) // Связь с ролями
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role> = setOf(), // Множество ролей

    @Column(name = "verified")
    var verified: Boolean = false
) {
    constructor() : this(
        id = null,
        username = "",
        firstname = "",
        lastname = "",
        email = "",
        password = "",
        roles = setOf(),
        verified = false
    )

}
