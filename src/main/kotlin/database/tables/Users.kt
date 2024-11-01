package database.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : IntIdTable() {
    val login : Column<String> = varchar("u_login", 45)
    val password : Column<String> = varchar("u_password", 35)
    val name : Column<String> = varchar("u_name", 20)
}