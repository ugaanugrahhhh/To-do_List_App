package simulasi.aad.todoapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import simulasi.aad.todoapp.data.Task
import simulasi.aad.todoapp.data.TaskRepository
import kotlinx.coroutines.launch

class DetailTaskViewModel(private val taskRepository: TaskRepository): ViewModel() {

    private val _taskId = MutableLiveData<Int>()

    private val _task = _taskId.switchMap { id ->
        taskRepository.getTaskById(id)
    }
    val task: LiveData<Task> = _task

    fun setTaskId(taskId: Int) {
        if (taskId == _taskId.value) {
            return
        }
        _taskId.value = taskId
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}