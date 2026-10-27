package com.example.medicalrep.ui.mapper

import com.appcore.core.data.local.datastore.model.UserDS
import org.junit.Assert.assertEquals
import org.junit.Test

/** The signed-in user shows as "first last", the role name, and the two first letters as initials. */
class UserDsMapperTest {
    @Test
    fun `name role and initials come from the session user`() {
        val ui = UserDS(id = 7, firstName = "Ahmed", lastName = "Mohamed", roleName = "Rep").toUi()
        assertEquals("7", ui.id)
        assertEquals("Ahmed Mohamed", ui.displayName)
        assertEquals("Rep", ui.roleTitle)
        assertEquals("AM", ui.initials)
    }

    @Test
    fun `a missing last name leaves no trailing space`() {
        val ui = UserDS(firstName = "Ahmed").toUi()
        assertEquals("Ahmed", ui.displayName)
        assertEquals("A", ui.initials)
    }

    @Test
    fun `an empty user gives empty text`() {
        val ui = UserDS().toUi()
        assertEquals("", ui.displayName)
        assertEquals("", ui.initials)
    }
}
