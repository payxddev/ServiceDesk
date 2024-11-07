package dev.payxd.ticketservice.config


import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsDTO(
    val id: Long,
    private var username: String,
    val roles: Set<String>,
    var permissions: Set<String>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        // Преобразуем roles и permissions в GrantedAuthority
        return roles.map { role -> SimpleGrantedAuthority("ROLE_$role") } +
                permissions.map { permission -> SimpleGrantedAuthority(permission) }
    }

    override fun getPassword(): String? {
        return null // Пароль нам не нужен
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}