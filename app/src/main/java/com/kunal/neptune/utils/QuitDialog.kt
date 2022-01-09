package com.kunal.neptune.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.kunal.neptune.R

class QuitDialog : DialogFragment() {
    lateinit var back:AppCompatButton
    lateinit var exit:AppCompatButton
    lateinit var anim:LottieAnimationView
    lateinit var rel:RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.exit_dialog_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val newHeight = (height * 65)/100
        val newWidth = (width * 80)/100
        view.layoutParams.width = newWidth
        anim = view.findViewById(R.id.animation)
        back = view.findViewById(R.id.back)
        exit = view.findViewById(R.id.exit)
        rel = view.findViewById(R.id.rel)
        back.setOnClickListener {
            dialog?.dismiss()
        }
        exit.setOnClickListener {
            activity?.finishAffinity()
            dialog?.dismiss()
        }
        val animHeight = (newHeight * 50)/100
        anim.layoutParams.height = animHeight
        val rel_height = rel.layoutParams.height
        view.layoutParams.height = rel_height

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_fm)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnKeyListener { _, keyCode, _ ->
            // getAction to make sure this doesn't double fire
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
                true // Capture onKey
            } else false
            // Don't capture
        }
        return dialog
    }
}