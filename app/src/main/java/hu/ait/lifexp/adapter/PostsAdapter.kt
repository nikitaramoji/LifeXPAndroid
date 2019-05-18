package hu.ait.lifexp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import hu.ait.lifexp.R
import hu.ait.lifexp.data.NewHabitPost
import kotlinx.android.synthetic.main.row_post.view.*

class PostsAdapter(
    private val context: Context,
    private val uId: String) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private var postsList = mutableListOf<NewHabitPost>()
    private var postKeys = mutableListOf<String>()

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_post, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = postsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (habitName) =
            postsList[holder.adapterPosition]

        holder.tvHabitName.text = habitName
        holder.btnDeletePost.setOnClickListener {
            removePost(holder.adapterPosition)
        }
        setAnimation(holder.itemView, position)
    }

    fun addPost(post: NewHabitPost, key: String) {
        postsList.add(post)
        postKeys.add(key)
        notifyDataSetChanged()
    }

    private fun removePost(index: Int) {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
        val habits = db.collection(
            "users"
        ).document(FirebaseAuth.getInstance().currentUser!!.uid).collection(
            "habits"
        ).document(
            postKeys[index]
        ).delete()

        postsList.removeAt(index)
        postKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removePostByKey(key: String) {
        val index = postKeys.indexOf(key)
        if (index != -1) {
            postsList.removeAt(index)
            postKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(
                context,
                android.R.anim.slide_in_left
            )
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHabitName: TextView = itemView.tvHabitName
        val btnDeletePost: Button = itemView.btnDeletePost
    }
}