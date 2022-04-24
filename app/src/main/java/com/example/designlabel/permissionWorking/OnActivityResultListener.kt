package com.example.designlabel.permissionWorking

import androidx.activity.result.ActivityResult

interface OnActivityResultListener {
    fun onActivityResult(
        result: ActivityResult,
        currentRequestCode: Int
    )
}