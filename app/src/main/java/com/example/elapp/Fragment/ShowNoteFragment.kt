package com.example.elapp.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.elapp.Helper.NoteDatabaseHelper
import com.example.elapp.Model.Note
import com.example.elapp.R
import com.example.elapp.databinding.FragmentShowNoteBinding

class ShowNoteFragment : Fragment() {
    private lateinit var mBinding: FragmentShowNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentShowNoteBinding.inflate(inflater, container, false)

        val notes = NoteDatabaseHelper(requireContext()).getAllNotes()
        Log.d("elms", "notes: ${notes.size}")
        mBinding.showNoteRv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.showNoteRv.adapter = ShowNoteAdapter(notes, requireContext())

        setListeners()

        return mBinding.root
    }

    private fun setListeners() {
        mBinding.closeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }



    class ShowNoteAdapter(private var notes: List<Note>, context: Context) :Adapter<ShowNoteAdapter.ShowNoteViewHolder>() {

        class ShowNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(note: Note) {
                itemView.findViewById<TextView>(R.id.list_note_title).text = note.title
                itemView.findViewById<TextView>(R.id.list_note_content).text = note.content
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowNoteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_note_item, parent, false)
            return ShowNoteViewHolder(view)
        }

        override fun onBindViewHolder(holder: ShowNoteViewHolder, position: Int) {
            holder.bind(notes[position])
        }

        override fun getItemCount(): Int {
            return notes.size
        }

    }
}