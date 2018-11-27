package com.shalom.hobby.gamecollector.utils

import com.google.common.truth.Truth.assertThat
import com.shalom.hobby.gamecollector.utils.VideoGameUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Parameterized unit tests for confirming that constants in VideoGameUtils are what's expected
 */
@RunWith(Parameterized::class)
class  VideoGameUtilsConstantsUnitTests(val constantValue: String, val expectedValue: String){

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<String>> =
                listOf(
                        arrayOf(VideoGameUtils.NODE_COLLECTABLES_OWNED, "collectables_owned"),
                        arrayOf(VideoGameUtils.NODE_USERS, "users"),
                        arrayOf(VideoGameUtils.NODE_VIDEO_GAMES, "video_games")
                )
    }

    @Test
    fun `Confirm constants equal expected values`() {
        //Given a constant
        constantValue

        //When called
        val response = constantValue

        //Then it returns the expected value
        assertThat(response).isSameAs(expectedValue)
    }
}