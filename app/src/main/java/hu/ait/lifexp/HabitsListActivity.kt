package hu.ait.lifexp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import hu.ait.lifexp.adapter.PostsAdapter
import hu.ait.lifexp.data.NewHabitPost
import kotlinx.android.synthetic.main.activity_habits_list.*

class HabitsListActivity : AppCompatActivity() {

    lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits_list)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            startActivity(
                Intent(this@HabitsListActivity,
                    NewHabitActivity::class.java)
            )
        }
        postsAdapter = PostsAdapter(this,
            FirebaseAuth.getInstance().currentUser!!.uid)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerPosts.layoutManager = layoutManager

        recyclerPosts.adapter = postsAdapter

        initPosts()
    }

    private fun initPosts() {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
        val habits = db.collection(
            "users"
        ).document(FirebaseAuth.getInstance().currentUser!!.uid).collection("habits")

        var allPostsListener = habits.addSnapshotListener(
            object: EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@HabitsListActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }
                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                val post = dc.document.toObject(NewHabitPost::class.java)
                                postsAdapter.addPost(post, dc.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Toast.makeText(this@HabitsListActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG).show()
                            }
                            DocumentChange.Type.REMOVED -> {
                                postsAdapter.removePostByKey(dc.document.id)
                            }
                        }
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.nav_chart -> {
                startActivity(
                    Intent(this@HabitsListActivity,
                        LifeExpectancyChartActivity::class.java)
                )
                return true
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(this@HabitsListActivity,
                        MainActivity::class.java)
                )
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}


