package org.futo.inputmethod.latin.uix.actions

import android.os.Build
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action

val SwitchImeAction = Action(
    icon = R.drawable.switch_keyboard,
    name = R.string.switch_ime_key,
    simplePressImpl = { manager, _ ->
        val latinIME = manager.getLatinIMEForDebug()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            latinIME.switchToPreviousInputMethod()
        } else {
            latinIME.switchToNextInputMethod(false)
        }
    },
    altPressImpl = { manager, _ ->
        manager.openInputMethodPicker()
    },
    windowImpl = null,
)
