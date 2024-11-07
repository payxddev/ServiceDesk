package dev.payxd.userservice.service.user

import dev.payxd.userservice.config.security.CustomUserDetails
import dev.payxd.userservice.entity.User
import dev.payxd.userservice.entity.dto.ApiResponse
import dev.payxd.userservice.entity.dto.user.UpdateUserRequest
import dev.payxd.userservice.entity.dto.user.UserProfileDTO
import dev.payxd.userservice.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service



@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // Получение профиля текущего пользователя
    fun getCurrentUserProfile(): ResponseEntity<UserProfileDTO> {
        val currentUser = getCurrentUser()
        val user = userRepository.findUserById(currentUser.id!!)
            ?: throw UsernameNotFoundException("User with ID ${currentUser.id} not found")

        val userProfile = UserProfileDTO(
            id = user.id!!,
            username = user.username,
            email = user.email,
            firstname = user.firstname,
            lastname = user.lastname,
            roles = user.roles,  // Теперь возвращаем список ролей
            verified = user.verified
        )

        return ResponseEntity.ok(userProfile)
    }

    // Обновление профиля текущего пользователя
    fun updateUserProfile(updateRequest: UpdateUserRequest): ResponseEntity<ApiResponse> {
        val currentUser = getCurrentUser()
        val user = userRepository.findUserById(currentUser.id!!)
            ?: throw UsernameNotFoundException("User with ID ${currentUser.id} not found")

        updateRequest.email?.let {
            user.email = it
        }

        updateRequest.password?.let {
            user.password = passwordEncoder.encode(it)
        }

        userRepository.save(user)

        return ResponseEntity.ok(ApiResponse("Профиль успешно обновлён"))
    }
    // Проверка прав доступа у роли пользователя
    private fun hasPermission(currentUser: User, permissionName: String): Boolean {
        return currentUser.roles.flatMap { it.permissions }.any { it.name == permissionName }
    }
    // Получение профиля другого пользователя с проверкой прав
    fun getUserProfile(userId: Long): ResponseEntity<Any> {
        val currentUser = getCurrentUser()
        val user = userRepository.findUserById(userId)
            ?: throw UsernameNotFoundException("User with ID $userId not found")

        // Проверка прав доступа: администратор может просматривать профили других пользователей
       //val isAdmin = currentUser.roles.any { it.name == "ADMIN" || it.name == "OPERATOR" } // Проверяем наличие роли ADMIN
       //if (!isAdmin && currentUser.id != userId) {
       //    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse("У вас нет прав для просмотра этого профиля"))
       //}
        if (!hasPermission(currentUser, "VIEW_USERS_PROFILE") && currentUser.id != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse("У вас нет прав для просмотра чужого профиля"))
        }
        val response = User(
            user.id,
            user.username,
            user.firstname,
            user.lastname,
            user.email,
            user.password,
            user.roles,  // Отправляем список ролей
            user.verified
        )

        return ResponseEntity.ok(response)
    }

    // Вспомогательный метод для получения текущего пользователя
    private fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal as CustomUserDetails
        return principal.user
    }
}