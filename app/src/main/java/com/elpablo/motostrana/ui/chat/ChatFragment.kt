package com.elpablo.motostrana.ui.chat

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.elpablo.motostrana.App.Companion.auth
import com.elpablo.motostrana.App.Companion.cityName
import com.elpablo.motostrana.App.Companion.regionName
import com.elpablo.motostrana.App.Companion.remoteDB
import com.elpablo.motostrana.R
import com.elpablo.motostrana.databinding.FragmentChatBinding
import com.elpablo.motostrana.model.Message
import com.elpablo.motostrana.utils.BaseFragment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private lateinit var adapter: ChatRecyclerViewAdapter
    private lateinit var manager: WrapContentLinearLayoutManager
    private var location: String? = null
    private var lastTabPosition = 0

    private val openDocument = registerForActivityResult(OpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            lastTabPosition = savedInstanceState.getInt(TAB_POSITION)
            location = savedInstanceState.getString(LOCATION).toString()
        }

        if (cityName == null || regionName == null) {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Внимание!")
            alert.setMessage("Не удается определить местоположение. Возможные причины: 1. Доступ к местоположению не предоставлен\n2. Службе требуется больше времени для определения местоположения")
            alert.setPositiveButton(R.string.settings_logout_button) { dialog, which ->
                findNavController().navigate(R.id.navigation_home)
            }
            alert.show()
        } else {
            if (lastTabPosition == 0) location = regionName
            if (lastTabPosition == 1) location = cityName
            binding.progressBar.visibility = ProgressBar.VISIBLE
            //Устанавливаем название вкладок в соотвествии с полученными данными геолокации
            regionName.also { binding.chatTabs.getTabAt(0)?.text = it }
            cityName.also { binding.chatTabs.getTabAt(1)?.text = it }

            initTabs()
            location?.let { _location-> initRecyclerView(_location) }

            binding.buttonLogout.setOnClickListener {
                activity?.finish()
            }

            binding.messageChatInput.addTextChangedListener(ButtonObserver(binding.sendButton))

            binding.sendButton.setOnClickListener {
                sendMessage()
                adapter.notifyDataSetChanged()
            }

            binding.messageChatInput.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    return@setOnEditorActionListener sendMessage()
                }
                return@setOnEditorActionListener false
            }

            binding.addMessageImageView.setOnClickListener {
                openDocument.launch(arrayOf("image/*"))
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sendMessage(): Boolean {
        if (binding.messageChatInput.text.toString() != "") {
            val message = Message(
                auth.currentUser?.uid,
                binding.messageChatInput.text.toString(),
                getUserName(),
                null,
                null,
                getPhotoUrl(),
                null,
                null
            )
            location?.let { remoteDB.child(it).push().setValue(message) }
            binding.messageChatInput.setText("")
            return true
        } else {
            return false
        }
    }

    private fun initRecyclerView(location: String) {
        binding.progressBar.visibility = ProgressBar.VISIBLE
        val messagesRef = remoteDB.child(location)
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()
        adapter = ChatRecyclerViewAdapter(options)
        manager = WrapContentLinearLayoutManager(context)
        manager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = manager
        binding.chatRecyclerView.adapter = adapter
        adapter.stopListening()                                 //для обновления при переключении tabs
        binding.progressBar.visibility = ProgressBar.GONE
        binding.headerText.text = location
        adapter.startListening()                                //для обновления при переключении tabs

        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.chatRecyclerView, adapter, manager)
        )
    }

    private fun initTabs() {
        binding.chatTabs.getTabAt(lastTabPosition)?.select()
        //Обработка Tabs и отображение контента
        binding.chatTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        location = regionName
                        lastTabPosition = tab.position
                    }
                    1 -> {
                        location = cityName
                        lastTabPosition = tab.position
                    }
                }
                location?.let { initRecyclerView(it) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = Message(
            user?.uid,
            null,
            getUserName(),
            null,
            null,
            getPhotoUrl(),
            LOADING_IMAGE_URL,
            null
        )
        remoteDB
            .child(location!!)
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.w(
                            TAG, "Unable to write message to database.",
                            databaseError.toException()
                        )
                        return@CompletionListener
                    }

                    //Загружаем файл
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                })

    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // Загружаем файл в Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot -> // После загрузки получаем ссылку и загружаем её в Firebase.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val message =
                            Message(
                                auth.currentUser?.uid,
                                null,
                                getUserName(),
                                null,
                                null,
                                getPhotoUrl(),
                                uri.toString(),
                                null
                            )
                        remoteDB
                            .child(location!!)
                            .child(key!!)
                            .setValue(message)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(
                    TAG,
                    "Image upload task was unsuccessful.",
                    e
                )
            }
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return user?.displayName
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAB_POSITION, lastTabPosition)
        outState.putString(LOCATION, location)
    }

    companion object {
        private const val TAG = "ChatFragment"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
        private const val TAB_POSITION = "lastTabPosition"
        private const val LOCATION = "location"
    }
}