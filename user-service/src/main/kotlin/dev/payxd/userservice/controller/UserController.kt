package dev.payxd.userservice.controller

import dev.payxd.userservice.entity.dto.ApiResponse
import dev.payxd.userservice.entity.dto.user.UpdateUserRequest
import dev.payxd.userservice.entity.dto.user.UserProfileDTO
import dev.payxd.userservice.service.user.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController (private val profileService: ProfileService){

    @GetMapping("{id}")
    fun getUserByID(@PathVariable id: Long): ResponseEntity<Any> = profileService.getUserProfile(id)

    @GetMapping("/profile")
    fun getCurrentUserProfile(): ResponseEntity<UserProfileDTO> = profileService.getCurrentUserProfile()

    @PatchMapping("/profile")
    fun updateUserProfile(@RequestBody updateRequest: UpdateUserRequest): ResponseEntity<ApiResponse> {
        return profileService.updateUserProfile(updateRequest)
    }

}