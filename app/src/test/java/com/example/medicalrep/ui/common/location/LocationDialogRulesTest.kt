package com.example.medicalrep.ui.common.location

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 1. Outside the main graph (login) the dialog never shows.
 * 2. Ready: no dialog.
 * 3. Permission not granted yet and the system prompt not answered: no dialog (the system prompt comes first).
 * 4. Permission denied after the system prompt was answered: dialog.
 * 5. GPS off: dialog at once, no system prompt is involved.
 */
class LocationDialogRulesTest {

    @Test
    fun `never shows outside the main graph`() {
        assertFalse(locationDialogNeeded(isMain = false, status = LocationStatus.GpsOff, permissionPrompted = true))
        assertFalse(locationDialogNeeded(isMain = false, status = LocationStatus.PermissionDenied, permissionPrompted = true))
    }

    @Test
    fun `ready shows nothing`() {
        assertFalse(locationDialogNeeded(isMain = true, status = LocationStatus.Ready, permissionPrompted = true))
    }

    @Test
    fun `waits for the system permission prompt before showing`() {
        assertFalse(locationDialogNeeded(isMain = true, status = LocationStatus.PermissionDenied, permissionPrompted = false))
    }

    @Test
    fun `shows after the permission was denied`() {
        assertTrue(locationDialogNeeded(isMain = true, status = LocationStatus.PermissionDenied, permissionPrompted = true))
    }

    @Test
    fun `gps off shows at once`() {
        assertTrue(locationDialogNeeded(isMain = true, status = LocationStatus.GpsOff, permissionPrompted = false))
    }
}
