package simulasi.aad.todoapp.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import simulasi.aad.todoapp.data.Task
import simulasi.aad.todoapp.setting.SettingsActivity
import simulasi.aad.todoapp.ui.ViewModelFactory
import simulasi.aad.todoapp.ui.add.AddTaskActivity
import simulasi.aad.todoapp.utils.Event
import simulasi.aad.todoapp.utils.TasksFilterType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import simulasi.aad.todoapp.R

@Suppress("DEPRECATION", "NAME_SHADOWING")
class TaskActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var taskViewModel: TaskViewModel
    private val codeAddTask = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { _ ->

            val addIntent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(addIntent, codeAddTask)
        }

        //TODO 6 : Initiate RecyclerView with LayoutManager
        recycler = findViewById(R.id.rv_task)
        recycler.layoutManager = LinearLayoutManager(this)

        initAction()

        val factory = ViewModelFactory.getInstance(this)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        taskViewModel.tasks.observe(this, Observer(this::showRecyclerView))

        //TODO 15 : Fixing bug : snackBar not show when task completed
    }

    private fun showRecyclerView(task: PagedList<Task>) {
        //TODO 7 : Submit pagedList to adapter and update database when onCheckChange (Done)
        val adapter = TaskAdapter(onCheckedChange = { task, b ->
            taskViewModel.completeTask(task, b)
            val msg = if (task.isCompleted) {
                R.string.task_marked_active
            } else {
                R.string.task_marked_complete
            }
            showSnackBar(Event(msg))
        })
        adapter.submitList(task)
        recycler.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == codeAddTask && resultCode == Activity.RESULT_OK) {
            taskViewModel.tasks
        }
    }

    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            findViewById(R.id.coordinator_layout),
            getString(message),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)
                true
            }

            R.id.action_filter -> {
                showFilteringPopUpMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = findViewById<View>(R.id.action_filter) ?: return
        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                taskViewModel.filter(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = (viewHolder as TaskAdapter.TaskViewHolder).getTask
                taskViewModel.deleteTask(task)
            }

        })
        itemTouchHelper.attachToRecyclerView(recycler)
    }
}