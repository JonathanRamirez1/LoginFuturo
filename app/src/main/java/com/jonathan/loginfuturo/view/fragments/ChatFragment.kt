package com.jonathan.loginfuturo.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.jonathan.loginfuturo.R
import com.jonathan.loginfuturo.TotalMessagesEvent
import com.jonathan.loginfuturo.Utils.RxBus
import com.jonathan.loginfuturo.models.Message
import com.jonathan.loginfuturo.view.adapters.ChatAdapter
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatFragment : Fragment() {

    private lateinit var _view : View
    private lateinit var chatDataBaseReference : CollectionReference
    private lateinit var currentUser : FirebaseUser
    private lateinit var chatAdapter: ChatAdapter


    private val fireBaseStore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val messageList : ArrayList<Message> = ArrayList()

    private var chatSubscription : ListenerRegistration? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDataBase()
        setUPCurrentUser()
        setUpRecyclerView()
        setUpChatButton()

        subscribeToChatMessage()

        return _view
    }

    /** CUANDO SE AGREGA ALGUN VALOR VERIFICA SI EXISTE, DE EXISTIR LA AÑADE Y SINO LA CREA**/

    private fun setUpChatDataBase() {
        chatDataBaseReference = fireBaseStore.collection("chat")
    }

    /** SI EL USUAROI ESTA LOGGEADO LO DA Y SINO LO MANDA A LA PANTALLA DE LOGGIN O CUALQUIER OTRA ACCION**/

    private fun setUPCurrentUser() {
        currentUser = firebaseAuth.currentUser!!

    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter(messageList, currentUser.uid)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = chatAdapter
    }

    private fun setUpChatButton() {
        _view.buttonSend.setOnClickListener {
            val messageText = _view.editTextMessage.text.toString()
            if (messageText.isNotEmpty()) {
                val photo = currentUser.photoUrl?.let { currentUser.photoUrl.toString() } ?: run { "" }
                val message = Message(currentUser.uid, messageText, photo, Date())
                saveMessage(message)
                // Asi se borra el texto en el editText cuando se ha enviado el mensaje
                _view.editTextMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message) {
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageUrl"] = message.profileImageUrl
        newMessage["sendAt"] = message.sendAt

        chatDataBaseReference.add(newMessage)
            .addOnCompleteListener {
                Toast.makeText(context, "Message Added!", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(context, "Message Error, try again!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subscribeToChatMessage() {
        chatSubscription = chatDataBaseReference
            .orderBy("sendAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    Toast.makeText(context, "Exception!", Toast.LENGTH_SHORT).show()
                    return
                }
                snapshot?.let {
                    messageList.clear()
                    val messages = it.toObjects(Message::class.java)
                    messageList.addAll(messages.asReversed())
                    chatAdapter.notifyDataSetChanged()
                    _view.recyclerView.smoothScrollToPosition(messageList.size)
                    RxBus.publish(TotalMessagesEvent(messageList.size)) //Envia un evento (TotalMessagesEvent) cuando se ejecute y todos lo que esten escuchando (subscribeTotalMessagesEventBusReactiveStyle()
                                                                       // del InforFragment) a eventos del mismo tipo se ejecuta lo que esta en el codigo
                }
            }
        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }
}
