package com.shalom.hobby.gamecollector

import android.provider.BaseColumns
import com.google.common.truth.Truth
import com.shalom.hobby.gamecollector.data.propertyBags.VideoGame
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Tests whether constants in VideoGame.java are equal to what they are expected to be
 */

@RunWith(Parameterized::class)
class VideoGameConstantsUnitTests(val constantValue: String, val expectedValue: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<String>> =
                listOf(
                        //Constants which can be valueRegionLock's value
                        arrayOf(VideoGame.USA, "USA"),
                        arrayOf(VideoGame.JAPAN, "Japan"),
                        arrayOf(VideoGame.EUROPEAN_UNION, "EU"),
                        //Constants for valuesComponentsOwned's keys
                        arrayOf(VideoGame.GAME, "Game"),
                        arrayOf(VideoGame.MANUAL, "Manual"),
                        arrayOf(VideoGame.BOX, "Box"),
                        //Constants for other Firebase's key values
                        arrayOf(VideoGame.KEY_ROW_ID, BaseColumns._ID),
                        arrayOf(VideoGame.KEY_UNIQUE_ID, "Unique_ID"),
                        arrayOf(VideoGame.KEY_CONSOLE, "Console"),
                        arrayOf(VideoGame.KEY_LICENSEE, "Licensee"),
                        arrayOf(VideoGame.KEY_RELEASED, "Released"),
                        arrayOf(VideoGame.KEY_TITLE, "Title"),
                        arrayOf(VideoGame.KEY_COPIES_OWNED, "Copies"),
                        arrayOf(VideoGame.KEY_DATE_ADDED_UNIX, "Date_Added_Unix"),
                        arrayOf(VideoGame.KEY_REGION_LOCK, "Region_Lock"),
                        arrayOf(VideoGame.KEY_COMPONENTS_OWNED, "Components_Owned"),
                        arrayOf(VideoGame.KEY_NOTE, "Note"),
                        arrayOf(VideoGame.KEY_UNIQUE_NODE_ID, "Unique_Node_Id"),
                        //Used when region lock isn't defined by user
                        arrayOf(VideoGame.UNDEFINED_TRAIT, "undefined"),
                        //Console names
                        arrayOf(VideoGame.NINTENDO_ENTERTAINMENT_SYSTEM, "NES"),
                        arrayOf(VideoGame.SUPER_NINTENDO_ENTERTAINMENT_SYSTEM, "SNES"),
                        arrayOf(VideoGame.NINTENDO_64, "N64"),
                        arrayOf(VideoGame.NINTENDO_GAMEBOY, "GB"),
                        arrayOf(VideoGame.NINTENDO_GAMEBOY_COLOR, "GBC")
                )
    }

    @Test
    fun `Confirm constants equal expected values`() {
        //Given a constant
        constantValue

        //When called
        val response = constantValue

        //Then it returns the expected value
        Truth.assertThat(response).isSameAs(expectedValue)
    }
}