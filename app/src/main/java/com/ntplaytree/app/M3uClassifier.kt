package com.ntplaytree.app

import java.net.URI
import java.text.Normalizer
import java.util.Locale

internal data class EpisodeNameInfo(
    val seriesTitle: String,
    val season: String,
    val episode: String,
    val episodeTitle: String
)

internal fun normalizeSearchText(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("""\p{Mn}+"""), "")
        .lowercase(Locale.ROOT)
        .trim()

private fun normalizedGroup(group: String): String = normalizeSearchText(group)
    .replace(Regex("""[|/>\\]+"""), " ")
    .replace(Regex("""\s+"""), " ")
    .trim()

private fun hasAnyTerm(value: String, terms: Set<String>): Boolean = terms.any { term ->
    Regex("""(?<![\p{L}\p{N}])${Regex.escape(term)}(?![\p{L}\p{N}])""")
        .containsMatchIn(value)
}

private val movieTerms = setOf("filme", "filmes", "movie", "movies", "vod", "cinema", "film", "films")
private val seriesTerms = setOf(
    "serie", "series", "seriado", "seriados", "tv show", "tv shows",
    "temporada", "temporadas", "season", "seasons", "episodio", "episodios",
    "episode", "episodes", "novela", "novelas", "dorama", "doramas",
    "drama legendado", "dramas legendados", "dramas"
)

/** A category containing both words is ambiguous, not a movie or series category. */
internal fun isMixedMediaGroup(group: String): Boolean {
    val value = normalizedGroup(group)
    return hasAnyTerm(value, movieTerms) && hasAnyTerm(value, seriesTerms)
}

internal fun isMovieGroup(group: String): Boolean {
    if (isMixedMediaGroup(group)) return false
    return hasAnyTerm(normalizedGroup(group), movieTerms)
}

internal fun isSeriesGroup(group: String): Boolean {
    if (isMixedMediaGroup(group) || isMovieGroup(group)) return false
    return hasAnyTerm(normalizedGroup(group), seriesTerms)
}

private val broadcastPrefixes = listOf(
    "a&e", "a e", "amc", "axn", "discovery", "espn", "band", "globo", "sbt", "record",
    "hbo", "telecine", "tnt", "warner", "sony", "history", "nat geo", "national geographic",
    "universal", "cartoon network", "nickelodeon", "disney", "paramount", "comedy central",
    "mtv", "sportv", "premiere", "combate", "gnt", "multishow", "viva", "lifetime",
    "animal planet", "cnn", "bandnews", "record news", "france 24", "bloomberg", "bbc",
    "tv brasil", "tv cultura", "rede tv", "canção nova", "tv aparecida", "food network",
    "e!", "cazé tv", "futura", "arte 1", "globo news", "discovery home", "discovery kids"
)

private fun isLikelyBroadcastChannel(name: String): Boolean {
    val value = normalizeSearchText(name).replace(Regex("""\s+"""), " ").trim()
    if (broadcastPrefixes.any { prefix ->
        value == prefix || value.startsWith("$prefix ") || value.startsWith("$prefix-") || value.startsWith("$prefix:")
    }) return true

    val channelMarkers = listOf(
        "canal ", " tv", "tv ", "ao vivo", " fhd", " hd", " uhd", " 4k", " 8k", " sd",
        "h264", "h265", "hevc"
    )
    return channelMarkers.any { it in value } && !Regex("""\b(?:19|20)\d{2}\b""").containsMatchIn(value)
}

private fun looksLikeMovieTitle(name: String): Boolean {
    val value = normalizeSearchText(name)
    // A known broadcast brand always remains a channel, even when the title contains a year.
    if (isLikelyBroadcastChannel(name)) return false
    if (Regex("""\b(?:19|20)\d{2}\b""").containsMatchIn(value)) return true
    return Regex("""\[\s*(?:cinema|filme|movie|vod|cam)\s*]""").containsMatchIn(value) ||
        Regex("""\b(?:filme|movie|vod)\b""").containsMatchIn(value)
}

internal fun parseEpisodeName(name: String, group: String): EpisodeNameInfo? {
    val patterns = listOf(
        Regex("""(?i)^(.+?)[ ._\-]+S(\d{1,2})E(\d{1,3})(?:[ ._\-]*(.*))?$"""),
        Regex("""(?i)^(.+?)[ ._\-]+(\d{1,2})x(\d{1,3})(?:[ ._\-]*(.*))?$"""),
        Regex("""(?i)^(.+?)[ ._\-]+(?:temporada|season)\s*(\d+)[ ._\-]+(?:epis[oó]dio|episode|ep)\s*(\d+)(?:[ ._\-]*(.*))?$""")
    )
    for (pattern in patterns) {
        val match = pattern.find(name.trim()) ?: continue
        val title = match.groupValues.getOrNull(1).orEmpty().trim(' ', '-', '_', '.', '|')
        val season = match.groupValues.getOrNull(2).orEmpty().ifBlank { "1" }
        val episode = match.groupValues.getOrNull(3).orEmpty().ifBlank { "1" }
        val episodeTitle = match.groupValues.getOrNull(4).orEmpty().trim(' ', '-', '_', '.', '|')
        if (title.isNotBlank()) return EpisodeNameInfo(title, season, episode, episodeTitle.ifBlank { "Episódio $episode" })
    }

    val groupParts = group.split('/', '|', '>').map { it.trim() }.filter { it.isNotBlank() }
    val seasonIndex = groupParts.indexOfFirst {
        Regex("""(?i)(?:temporada|season)\s*\d+""").containsMatchIn(it)
    }
    if (seasonIndex > 0) {
        val title = groupParts.subList(0, seasonIndex).lastOrNull { !isSeriesGroup(it) }.orEmpty()
        val season = Regex("""(?i)(?:temporada|season)\s*(\d+)""").find(groupParts[seasonIndex])?.groupValues?.getOrNull(1) ?: "1"
        val ep = Regex("""(?i)(?:ep|epis[oó]dio)\s*(\d+)""").find(name)?.groupValues?.getOrNull(1) ?: "1"
        if (title.isNotBlank()) return EpisodeNameInfo(title, season, ep, name)
    }

    // A series-specific group may contain episode links whose names do not include S01E01.
    // A mixed group such as "FILMES E SERIES" is deliberately excluded by isSeriesGroup().
    if (isSeriesGroup(group)) {
        val pathTitle = groupParts.lastOrNull {
            !isSeriesGroup(it) && !Regex("""(?i)(?:temporada|season)\s*\d+""").containsMatchIn(it)
        }
        val groupSeason = groupParts.firstNotNullOfOrNull { part ->
            Regex("""(?i)(?:temporada|season)\s*(\d+)""").find(part)?.groupValues?.getOrNull(1)
        } ?: "1"
        val standaloneSeasonEpisode = Regex("""(?i)^(?:S|T)(\d{1,2})[ ._-]*E(\d{1,3})[ ._-]*(.*)$""").find(name.trim())
        if (standaloneSeasonEpisode != null) {
            return EpisodeNameInfo(
                pathTitle ?: name,
                standaloneSeasonEpisode.groupValues[1],
                standaloneSeasonEpisode.groupValues[2],
                standaloneSeasonEpisode.groupValues[3].ifBlank { "Episódio ${standaloneSeasonEpisode.groupValues[2]}" }
            )
        }
        val leadingEpisode = Regex("""(?i)^(?:E|EP|EPIS[oó]DIO)\s*(\d+)[ ._-]*(.*)$""").find(name.trim())
        if (leadingEpisode != null) {
            return EpisodeNameInfo(
                pathTitle ?: name,
                groupSeason,
                leadingEpisode.groupValues[1],
                leadingEpisode.groupValues[2].ifBlank { "Episódio ${leadingEpisode.groupValues[1]}" }
            )
        }
        val numberedEpisode = Regex("""^(\d{1,3})[ ._-]+(.+)$""").find(name.trim())
        if (numberedEpisode != null && pathTitle != null) {
            return EpisodeNameInfo(pathTitle, groupSeason, numberedEpisode.groupValues[1], numberedEpisode.groupValues[2])
        }
        val episodeMarker = Regex("""(?i)\s+(?:ep|epis[oó]dio)\s*\d+""").find(name)?.range?.first
        val title = pathTitle ?: episodeMarker?.let { name.substring(0, it).trim() } ?: name
        return EpisodeNameInfo(title.ifBlank { name }, groupSeason, "1", name)
    }
    return null
}

internal fun classifyM3uContent(
    group: String,
    name: String,
    url: String,
    explicitType: String?,
    episodeInfo: EpisodeNameInfo?
): String {
    val explicit = normalizeSearchText(explicitType.orEmpty())
    when {
        explicit in setOf("movie", "movies", "filme", "filmes", "vod") -> return "movie"
        explicit in setOf("series", "serie", "tvshow", "tv show", "episode", "episodio") -> return "episode"
        explicit in setOf("live", "channel", "tv", "canal", "channels", "live tv") -> return "live"
    }

    // A provider may expose live channels under a generic /movie/ path. Known TV networks
    // must stay in Live TV unless the M3U explicitly declares another content type above.
    if (isLikelyBroadcastChannel(name)) return "live"

    val path = runCatching { URI(url).path.orEmpty().lowercase(Locale.ROOT) }.getOrDefault("")
    val pathSegments = path.split('/').filter { it.isNotBlank() }
    when {
        pathSegments.any { it in setOf("movie", "movies", "vod") } -> return "movie"
        pathSegments.any { it in setOf("series", "episode", "episodes") } -> return "episode"
        pathSegments.any { it in setOf("live", "channel", "channels") } -> return "live"
    }
    if (episodeInfo != null) return "episode"

    // Mixed provider groups are not evidence that an entry is a movie or a series.
    // Recognize broadcast networks and year-tagged VOD titles; default ambiguous entries to Live.
    if (isMixedMediaGroup(group)) {
        if (looksLikeMovieTitle(name)) return "movie"
        if (isLikelyBroadcastChannel(name)) return "live"
        return "live"
    }

    if (isMovieGroup(group)) return "movie"
    if (isSeriesGroup(group)) return "episode"
    if (looksLikeMovieTitle(name)) return "movie"
    if (isLikelyBroadcastChannel(name)) return "live"
    return "live"
}
