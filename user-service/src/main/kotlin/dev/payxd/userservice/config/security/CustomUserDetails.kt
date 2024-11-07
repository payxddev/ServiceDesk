package dev.payxd.userservice.config.security

import dev.payxd.userservice.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class CustomUserDetails(val user: User): UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        // Проходим по ролям пользователя
        user.roles.forEach { role ->
            // Добавляем каждую роль как GrantedAuthority
            authorities.add(SimpleGrantedAuthority("ROLE_${role.name}"))

            // Добавляем права, связанные с этой ролью
            role.permissions.forEach { permission ->
                authorities.add(SimpleGrantedAuthority(permission.name))
            }
        }

        return authorities
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
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