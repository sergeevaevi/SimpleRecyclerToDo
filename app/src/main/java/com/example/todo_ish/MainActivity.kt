package com.example.todo_ish
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_ish.databinding.ActivityMainBinding
import java.util.*


class Task(s: String, i: Boolean) {
    var value: String = s
    var isDone: Boolean = i
}

class MainActivity : AppCompatActivity() {
    private val context: Context = this
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: WordListAdapter
    private val mWordList: LinkedList<Task> = LinkedList()
    private lateinit var sPref: SharedPreferences

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            onClick(view)
        }
        loadText()
        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerview)
        mAdapter = WordListAdapter(this, mWordList)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveText()
    }

    override fun onPause() {
        super.onPause()
        saveText()

    }

    override fun onStop() {
        super.onStop()
        saveText()
    }

    private fun onClick(view: View?) {
        val li = LayoutInflater.from(context)
        val promptsView: View = li.inflate(R.layout.prompt, null)
        //Создаем AlertDialog
        val mDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView)
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        val userInput = promptsView.findViewById<View>(R.id.input_text) as EditText
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK",
                DialogInterface.OnClickListener { _, _ -> //Вводим текст и отображаем в строке ввода на основном экране:
                    setTask(userInput.text.toString())
                })
            .setNegativeButton("Отмена",
                DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        //Создаем AlertDialog:
        val alertDialog: AlertDialog = mDialogBuilder.create()
        //и отображаем его:
        alertDialog.show()
    }

    private fun setTask(task: String) {
        val wordListSize = mWordList.size
        // Add a new word to the wordList.
        mWordList.addLast(Task(task, false))
        // Notify the adapter that the data has changed.
        mRecyclerView.adapter!!.notifyItemInserted(wordListSize)
        // Scroll to the bottom.
        mRecyclerView.smoothScrollToPosition(wordListSize)
        saveText()
    }

    fun onButtonClick(view: View) {
        val mPosition = view.tag as Int
        mWordList.removeAt(mPosition)
        mAdapter.notifyDataSetChanged()
        deleteText(mPosition, mWordList.size)
    }

    private fun saveText() {
        sPref = getPreferences(MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("counter", mWordList.size.toString())
        for (i in mWordList) {
            if (!i.isDone)
                ed.putString(mWordList.indexOf(i).toString(), i.value)
            else
                ed.remove(mWordList.indexOf(i).toString())
        }
        ed.apply()
    }

    private fun deleteText(num: Int, counter: Int) {
        sPref = getPreferences(MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString("counter", counter.toString())
        ed.remove(num.toString())
        ed.apply()
    }

    private fun loadText() {
        sPref = getPreferences(MODE_PRIVATE)
        val counter = sPref.getString("counter", "")
        if (counter != "") {
            for (i in 0..counter?.toInt()!!) {
                val value = sPref.getString(i.toString(), "")
                if (value != "")
                    mWordList.addLast(Task(value!!, false))
            }
        }else
            Toast.makeText(this, "Type your first To-Do", Toast.LENGTH_SHORT).show()
    }
}