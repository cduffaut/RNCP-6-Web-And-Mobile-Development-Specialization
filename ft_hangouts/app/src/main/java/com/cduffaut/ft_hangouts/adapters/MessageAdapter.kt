package com.cduffaut.ft_hangouts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cduffaut.ft_hangouts.R
import com.cduffaut.ft_hangouts.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    // determines whether a msg was sent or received to
    // select the appropriate layout for each message in the chat
    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    // creates the appropriate view holder based on message type
    // inflating either the sent message layout or received message layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    // populates the view holder with message content and decides whether to show
    // the timestamp based on message grouping logic
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)

        val showTimestamp = shouldShowTimestamp(position)

        if (holder is SentMessageViewHolder) {
            holder.bind(message, showTimestamp)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message, showTimestamp)
        }
    }

    // determine if horodatage should be displayed
    private fun shouldShowTimestamp(position: Int): Boolean {
        // first msg always show time
        if (position == 0) return true

        val currentMessage = getItem(position)
        val previousMessage = getItem(position - 1)

        // if msg are send / received : always show time
        if (currentMessage.isSent != previousMessage.isSent) return true

        // are msg send in the same min ?
        val currentMinutes = getMinutesFromTimestamp(currentMessage.timestamp)
        val previousMinutes = getMinutesFromTimestamp(previousMessage.timestamp)

        // if min are != : display the time
        return currentMinutes != previousMinutes
    }

    //  converts a timestamp to minutes since epoch to facilitate comparing
    //  if messages were sent within the same minute
    private fun getMinutesFromTimestamp(timestamp: Long): Long {
        return timestamp / (1000 * 60)
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)

        // handles the display of sent messages, showing or hiding the timestamp as appropriate
        fun bind(message: Message, showTimestamp: Boolean) {
            textMessage.text = message.message

            if (showTimestamp) {
                textTime.visibility = View.VISIBLE
                textTime.text = formatDate(message.timestamp)
            } else {
                textTime.visibility = View.GONE
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)

        // handles the display of received messages, showing or hiding the timestamp as appropriate
        fun bind(message: Message, showTimestamp: Boolean) {
            textMessage.text = message.message

            if (showTimestamp) {
                textTime.visibility = View.VISIBLE
                textTime.text = formatDate(message.timestamp)
            } else {
                textTime.visibility = View.GONE
            }
        }
    }
    // converts a timestamp into a readable time format (HH:mm) for display in the message bubbles
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

// determining when message items have changed through ID and content comparison
// goal: improving recyclerview performance by efficiently
class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}