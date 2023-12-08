package simulasi.aad.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import simulasi.aad.todoapp.data.Task
import simulasi.aad.todoapp.ui.ViewModelFactory
import simulasi.aad.todoapp.utils.TASK_ID
import simulasi.aad.todoapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTaskActivity : AppCompatActivity() {
    private lateinit var viewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val title = findViewById<TextView>(R.id.detail_ed_title)
        val description = findViewById<TextView>(R.id.detail_ed_description)
        val dueDate = findViewById<TextView>(R.id.detail_ed_due_date)
        val deleteButton = findViewById<Button>(R.id.btn_delete_task)

        //TODO 11 : Show detail task and implement delete action
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        val taskId = intent.getIntExtra(TASK_ID, 0)
        val taskData = getData(taskId)
        taskData.observe(this) { task ->
            title.text = task.title
            description.text = task.description
            val date = Date(task.dueDateMillis)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = formatter.format(date)
            dueDate.text = formattedDate
            deleteButton.setOnClickListener {
                viewModel.deleteTask(task)
                taskData.removeObservers(this)
                finish()
            }
        }
    }

    private fun getData(taskId: Int): LiveData<Task> {
        viewModel.setTaskId(taskId)
        return viewModel.task
    }
}