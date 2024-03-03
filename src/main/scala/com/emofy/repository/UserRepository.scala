package com.emofy.repository

import com.emofy.models.User
import org.springframework.data.jpa.repository.JpaRepository

trait UserRepository extends JpaRepository[User, java.lang.Long]

