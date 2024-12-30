package com.dicoding.picodiploma.loginwithanimation.view.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        // Set gravity to center vertically to match typical email input
        gravity = android.view.Gravity.CENTER_VERTICAL

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu diimplementasikan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parent = parent.parent
                if (parent is TextInputLayout) {
                    if (s.toString().length < 8) {
                        parent.error = "Password tidak boleh kurang dari 8 karakter"
                    } else {
                        parent.error = null // Menghilangkan pesan error
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu diimplementasikan
            }
        })
    }
}