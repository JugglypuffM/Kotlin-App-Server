package database
import domain.Person
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Optional


object Users : Table() {
    val id : Column<String> = varchar("id", 10)
    val login : Column<String> = varchar("u_login", 45)
    val password : Column<String> = varchar("u_password", 35)
    val name : Column<String> = varchar("u_name", 20)
    val distance : Column<Int> = integer("distance").default(0)
    override val primaryKey: PrimaryKey = PrimaryKey(id, name="PK_id")
}

class DBManager {
    init {
        val dotenv = dotenv()
        Database.connect(
            url = "jdbc:postgresql://localhost:${dotenv["PORT"]}/${dotenv["NAMEDB"]}",
            driver = "org.postgresql.Driver",
            user = dotenv["USER"].toString(),
            password = dotenv["PASSWORD"].toString()
        )
        transaction {
            try {
                SchemaUtils.create(Users)
            } catch (_: Exception) {
                //TODO Добавить логирование
            }
        }
    }

    fun addUser(personID: String, person: Person) : Boolean{
        return transaction {
            return@transaction try {
                Users.insert {
                    it[id] = personID
                    it[login] = person.login
                    it[password] = person.password
                    it[name] = person.name
                }
                true
            } catch (_: Exception) { false }
        }
    }

    fun updateUser(personID: String, person: Person) : Boolean {
        return transaction {
            return@transaction try {
                Users.update({ Users.id eq personID }) {
                    it[login] = person.login
                    it[password] = person.password
                    it[name] = person.name
                    it[distance] = person.distance
                }
                true
            } catch (_: Exception) { false }
        }
    }

    fun getUser(personID: String) : Optional<Person> {
        var person: Person? = null
        transaction {
            try {
                for (user in Users.selectAll().where { Users.id eq personID }) {
                    person = Person(user[Users.name], user[Users.login], user[Users.password], user[Users.distance])
                }
            } catch (_: Exception){}
        }
        return Optional.ofNullable(person)
    }

    fun dropUser(personID: String) : Boolean {
        return transaction {
            return@transaction try {
                Users.deleteWhere { id eq personID }
                true
            } catch (_: Exception) { false }
        }
    }
}