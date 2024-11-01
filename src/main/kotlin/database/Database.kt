package database

import java.util.*

interface Database<T> {
    /**
     * Получения записи из таблицы
     * @throws DatabaseException Ошибка обращения к бд
     */
    fun get(login: String): Optional<T>
    /**
     * Добавление записи в таблицу
     * @throws DatabaseException Данные уже есть в бд
     */
    fun add(entry: T)
    /**
     * Обновление данных в таблице
     * @throws DatabaseException Отсутствие записи в бд
     */
    fun update(login: String, entry: T)
    /**
     * Удаление записи из таблицы
     * @throws DatabaseException Ошибка отсутствия данных
     */
    fun delete(login: String)
}

class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)