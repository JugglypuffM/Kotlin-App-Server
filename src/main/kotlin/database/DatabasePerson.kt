package database

import domain.Person
import java.util.*

class DatabasePerson(private val managerDB: DBManager) : Database<Person>{
    //TODO Сделать кеш запросов, чтобы сократить количество запросов в бд
    override fun get(id: String): Optional<Person> {
        //TODO обработка ошибок - записи нет в бд
        return managerDB.getUser(id)
    }

    override fun delete(id: String): Boolean {
        //TODO Обработка ошибок - такого пользователя не существует
        return managerDB.dropUser(id)
    }

    override fun update(id: String, entry: Person): Boolean {
        //TODO обработка ошибок - пользователь не найден?
        return managerDB.updateUser(id, person = entry)
    }

    override fun add(id: String, entry: Person): Boolean {
        //TODO Обработка ошибок - такой пользователь уже добавлен
        return managerDB.addUser(id, person = entry)
    }
}