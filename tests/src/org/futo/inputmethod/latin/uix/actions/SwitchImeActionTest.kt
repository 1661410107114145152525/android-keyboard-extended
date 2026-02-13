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
    fun testDefaultIconsAreLooksIcons() {
        // Verify that the default icons for SwitchImeActions are looks_1 through looks_5
        assertEquals("SwitchIme1 default icon should be looks_1", 
            R.drawable.looks_1, SwitchIme1IconSetting.default)
        assertEquals("SwitchIme2 default icon should be looks_2", 
            R.drawable.looks_2, SwitchIme2IconSetting.default)
        assertEquals("SwitchIme3 default icon should be looks_3", 
            R.drawable.looks_3, SwitchIme3IconSetting.default)
        assertEquals("SwitchIme4 default icon should be looks_4", 
            R.drawable.looks_4, SwitchIme4IconSetting.default)
        assertEquals("SwitchIme5 default icon should be looks_5", 
            R.drawable.looks_5, SwitchIme5IconSetting.default)
    }

    @Test
    fun testAvailableIconsContainsLooksIcons() {
        // Verify that the available icons list contains all looks icons
        val iconResources = availableSwitchImeIcons.map { it.first }
        assertTrue("Available icons should contain looks_1", 
            iconResources.contains(R.drawable.looks_1))
        assertTrue("Available icons should contain looks_2", 
            iconResources.contains(R.drawable.looks_2))
        assertTrue("Available icons should contain looks_3", 
            iconResources.contains(R.drawable.looks_3))
        assertTrue("Available icons should contain looks_4", 
            iconResources.contains(R.drawable.looks_4))
        assertTrue("Available icons should contain looks_5", 
            iconResources.contains(R.drawable.looks_5))
    }

    @Test
    fun testAvailableIconsContainsKeyboardIcons() {
        // Verify that the available icons list contains all keyboard icons
        val iconResources = availableSwitchImeIcons.map { it.first }
        assertTrue("Available icons should contain keyboard_1", 
            iconResources.contains(R.drawable.keyboard_1))
        assertTrue("Available icons should contain keyboard_2", 
            iconResources.contains(R.drawable.keyboard_2))
        assertTrue("Available icons should contain keyboard_3", 
            iconResources.contains(R.drawable.keyboard_3))
        assertTrue("Available icons should contain keyboard_4", 
            iconResources.contains(R.drawable.keyboard_4))
        assertTrue("Available icons should contain keyboard_5", 
            iconResources.contains(R.drawable.keyboard_5))
    }

    @Test
    fun testAvailableIconsHaveDisplayNames() {
        // Verify that all available icons have non-empty display names
        for ((_, displayName) in availableSwitchImeIcons) {
            assertNotNull("Icon display name should not be null", displayName)
            assertTrue("Icon display name should not be empty", displayName.isNotEmpty())
        }
    }

    @Test
    fun testAvailableIconsAreUnique() {
        // Verify that all icon resources in the available list are unique
        val iconResources = availableSwitchImeIcons.map { it.first }
        val uniqueIcons = iconResources.toSet()
        assertEquals("All icon resources should be unique", 
            iconResources.size, uniqueIcons.size)
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
        assertEquals("switch_ime_1_icon", SwitchIme1IconSetting.key.name)
        assertEquals("switch_ime_2_icon", SwitchIme2IconSetting.key.name)
        assertEquals("switch_ime_3_icon", SwitchIme3IconSetting.key.name)
        assertEquals("switch_ime_4_icon", SwitchIme4IconSetting.key.name)
        assertEquals("switch_ime_5_icon", SwitchIme5IconSetting.key.name)
    }
}
