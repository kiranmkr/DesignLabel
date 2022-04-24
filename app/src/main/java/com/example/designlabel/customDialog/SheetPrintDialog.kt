package com.example.designlabel.customDialog

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.example.designlabel.R


class SheetPrintDialog(activity: Activity, callback: SheetPrintDialogCallBack) {

    var customDialog: AlertDialog
    var btnClose: ImageView
    var btnSaveLable: TextView
    var btnSaveSheet: TextView
    var callBack: SheetPrintDialogCallBack

    init {

        this.callBack = callback

        val view = View.inflate(activity, R.layout.sheet_prtint_dialog, null)
        customDialog = AlertDialog.Builder(activity).create()
        if (customDialog.window != null) {
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            //customDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
        customDialog.setView(view)

        btnClose = view.findViewById(R.id.imageView42)
        btnSaveLable = view.findViewById(R.id.print_btn)
        btnSaveSheet = view.findViewById(R.id.print_btn2)

        btnClose.setOnClickListener {
            dismiss()
        }

        btnSaveLable.setOnClickListener {
            callBack.saveSingleLabel()
            dismiss()
        }

        btnSaveSheet.setOnClickListener {
            callBack.saveLabelSheet()
            dismiss()
        }

    }

    fun show() {
        customDialog.show()
    }

    fun dismiss() {
        callBack.sheetPrintDialogClose()
        customDialog.dismiss()
    }

    interface SheetPrintDialogCallBack {
        fun saveSingleLabel()
        fun saveLabelSheet()
        fun sheetPrintDialogClose()
    }

}