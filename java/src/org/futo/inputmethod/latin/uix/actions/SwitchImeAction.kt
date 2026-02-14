package org.futo.inputmethod.latin.uix.actions

import android.content.Context
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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

val SwitchIme1IconSetting = SettingsKey(stringPreferencesKey("switch_ime_1_icon_name"), "looks_1")
val SwitchIme2IconSetting = SettingsKey(stringPreferencesKey("switch_ime_2_icon_name"), "looks_2")
val SwitchIme3IconSetting = SettingsKey(stringPreferencesKey("switch_ime_3_icon_name"), "looks_3")
val SwitchIme4IconSetting = SettingsKey(stringPreferencesKey("switch_ime_4_icon_name"), "looks_4")
val SwitchIme5IconSetting = SettingsKey(stringPreferencesKey("switch_ime_5_icon_name"), "looks_5")

/**
 * Map of icon names to drawable resource IDs.
 * These are existing icons from the app's drawable resources.
 */
private val iconNameToResource: Map<String, Int> = mapOf(
    "looks_1" to R.drawable.looks_1,
    "looks_2" to R.drawable.looks_2,
    "looks_3" to R.drawable.looks_3,
    "looks_4" to R.drawable.looks_4,
    "looks_5" to R.drawable.looks_5,
    "keyboard_1" to R.drawable.keyboard_1,
    "keyboard_2" to R.drawable.keyboard_2,
    "keyboard_3" to R.drawable.keyboard_3,
    "keyboard_4" to R.drawable.keyboard_4,
    "keyboard_5" to R.drawable.keyboard_5,
    "keyboard" to R.drawable.keyboard,
    "globe" to R.drawable.globe,
    "settings" to R.drawable.settings,
    "smile" to R.drawable.smile,
    "check" to R.drawable.check,
    "check_circle" to R.drawable.check_circle,
    "close" to R.drawable.close,
    "copy" to R.drawable.copy,
    "cut" to R.drawable.cut,
    "delete" to R.drawable.delete,
    "edit_text" to R.drawable.edit_text,
    "eye" to R.drawable.eye,
    "hash" to R.drawable.hash,
    "image" to R.drawable.image,
    "mic" to R.drawable.mic,
    "numpad" to R.drawable.numpad,
    "paste" to R.drawable.paste,
    "redo" to R.drawable.redo,
    "undo" to R.drawable.undo,
    "select_all" to R.drawable.select_all,
    "star" to R.drawable.star,
    "translate" to R.drawable.translate,
    "type" to R.drawable.type
)

/**
 * Resolves an icon name to its drawable resource ID.
 * Returns the default icon if the name is not found.
 */
fun resolveIconName(iconName: String, defaultIcon: Int): Int {
    return iconNameToResource[iconName] ?: defaultIcon
}

/**
 * Map of switch IME actions to their icon settings.
 * Used to look up the user's configured icon for each switch keyboard action.
 */
private val switchImeIconSettings: Map<Action, SettingsKey<String>> by lazy {
    mapOf(
        SwitchIme1Action to SwitchIme1IconSetting,
        SwitchIme2Action to SwitchIme2IconSetting,
        SwitchIme3Action to SwitchIme3IconSetting,
        SwitchIme4Action to SwitchIme4IconSetting,
        SwitchIme5Action to SwitchIme5IconSetting
    )
}

/**
 * Map of switch IME actions to their default icon resource IDs.
 */
private val switchImeDefaultIcons: Map<Action, Int> by lazy {
    mapOf(
        SwitchIme1Action to R.drawable.looks_1,
        SwitchIme2Action to R.drawable.looks_2,
        SwitchIme3Action to R.drawable.looks_3,
        SwitchIme4Action to R.drawable.looks_4,
        SwitchIme5Action to R.drawable.looks_5
    )
}

/**
 * Gets the effective icon for a switch IME action, reading from user settings if configured.
 * Returns the action's default icon for non-switch IME actions or if no custom icon is set.
 */
fun getEffectiveIconForAction(context: Context, action: Action): Int {
    val iconSetting = switchImeIconSettings[action]
    val defaultIcon = switchImeDefaultIcons[action] ?: action.icon
    return if (iconSetting != null) {
        val iconName = context.getSettingBlocking(iconSetting)
        resolveIconName(iconName, defaultIcon)
    } else {
        action.icon
    }
}

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
private fun IconNameInput(iconSetting: SettingsKey<String>, defaultIconName: String) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var iconName by remember { mutableStateOf(context.getSettingBlocking(iconSetting)) }
    val isValid = iconNameToResource.containsKey(iconName)
    
    OutlinedTextField(
        value = iconName,
        onValueChange = { newValue ->
            iconName = newValue
            if (iconNameToResource.containsKey(newValue)) {
                context.setSettingBlocking(iconSetting.key, newValue)
            }
        },
        label = { Text("Icon name (e.g. looks_1, globe, keyboard)") },
        isError = !isValid && iconName.isNotEmpty(),
        supportingText = {
            if (!isValid && iconName.isNotEmpty()) {
                Text("Unknown icon. Available: looks_1-5, keyboard_1-5, globe, settings, smile, etc.")
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun createSwitchImeAction(
    defaultIconRes: Int,
    defaultIconName: String,
    iconSetting: SettingsKey<String>,
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
                component = {
                    Spacer(modifier = Modifier.height(8.dp))
                    IconNameInput(iconSetting, defaultIconName)
                }
            )
        )
    )
)

val SwitchIme1Action = createSwitchImeAction(
    R.drawable.looks_1,
    "looks_1",
    SwitchIme1IconSetting,
    R.string.switch_ime_1_key,
    SwitchIme1Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_1"
)

val SwitchIme2Action = createSwitchImeAction(
    R.drawable.looks_2,
    "looks_2",
    SwitchIme2IconSetting,
    R.string.switch_ime_2_key,
    SwitchIme2Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_2"
)

val SwitchIme3Action = createSwitchImeAction(
    R.drawable.looks_3,
    "looks_3",
    SwitchIme3IconSetting,
    R.string.switch_ime_3_key,
    SwitchIme3Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_3"
)

val SwitchIme4Action = createSwitchImeAction(
    R.drawable.looks_4,
    "looks_4",
    SwitchIme4IconSetting,
    R.string.switch_ime_4_key,
    SwitchIme4Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_4"
)

val SwitchIme5Action = createSwitchImeAction(
    R.drawable.looks_5,
    "looks_5",
    SwitchIme5IconSetting,
    R.string.switch_ime_5_key,
    SwitchIme5Setting,
    R.string.switch_ime_settings_title,
    R.string.switch_ime_icon_settings_title,
    "actions/switch_ime_5"
)
