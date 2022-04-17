package com.example.designlabel.customDialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import com.example.designlabel.R
import com.example.designlabel.customCallBack.AddNewTextCallBack


class UpdateNewText(mActivity: Activity, var callBack: AddNewTextCallBack) {

    private var customDialog: Dialog
    private var newText: EditText

    init {

        customDialog = Dialog(mActivity, R.style.full_screen_dialog)
        val layoutInflater = LayoutInflater.from(mActivity)
        val view = layoutInflater.inflate(R.layout.add_text_dialog, null)

        customDialog.window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.attributes?.windowAnimations = R.style.DialogAnimation
            }
        }

        customDialog.setContentView(view)

        newText = view.findViewById(R.id.addText)

        view.findViewById<ImageView>(R.id.clearImg).setOnClickListener {
            dismiss()
        }

        view.findViewById<ImageView>(R.id.checkImg).setOnClickListener {
            if (newText.text.toString().isEmpty()){
                newText.error = "Required Field..."
                newText.requestFocus()
            }else{
                callBack.updateText(newText.text.toString())
                dismiss()
            }
        }

    }

    fun updateEditText(string: String?) {
        if (string != null) {
            newText.setText(string)
        } else {
            newText.setText("")
        }
    }

    fun show(string: String) {
        updateEditText(string)
        customDialog.show()
    }

    fun dismiss() {
        customDialog.dismiss()
    }


}