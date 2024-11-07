package dev.payxd.userservice.config.security

import dev.payxd.userservice.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = username?.let { userRepository.findUserByUsername(it) }
            ?: throw UsernameNotFoundException("User with username $username not found")

        return CustomUserDetails(user)
    }
}