package io.github.fstaudt.hugo

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.apache.tools.ant.taskdefs.condition.Os
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_UNIX
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HugoPluginExtensionTest {
    companion object {
        const val WINDOWS_ARCHIVE = "gohugoio/hugo/releases/download/v0.111.3/hugo_extended_0.111.3_windows-amd64.zip"
        const val LINUX_ARCHIVE = "gohugoio/hugo/releases/download/v0.111.3/hugo_extended_0.111.3_linux-amd64.tar.gz"
        const val MAC_OS_ARCHIVE = "gohugoio/hugo/releases/download/v0.111.3/hugo_extended_0.111.3_darwin-universal.tar.gz"
    }

    @BeforeEach
    fun `init mocks`() {
        mockkStatic(Os::class)
    }

    @AfterEach
    fun `clear mocks`() {
        clearAllMocks()
    }

    @Test
    fun `downloadUrl should return download URL for Windows Hugo binary when OS family is Windows`() {
        every { Os.isFamily(any()) }.returns(false)
        every { Os.isFamily(FAMILY_WINDOWS) }.returns(true)
        assertThat(HugoPluginExtension().downloadUrl()).hasPath("/$WINDOWS_ARCHIVE")
    }

    @Test
    fun `downloadUrl should return download URL for Linux Hugo binary when OS family is Unix`() {
        every { Os.isFamily(any()) }.returns(false)
        every { Os.isFamily(FAMILY_UNIX) }.returns(true)
        assertThat(HugoPluginExtension().downloadUrl()).hasPath("/$LINUX_ARCHIVE")
    }

    @Test
    fun `downloadUrl should return download URL for macOS Hugo binary when OS family is macOS`() {
        every { Os.isFamily(any()) }.returns(false)
        every { Os.isFamily(FAMILY_MAC) }.returns(true)
        assertThat(HugoPluginExtension().downloadUrl()).hasPath("/$MAC_OS_ARCHIVE")
    }

    @Test
    fun `downloadUrl should return download URL for linux Hugo binary when OS family can't be derived from system property`() {
        every { Os.isFamily(any()) }.returns(false)
        assertThat(HugoPluginExtension().downloadUrl()).hasPath("/$LINUX_ARCHIVE")
    }
}
