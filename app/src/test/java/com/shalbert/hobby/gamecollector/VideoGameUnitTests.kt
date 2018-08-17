package com.shalbert.hobby.gamecollector

import com.google.common.truth.Truth.assertThat
import com.shalbert.hobby.gamecollector.data.propertyBags.VideoGame
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.HashMap
import kotlin.concurrent.timerTask

@RunWith(Parameterized::class)
class VideoGameUnitTests(
        //Input
        var videoGame: VideoGame,
        //Expected values
        var valueRowID: String?,
        var valueUniqueID: String?,
        var valueConsole: String?,
        var valueTitle: String?,
        var valueLicensee: String?,
        var valueReleased: String?,
        var valueDateAdded: Long,
        var valueRegionLock: String?,
        var valuesComponentsOwned: HashMap<String, Boolean>,
        var valueNote: String?,
        var valueUniqueNodeId: String?,
        var valueCopiesOwned: Int
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<out Any?>> =
                listOf(
                        //Empty constructor
                        arrayOf(VideoGame(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                null,
                                HashMap<String, Boolean>(),
                                null,
                                null,
                                0
                        ),
                        arrayOf(VideoGame("666", VideoGame.NINTENDO_64,
                                "Donkey Kong", "Bethesda",
                                "March 21, 1988", 4),
                                null,
                                "666",
                                VideoGame.NINTENDO_64,
                                "Donkey Kong",
                                "Bethesda",
                                "March 21, 1988",
                                0,
                                null,
                                HashMap<String, Boolean>(),
                                null,
                                null,
                                4
                        ),
                        arrayOf(VideoGame("666", VideoGame.NINTENDO_64,
                                "Donkey Kong", "Bethesda",
                                "March 21, 1988", 4, VideoGame.USA,
                                hashMapOf(VideoGame.GAME to true, VideoGame.BOX to false, VideoGame.MANUAL to true),
                                "Hello world", "1234567890"),
                                null,
                                "666",
                                VideoGame.NINTENDO_64,
                                "Donkey Kong",
                                "Bethesda",
                                "March 21, 1988",
                                4,
                                VideoGame.USA,
                                hashMapOf(VideoGame.GAME to true, VideoGame.BOX to false, VideoGame.MANUAL to true),
                                "Hello world",
                                "1234567890",
                                0
                        )
                )
    }

    @Test
    fun `Empty constructor instantiation has expected properties`() {
        //Given a VideoGame object
        //When it's instantiated with values set via its constructor
        videoGame

        //Then all properties should equal expected values
        assertThat(videoGame.valueRowID).isEqualTo(valueRowID)
        assertThat(videoGame.valueUniqueID).isEqualTo(valueUniqueID)
        assertThat(videoGame.valueConsole).isEqualTo(valueConsole)
        assertThat(videoGame.valueTitle).isEqualTo(valueTitle)
        assertThat(videoGame.valueLicensee).isEqualTo(valueLicensee)
        assertThat(videoGame.valueReleased).isEqualTo(valueReleased)
        assertThat(videoGame.valueDateAdded).isEqualTo(valueDateAdded)
        assertThat(videoGame.valueRegionLock).isEqualTo(valueRegionLock)
        assertThat(videoGame.valuesComponentsOwned).isEqualTo(valuesComponentsOwned)
        assertThat(videoGame.valueNote).isEqualTo(valueNote)
        assertThat(videoGame.valueUniqueNodeId).isEqualTo(valueUniqueNodeId)
        assertThat(videoGame.valueCopiesOwned).isEqualTo(valueCopiesOwned)
    }
}