package com.ntplaytree.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class M3uClassifierTest {
    @Test fun mixedGroupIsNotMovieOrSeriesGroup() {
        assertTrue(isMixedMediaGroup("FILMES E SERIES"))
        assertFalse(isMovieGroup("FILMES E SERIES"))
        assertFalse(isSeriesGroup("FILMES E SERIES"))
    }

    @Test fun broadcastNetworksInMixedGroupStayLive() {
        val names = listOf("A&E", "A&E FHD H264", "AMC", "AMC SERIES", "AMC FHD H264", "AXN FHD H264", "Discovery Home & Health HD", "Band RS 4K")
        names.forEach { name ->
            val episode = parseEpisodeName(name, "FILMES E SERIES")
            assertEquals("$name must stay in Live TV", "live", classifyM3uContent("FILMES E SERIES", name, "https://provider.invalid/stream/123.ts", null, episode))
        }
    }

    @Test fun yearTaggedMoviesInMixedGroupStayMovies() {
        val names = listOf("A Parte dos Anjos (2012)", "Amigos Indiscretos (1999)", "As Torres Gêmeas (2006)", "A Morte do Demônio: Em Chamas [Cinema] (2026)")
        names.forEach { name ->
            val episode = parseEpisodeName(name, "FILMES E SERIES")
            assertEquals("$name must stay in Movies", "movie", classifyM3uContent("FILMES E SERIES", name, "https://provider.invalid/stream/123.mp4", null, episode))
        }
    }

    @Test fun broadcastNamesWithYearsStillStayLive() {
        val names = listOf("AMC 2026", "Band RS 2026", "Discovery HD 2026")
        names.forEach { name ->
            assertEquals("$name must stay in Live TV", "live", classifyM3uContent("FILMES E SERIES", name, "https://provider.invalid/stream/123.ts", null, null))
        }
    }

    @Test fun knownBroadcastChannelsStayLiveEvenIfProviderUsesMoviePath() {
        val names = listOf("A&E", "AMC", "AMC FHD H264", "AXN FHD H264", "Discovery Home & Health HD")
        names.forEach { name ->
            assertEquals(
                "$name must stay in Live TV even with a misleading movie URL",
                "live",
                classifyM3uContent("FILMES E SERIES", name, "https://provider.invalid/movie/user/pass/123.mp4", null, null)
            )
        }
    }

    @Test fun mixedGroupEpisodePatternStaysSeries() {
        val name = "Minha Série S01E02 - Episódio 2"
        assertEquals("episode", classifyM3uContent("FILMES E SERIES", name, "https://provider.invalid/stream/123.mp4", null, parseEpisodeName(name, "FILMES E SERIES")))
    }

    @Test fun explicitTypesAndXtreamPathsTakePriority() {
        assertEquals("movie", classifyM3uContent("TV", "Título", "https://host.invalid/live/u/p/1.ts", "movie", null))
        assertEquals("live", classifyM3uContent("Filmes", "Canal", "https://host.invalid/live/u/p/1.ts", null, null))
        assertEquals("movie", classifyM3uContent("Sem categoria", "Título", "https://host.invalid/movie/u/p/1.mp4", null, null))
        assertEquals("episode", classifyM3uContent("Sem categoria", "Título S01E02", "https://host.invalid/series/u/p/1.mp4", null, parseEpisodeName("Título S01E02", "Sem categoria")))
    }

    @Test fun dedicatedGroupsRemainSeparate() {
        assertEquals("movie", classifyM3uContent("Filmes | Drama", "Título sem ano", "https://host.invalid/1.mp4", null, null))
        assertEquals("episode", classifyM3uContent("Dramas Legendados", "E01 - Primeiro", "https://host.invalid/1.mp4", null, parseEpisodeName("E01 - Primeiro", "Dramas Legendados")))
    }
}
