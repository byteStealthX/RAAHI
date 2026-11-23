package com.iris.raahi

import com.iris.raahi.permissions.PermissionPolicy
import com.iris.raahi.permissions.PermissionResult
import com.iris.raahi.voice.VoiceCommand
import com.iris.raahi.voice.VoiceCommandParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceCommandParserTest {
    @Test
    fun navigationCommandIsParsed() {
        assertTrue(VoiceCommandParser.parse("Please start navigation") is VoiceCommand.Navigation)
    }

    @Test
    fun stopCommandIsParsedBeforeUnknown() {
        assertTrue(VoiceCommandParser.parse("stop voice") is VoiceCommand.Stop)
    }

    @Test
    fun deniedPermissionsAreExplained() {
        val text = PermissionPolicy.summarize(PermissionResult(false, true, false))
        assertTrue(text.contains("camera"))
        assertTrue(text.contains("location"))
    }

    @Test
    fun unknownCommandRetainsText() {
        val command = VoiceCommandParser.parse("find the blue door") as VoiceCommand.Unknown
        assertEquals("find the blue door", command.text)
    }
}
