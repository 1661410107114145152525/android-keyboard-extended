package org.futo.inputmethod.latin.uix.actions

import org.futo.inputmethod.latin.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for SwitchImeAction configuration, particularly icon settings.
 */
class SwitchImeActionTest {

    @Test
    fun testDefaultIconNamesAreLooksIcons() {
        // Verify that the default icon names for SwitchImeActions are looks_1 through looks_5
        assertEquals("SwitchIme1 default icon name should be looks_1", 
            "looks_1", SwitchIme1IconSetting.default)
        assertEquals("SwitchIme2 default icon name should be looks_2", 
            "looks_2", SwitchIme2IconSetting.default)
        assertEquals("SwitchIme3 default icon name should be looks_3", 
            "looks_3", SwitchIme3IconSetting.default)
        assertEquals("SwitchIme4 default icon name should be looks_4", 
            "looks_4", SwitchIme4IconSetting.default)
        assertEquals("SwitchIme5 default icon name should be looks_5", 
            "looks_5", SwitchIme5IconSetting.default)
    }

    @Test
    fun testIconNameToResourceContainsLooksIcons() {
        // Verify that the icon name mapping contains all looks icons
        assertEquals("looks_1 should map to R.drawable.looks_1",
            R.drawable.looks_1, resolveIconName("looks_1", 0))
        assertEquals("looks_2 should map to R.drawable.looks_2",
            R.drawable.looks_2, resolveIconName("looks_2", 0))
        assertEquals("looks_3 should map to R.drawable.looks_3",
            R.drawable.looks_3, resolveIconName("looks_3", 0))
        assertEquals("looks_4 should map to R.drawable.looks_4",
            R.drawable.looks_4, resolveIconName("looks_4", 0))
        assertEquals("looks_5 should map to R.drawable.looks_5",
            R.drawable.looks_5, resolveIconName("looks_5", 0))
    }

    @Test
    fun testIconNameToResourceContainsKeyboardIcons() {
        // Verify that the icon name mapping contains all keyboard icons
        assertEquals("keyboard_1 should map to R.drawable.keyboard_1",
            R.drawable.keyboard_1, resolveIconName("keyboard_1", 0))
        assertEquals("keyboard_2 should map to R.drawable.keyboard_2",
            R.drawable.keyboard_2, resolveIconName("keyboard_2", 0))
        assertEquals("keyboard_3 should map to R.drawable.keyboard_3",
            R.drawable.keyboard_3, resolveIconName("keyboard_3", 0))
        assertEquals("keyboard_4 should map to R.drawable.keyboard_4",
            R.drawable.keyboard_4, resolveIconName("keyboard_4", 0))
        assertEquals("keyboard_5 should map to R.drawable.keyboard_5",
            R.drawable.keyboard_5, resolveIconName("keyboard_5", 0))
    }

    @Test
    fun testResolveIconNameReturnsDefaultForUnknown() {
        // Verify that unknown icon names return the default
        val defaultIcon = R.drawable.looks_1
        assertEquals("Unknown icon name should return default",
            defaultIcon, resolveIconName("unknown_icon", defaultIcon))
        assertEquals("Empty icon name should return default",
            defaultIcon, resolveIconName("", defaultIcon))
    }

    @Test
    fun testSwitchImeActionsHaveCorrectDefaultIcons() {
        // Verify that SwitchImeAction objects use the correct default icons
        assertEquals("SwitchIme1Action should use looks_1 icon", 
            R.drawable.looks_1, SwitchIme1Action.icon)
        assertEquals("SwitchIme2Action should use looks_2 icon", 
            R.drawable.looks_2, SwitchIme2Action.icon)
        assertEquals("SwitchIme3Action should use looks_3 icon", 
            R.drawable.looks_3, SwitchIme3Action.icon)
        assertEquals("SwitchIme4Action should use looks_4 icon", 
            R.drawable.looks_4, SwitchIme4Action.icon)
        assertEquals("SwitchIme5Action should use looks_5 icon", 
            R.drawable.looks_5, SwitchIme5Action.icon)
    }

    @Test
    fun testSwitchImeActionsHaveSettingsMenu() {
        // Verify that all SwitchImeActions have a settings menu configured
        assertNotNull("SwitchIme1Action should have settings menu", 
            SwitchIme1Action.settingsMenu)
        assertNotNull("SwitchIme2Action should have settings menu", 
            SwitchIme2Action.settingsMenu)
        assertNotNull("SwitchIme3Action should have settings menu", 
            SwitchIme3Action.settingsMenu)
        assertNotNull("SwitchIme4Action should have settings menu", 
            SwitchIme4Action.settingsMenu)
        assertNotNull("SwitchIme5Action should have settings menu", 
            SwitchIme5Action.settingsMenu)
    }

    @Test
    fun testSwitchImeActionsHaveIconSettings() {
        // Verify that the settings menu includes icon settings (2 settings: target + icon)
        assertEquals("SwitchIme1Action settings menu should have 2 settings", 
            2, SwitchIme1Action.settingsMenu?.settings?.size)
        assertEquals("SwitchIme2Action settings menu should have 2 settings", 
            2, SwitchIme2Action.settingsMenu?.settings?.size)
        assertEquals("SwitchIme3Action settings menu should have 2 settings", 
            2, SwitchIme3Action.settingsMenu?.settings?.size)
        assertEquals("SwitchIme4Action settings menu should have 2 settings", 
            2, SwitchIme4Action.settingsMenu?.settings?.size)
        assertEquals("SwitchIme5Action settings menu should have 2 settings", 
            2, SwitchIme5Action.settingsMenu?.settings?.size)
    }

    @Test
    fun testIconSettingsHaveCorrectKeys() {
        // Verify that icon settings have unique preference keys
        val keys = listOf(
            SwitchIme1IconSetting.key.name,
            SwitchIme2IconSetting.key.name,
            SwitchIme3IconSetting.key.name,
            SwitchIme4IconSetting.key.name,
            SwitchIme5IconSetting.key.name
        )
        val uniqueKeys = keys.toSet()
        assertEquals("All icon setting keys should be unique", keys.size, uniqueKeys.size)
        
        // Verify expected key names
        assertEquals("switch_ime_1_icon_name", SwitchIme1IconSetting.key.name)
        assertEquals("switch_ime_2_icon_name", SwitchIme2IconSetting.key.name)
        assertEquals("switch_ime_3_icon_name", SwitchIme3IconSetting.key.name)
        assertEquals("switch_ime_4_icon_name", SwitchIme4IconSetting.key.name)
        assertEquals("switch_ime_5_icon_name", SwitchIme5IconSetting.key.name)
    }
}
