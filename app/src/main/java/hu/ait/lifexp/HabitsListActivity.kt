package hu.ait.lifexp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
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

}


