package dev.payxd.userservice.repository;

import dev.payxd.userservice.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User
    fun existsByEmail(email: String?): Boolean
    fun existsByUsername(username: String?): Boolean
    fun findUserById(id: Long): User?
}