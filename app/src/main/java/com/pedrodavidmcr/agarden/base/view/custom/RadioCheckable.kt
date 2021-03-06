package com.pedrodavidmcr.agarden.base.view.custom

import android.view.View
import android.widget.Checkable


interface RadioCheckable : Checkable {
  fun addOnCheckChangeListener(onCheckedChangeListener: OnCheckedChangeListener)
  fun removeOnCheckChangeListener(onCheckedChangeListener: OnCheckedChangeListener)
  interface OnCheckedChangeListener {
    fun onCheckedChanged(radioGroup: View, isChecked: Boolean)
  }
}