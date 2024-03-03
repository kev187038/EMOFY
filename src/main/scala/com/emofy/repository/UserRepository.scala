package com.emofy.repository

import com.emofy.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

trait UserRepository extends JpaRepository[User, java.lang.Long] {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    def findByUsername(@Param("username") username: String): Option[User]

    @Query("SELECT u FROM User u WHERE u.email = :email")
    def findByEmail(@Param("email") email: String): Option[User]
}

