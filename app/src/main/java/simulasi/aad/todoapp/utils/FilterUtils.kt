package simulasi.aad.todoapp.utils

import androidx.sqlite.db.SimpleSQLiteQuery

object FilterUtils {

    fun getFilteredQuery(filter: TasksFilterType): SimpleSQLiteQuery {
        val simpleQuery = StringBuilder().append("SELECT * FROM tasks ")
        when (filter) {
            TasksFilterType.COMPLETED_TASKS -> {
                simpleQuery.append("WHERE completed = 1")
            }
            TasksFilterType.ACTIVE_TASKS -> {
                simpleQuery.append("WHERE completed = 0")
            }
            else -> {
            }
        }
        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}