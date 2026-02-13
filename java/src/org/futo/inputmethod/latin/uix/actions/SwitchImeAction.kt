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
import androidx.datastore.preferences.core.intPreferencesKey
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

val SwitchIme1IconSetting = SettingsKey(intPreferencesKey("switch_ime_1_icon"), R.drawable.looks_1)
val SwitchIme2IconSetting = SettingsKey(intPreferencesKey("switch_ime_2_icon"), R.drawable.looks_2)
val SwitchIme3IconSetting = SettingsKey(intPreferencesKey("switch_ime_3_icon"), R.drawable.looks_3)
val SwitchIme4IconSetting = SettingsKey(intPreferencesKey("switch_ime_4_icon"), R.drawable.looks_4)
val SwitchIme5IconSetting = SettingsKey(intPreferencesKey("switch_ime_5_icon"), R.drawable.looks_5)

/**
 * Map of switch IME actions to their icon settings.
 * Used to look up the user's configured icon for each switch keyboard action.
 */
private val switchImeIconSettings: Map<Action, SettingsKey<Int>> by lazy {
    mapOf(
        SwitchIme1Action to SwitchIme1IconSetting,
        SwitchIme2Action to SwitchIme2IconSetting,
        SwitchIme3Action to SwitchIme3IconSetting,
        SwitchIme4Action to SwitchIme4IconSetting,
        SwitchIme5Action to SwitchIme5IconSetting
    )
}

/**
 * Gets the effective icon for a switch IME action, reading from user settings if configured.
 * Returns the action's default icon for non-switch IME actions or if no custom icon is set.
 */
fun getEffectiveIconForAction(context: Context, action: Action): Int {
    val iconSetting = switchImeIconSettings[action]
    return if (iconSetting != null) {
        context.getSettingBlocking(iconSetting)
    } else {
        action.icon
    }
}

/**
 * List of available icons that can be selected for switch keyboard actions.
 */
val availableSwitchImeIcons = listOf(
    R.drawable.looks_1 to "1 (Circle)",
    R.drawable.looks_2 to "2 (Circle)",
    R.drawable.looks_3 to "3 (Circle)",
    R.drawable.looks_4 to "4 (Circle)",
    R.drawable.looks_5 to "5 (Circle)",
    R.drawable.keyboard_1 to "1 (Keyboard)",
    R.drawable.keyboard_2 to "2 (Keyboard)",
    R.drawable.keyboard_3 to "3 (Keyboard)",
    R.drawable.keyboard_4 to "4 (Keyboard)",
    R.drawable.keyboard_5 to "5 (Keyboard)",
    R.drawable.keyboard to "Keyboard",
    R.drawable.globe to "Globe",
    R.drawable.settings to "Settings",
    R.drawable.smile to "Smile"
)

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

@Composable
private fun IconPicker(iconSetting: SettingsKey<Int>) {
    val context = LocalContext.current
    var selectedIconRes by remember { mutableStateOf(context.getSettingBlocking(iconSetting)) }

    DropDownPicker(
        options = availableSwitchImeIcons,
        selection = availableSwitchImeIcons.find { it.first == selectedIconRes },
        onSet = { iconPair ->
            selectedIconRes = iconPair.first
            context.setSettingBlocking(iconSetting.key, iconPair.first)
        },
        getDisplayName = { it.second },
        modifier = Modifier.fillMaxWidth()
    )
}

private fun createSwitchImeAction(
    defaultIconRes: Int,
    iconSetting: SettingsKey<Int>,
    nameRes: Int,
    setting: SettingsKey<String>,
    settingsTitle: Int,
    iconSettingsTitle: Int,
    navPath: String
) = Action(
    icon = defaultIconRes,
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
            ),
            UserSetting(
                name = iconSettingsTitle,
                component = { IconPicker(iconSetting) }
            )
        )
    )
)

val SwitchIme1Action = createSwitchImeAction(
    R.drawable.looks_1,
    SwitchIme1IconSetting,
    R.string.switch_ime_1_key,
    SwitchIme1Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_1"
)

val SwitchIme2Action = createSwitchImeAction(
    R.drawable.looks_2,
    SwitchIme2IconSetting,
    R.string.switch_ime_2_key,
    SwitchIme2Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_2"
)

val SwitchIme3Action = createSwitchImeAction(
    R.drawable.looks_3,
    SwitchIme3IconSetting,
    R.string.switch_ime_3_key,
    SwitchIme3Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_3"
)

val SwitchIme4Action = createSwitchImeAction(
    R.drawable.looks_4,
    SwitchIme4IconSetting,
    R.string.switch_ime_4_key,
    SwitchIme4Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_4"
)

val SwitchIme5Action = createSwitchImeAction(
    R.drawable.looks_5,
    SwitchIme5IconSetting,
    R.string.switch_ime_5_key,
    SwitchIme5Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_5"
)
