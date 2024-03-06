package com.emofy.models

import jakarta.persistence.{Entity,Table,Id,GeneratedValue,GenerationType}
import java.io.Serializable
import scala.beans.BeanProperty

@Entity
@Table(name = "users")
class User extends Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var username: String = _

  @BeanProperty
  var role: String = _

  @BeanProperty
  var email: String = _

  @BeanProperty
  var password: String = _

   //Builder (othewise it can't create new user)
  def this(username: String, email: String, password: String, role: String) {
    this()
    this.username = username
    this.email = email
    this.password = password
    this.role = role
  }
}

