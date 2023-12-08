@file:Suppress("DEPRECATION")

package simulasi.aad.todoapp.ui.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import simulasi.aad.todoapp.ui.add.AddTaskActivity
import simulasi.aad.todoapp.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4ClassRunner::class)
class TaskActivityTest {

    @get:Rule
    val intentRule = IntentsTestRule(TaskActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(TaskActivity::class.java)
    }

    @Test
    fun isAddTaskActivityLaunched() {
        onView(withId(R.id.fab))
            .perform(click())

        intended(hasComponent(AddTaskActivity::class.java.name))
    }
}