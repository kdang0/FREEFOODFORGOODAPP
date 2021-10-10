package com.example.freefoodapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ConfirmationFragment : Fragment() {
    private lateinit var successTextView: TextView
    private lateinit var returnButton: Button

    interface MainCallbacks {
        fun onReturnToLogin()
    }
    private var mainCallbacks: MainCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainCallbacks = context as MainCallbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registration_confirmation, container, false)
        successTextView = view.findViewById(R.id.successTextView) as TextView
        returnButton = view.findViewById(R.id.returnLogin) as Button
        returnButton.setOnClickListener {
            mainCallbacks?.onReturnToLogin()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance(): ConfirmationFragment {
            return ConfirmationFragment()
        }
    }
}