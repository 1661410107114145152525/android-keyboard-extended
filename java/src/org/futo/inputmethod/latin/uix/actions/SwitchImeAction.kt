package org.futo.inputmethod.latin.uix.actions

import android.content.Context
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.SettingsKey
import org.futo.inputmethod.latin.uix.getSettingBlocking
import org.futo.inputmethod.latin.uix.setSettingBlocking
import org.futo.inputmethod.latin.uix.settings.DropDownPicker
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.UserSetting

private fun getEnabledImes(context: Context): List<InputMethodInfo> {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.enabledInputMethodList.filter { it.packageName != context.packageName }
}

val SwitchIme1Setting = SettingsKey(stringPreferencesKey("switch_ime_1_target"), "")
val SwitchIme2Setting = SettingsKey(stringPreferencesKey("switch_ime_2_target"), "")
val SwitchIme3Setting = SettingsKey(stringPreferencesKey("switch_ime_3_target"), "")
val SwitchIme4Setting = SettingsKey(stringPreferencesKey("switch_ime_4_target"), "")
val SwitchIme5Setting = SettingsKey(stringPreferencesKey("switch_ime_5_target"), "")

@Composable
private fun ImePicker(setting: SettingsKey<String>) {
    val context = LocalContext.current
    val imes = remember { getEnabledImes(context) }
    var selected by remember { mutableStateOf(imes.find { it.id == context.getSettingBlocking(setting) }) }

    DropDownPicker(
        options = imes,
        selection = selected,
        onSet = {
            selected = it
            context.setSettingBlocking(setting.key, it.id)
        },
        getDisplayName = { it.loadLabel(context.packageManager).toString() },
        modifier = Modifier.fillMaxWidth()
    )
}

private fun createSwitchImeAction(
    iconRes: Int,
    nameRes: Int,
    setting: SettingsKey<String>,
    settingsTitle: Int,
    navPath: String
) = Action(
    icon = iconRes,
    name = nameRes,
    simplePressImpl = { manager, _ ->
        val context = manager.getContext()
        val targetId = context.getSettingBlocking(setting)
        if (targetId.isNotEmpty()) {
            val latinIME = manager.getLatinIMEForDebug()
            latinIME.switchInputMethod(targetId)
        } else {
            manager.openInputMethodPicker()
        }
    },
    altPressImpl = { manager, _ ->
        manager.openInputMethodPicker()
    },
    windowImpl = null,
    settingsMenu = UserSettingsMenu(
        title = settingsTitle,
        navPath = navPath,
        registerNavPath = true,
        settings = listOf(
            UserSetting(
                name = settingsTitle,
                component = { ImePicker(setting) }
            )
        )
    )
)

val SwitchIme1Action = createSwitchImeAction(
    R.drawable.keyboard_1,
    R.string.switch_ime_1_key,
    SwitchIme1Setting,
    R.string.switch_ime_settings_title,
    "actions/switch_ime_1"
)

val SwitchIme2Action = createSwitchImeAction(
    R.drawable.keyboard_2,
    R.string.switch_ime_2_key,
    SwitchIme2Setting,
    R.string.switch_ime_settings_title,
    "actions/switch_ime_2"
)

val SwitchIme3Action = createSwitchImeAction(
    R.drawable.keyboard_3,
    R.string.switch_ime_3_key,
    SwitchIme3Setting,
    R.string.switch_ime_settings_title,
    "actions/switch_ime_3"
)

val SwitchIme4Action = createSwitchImeAction(
    R.drawable.keyboard_4,
    R.string.switch_ime_4_key,
    SwitchIme4Setting,
    R.string.switch_ime_settings_title,
    "actions/switch_ime_4"
)

val SwitchIme5Action = createSwitchImeAction(
    R.drawable.keyboard_5,
    R.string.switch_ime_5_key,
    SwitchIme5Setting,
    R.string.switch_ime_settings_title,
    "actions/switch_ime_5"
)
