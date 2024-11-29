package database.dao

import domain.user.Account
import java.util.Optional

@Deprecated("Mock for whole DatabaseManager should be implemented instead")
class DbTableMock : DAO<Account> {
    private val data = mutableMapOf<String, Account>()

    override fun get(login: String): Optional<Account> {
        return Optional.ofNullable(data[login])
    }

    override fun add(entry: Account) {
        data[entry.login] = entry
    }

    override fun update(login: String, entry: Account) {
        if (data.containsKey(login)) {
            data[login] = entry
        }
    }

    override fun delete(login: String) {
        data.remove(login) != null
    }
}