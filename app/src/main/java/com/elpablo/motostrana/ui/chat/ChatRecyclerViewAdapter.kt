package com.elpablo.motostrana.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.elpablo.motostrana.App.Companion.auth
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.ItemChatMyImageBinding
import com.elpablo.motostrana.databinding.ItemChatMyMessageBinding
import com.elpablo.motostrana.databinding.ItemChatOtherImageBinding
import com.elpablo.motostrana.databinding.ItemChatOtherMessageBinding
import com.elpablo.motostrana.model.Message
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatRecyclerViewAdapter(
    private val options: FirebaseRecyclerOptions<Message>
) :
    FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            VIEW_TYPE_MY_TEXT -> {
                val view = inflater.inflate(R.layout.item_chat_my_message, parent, false)
                val binding = ItemChatMyMessageBinding.bind(view)
                MyMessageViewHolder(binding)
            }
            VIEW_TYPE_MY_IMAGE -> {
                val view = inflater.inflate(R.layout.item_chat_my_image, parent, false)
                val binding = ItemChatMyImageBinding.bind(view)
                MyImageMessageViewHolder(binding)
            }
            VIEW_TYPE_OTHER_TEXT -> {
                val view = inflater.inflate(R.layout.item_chat_other_message, parent, false)
                val binding = ItemChatOtherMessageBinding.bind(view)
                OtherMessageViewHolder(binding)
            }
            VIEW_TYPE_OTHER_IMAGE -> {
                val view = inflater.inflate(R.layout.item_chat_other_image, parent, false)
                val binding = ItemChatOtherImageBinding.bind(view)
                OtherImageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        if (options.snapshots[position].uid == auth.currentUser?.uid && options.snapshots[position].text != null) {
            (holder as MyMessageViewHolder).bind(model)
        }
        if (options.snapshots[position].uid == auth.currentUser?.uid && options.snapshots[position].text == null) {
            (holder as MyImageMessageViewHolder).bind(model)
        }
        if (options.snapshots[position].uid != auth.currentUser?.uid && options.snapshots[position].text != null) {
            (holder as OtherMessageViewHolder).bind(model)
        }
        if (options.snapshots[position].uid != auth.currentUser?.uid && options.snapshots[position].text == null) {
            (holder as OtherImageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        if (options.snapshots[position].uid == auth.currentUser?.uid && options.snapshots[position].text != null) viewType = VIEW_TYPE_MY_TEXT
        if (options.snapshots[position].uid == auth.currentUser?.uid && options.snapshots[position].text == null) viewType = VIEW_TYPE_MY_IMAGE
        if (options.snapshots[position].uid != auth.currentUser?.uid && options.snapshots[position].text != null) viewType = VIEW_TYPE_OTHER_TEXT
        if (options.snapshots[position].uid != auth.currentUser?.uid && options.snapshots[position].text == null) viewType = VIEW_TYPE_OTHER_IMAGE
        return viewType
    }

    inner class MyMessageViewHolder(private val binding: ItemChatMyMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.chatMessage.text = item.text
            binding.chatUserName.text = item.name
            if (item.photoUrl != null) {
                binding.chatUserLogo.load(item.photoUrl){
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle_24)
                    transformations(RoundedCornersTransformation(12F))
                }
            } else {
                binding.chatUserLogo.setImageResource(R.drawable.ic_account_circle_24)
            }
        }
    }

    inner class MyImageMessageViewHolder(private val binding: ItemChatMyImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            loadImageIntoView(binding.chatImage, item.imageUrl!!)
            binding.chatUserName.text = item.name
            if (item.photoUrl != null) {
                binding.chatUserLogo.load(item.photoUrl){
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle_24)
                    transformations(RoundedCornersTransformation(12F))
                }
            } else {
                binding.chatUserLogo.setImageResource(R.drawable.ic_account_circle_24)
            }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemChatOtherMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.chatMessage.text = item.text
            binding.chatUserName.text = item.name
            if (item.photoUrl != null) {
                binding.chatUserLogo.load(item.photoUrl){
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle_24)
                    transformations(RoundedCornersTransformation(12F))
                }
            } else {
                binding.chatUserLogo.setImageResource(R.drawable.ic_account_circle_24)
            }
        }
    }

    inner class OtherImageViewHolder(private val binding: ItemChatOtherImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            loadImageIntoView(binding.chatImage, item.imageUrl!!)
            binding.chatUserName.text = item.name
            if (item.photoUrl != null) {
                binding.chatUserLogo.load(item.photoUrl){
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle_24)
                    transformations(RoundedCornersTransformation(12F))
                }
            } else {
                binding.chatUserLogo.setImageResource(R.drawable.ic_account_circle_24)
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    view.load(downloadUrl){
                        crossfade(true)
                        placeholder(R.drawable.ic_image_not_supported_96)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            view.load(url)
        }
    }

    companion object {
        const val TAG = "ChatRecyclerViewAdapter"
        const val VIEW_TYPE_MY_TEXT = 1
        const val VIEW_TYPE_MY_IMAGE = 2
        const val VIEW_TYPE_OTHER_TEXT = 3
        const val VIEW_TYPE_OTHER_IMAGE = 4
    }
}