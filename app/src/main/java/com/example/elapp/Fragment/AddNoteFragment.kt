package com.example.elapp.Fragment

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.elapp.Helper.NoteDatabaseHelper
import com.example.elapp.databinding.FragmentAddNoteBinding


class AddNoteFragment : Fragment() {
    private lateinit var mBinding: FragmentAddNoteBinding
    private lateinit var mDb: NoteDatabaseHelper

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        mDb = NoteDatabaseHelper(requireContext())

        setView(mBinding.root);
        setListeners()

        return mBinding.root
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun setView(view: View) {
        // adjust height on keyboard shown
        view.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom
            val navBarHeight = insets.getInsets(WindowInsets.Type.systemGestures()).bottom
            val isKeyboardVisible = imeHeight > navBarHeight

            if (isKeyboardVisible) {
                view.setPadding(0, 0, 0, imeHeight - navBarHeight)
            } else {
                view.setPadding(0, 0, 0, 0)
            }

            insets
        }
    }

    private fun setListeners() {
        mBinding.closeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        mBinding.saveNotesBtn.setOnClickListener {
            val title = mBinding.noteTitleInput.text.toString()
            val content = mBinding.noteContentInput.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val note = com.example.elapp.Model.Note(0, title, content)
                mDb.insertNote(note)
                Toast.makeText(requireContext(), "Saved successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            }
        }
    }



}