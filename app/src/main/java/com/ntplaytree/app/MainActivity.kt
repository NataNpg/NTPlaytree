package com.ntplaytree.app
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory.Options
import android.media.MediaCodecList
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.net.Uri
import android.os.Bundle
import android.util.Xml
import android.view.KeyEvent as AndroidKeyEvent
import android.util.Base64
import android.util.LruCache
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer as VlcMediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.net.HttpURLConnection
import java.io.File
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.Reader
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.net.URLDecoder
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.UUID
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// Alpha 21: identidade cinematográfica em grafite, lima neon, laranja e violeta.
private val Navy = Color(0xFF090D12)
private val Panel = Color(0xFF151D25)
private val PanelRaised = Color(0xFF202B35)
private val Blue = Color(0xFFFF8A4C) // accent principal: laranja elétrico
private val Amber = Color(0xFFFFC857)
private val Muted = Color(0xFFA5B1BD)
private val White = Color(0xFFF7F8FA)
private val AccentPink = Color(0xFFB58CFF)
private val Cyan = Color(0xFFC6F36A) // lima neon
private val ErrorCoral = Color(0xFFFF667A)
private const val MAX_M3U_LINE_CHARS = 128 * 1024
private const val MAX_M3U_ITEMS = 65_000
private const val OVERSIZED_M3U_LINE = "\u0000OVERSIZED"
private val artworkCache = object : LruCache<String, Bitmap>(8 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(9, 13, 18)
        window.navigationBarColor = android.graphics.Color.rgb(9, 13, 18)
        setContent { NTPlaytreeApp() }
    }
}

private data class PlaylistSource(
    val id: String,
    val name: String,
    val location: String,
    val isLocalFile: Boolean,
    val type: String = "m3u",
    val username: String = "",
    val password: String = ""
)

private data class Channel(
    val id: String,
    val name: String,
    val url: String,
    val group: String,
    val logo: String?,
    val tvgId: String?,
    val sourceId: String,
    val kind: String = "live",
    val seriesId: String? = null,
    val seriesTitle: String? = null,
    val seasonNumber: String? = null,
    val episodeNumber: String? = null,
    val description: String? = null,
    val year: String? = null,
    val headers: Map<String, String> = emptyMap()
)

private data class SourceLoadResult(val channels: List<Channel>, val epgUrl: String? = null, val warning: String? = null)

private data class EpgProgram(
    val channelId: String,
    val title: String,
    val description: String,
    val startMs: Long,
    val endMs: Long
)

private data class PlaybackHistory(
    val channel: Channel,
    val positionMs: Long,
    val durationMs: Long,
    val playedAtMs: Long
)

@Composable
private fun NTPlaytreeApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var splashVisible by remember { mutableStateOf(true) }
    var section by remember { mutableStateOf("Início") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var transferAddress by remember { mutableStateOf<String?>(null) }
    var sourceName by remember { mutableStateOf("Minha lista") }
    var sourceUrl by remember { mutableStateOf("") }
    var sourceType by remember { mutableStateOf("m3u") }
    var xtreamUsername by remember { mutableStateOf("") }
    var xtreamPassword by remember { mutableStateOf("") }
    var sources by remember { mutableStateOf(readSources(context)) }
    var channels by remember { mutableStateOf(emptyList<Channel>()) }
    var favorites by remember { mutableStateOf(readFavorites(context)) }
    var history by remember { mutableStateOf(readHistory(context)) }
    var epgPrograms by remember { mutableStateOf(emptyList<EpgProgram>()) }
    var epgStatus by remember { mutableStateOf("Guia não carregado") }
    var selectedChannel by remember { mutableStateOf<Channel?>(null) }
    var selectedSeries by remember { mutableStateOf<Channel?>(null) }
    var loading by remember { mutableStateOf(false) }
    var loadProgress by remember { mutableStateOf("") }
    var epgUrlsBySource by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var epgRefreshVersion by remember { mutableIntStateOf(0) }
    var status by remember { mutableStateOf("Adicione uma lista M3U ou Xtream Codes para começar.") }
    var search by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("Todas") }
    var refreshVersion by remember { mutableIntStateOf(0) }
    val seriesCatalog = remember(channels, section) {
        if (section == "Séries") buildSeriesCatalog(channels)
        else emptyList<Channel>() to emptyMap<String, List<Channel>>()
    }
    val openContent: (Channel) -> Unit = { item ->
        if (item.kind == "series") {
            selectedSeries = item
        } else {
            history = touchHistory(history, item)
            writeHistory(context, history)
            selectedChannel = item
        }
    }

    val sectionContent: @Composable () -> Unit = {
        when (section) {
            "TV ao vivo", "Filmes", "Séries", "Favoritos" -> {
                val sectionItems = when (section) {
                    "Filmes" -> channels.filter { it.kind == "movie" }
                    "Séries" -> seriesCatalog.first
                    "Favoritos" -> channels.filter { it.url.isNotBlank() && it.url in favorites }
                    else -> channels.filter { it.kind == "live" }
                }
                LibrarySection(
                    title = section,
                    channels = sectionItems,
                    favorites = favorites,
                    epgPrograms = epgPrograms,
                    search = search,
                    onSearch = { search = it },
                    selectedGroup = selectedGroup,
                    onGroupSelected = { selectedGroup = it },
                    loading = loading,
                    status = status,
                    onPlay = openContent,
                    onFavorite = { channel ->
                        if (channel.url.isNotBlank()) {
                            favorites = favorites.toMutableSet().apply {
                                if (!add(channel.url)) remove(channel.url)
                            }
                            writeFavorites(context, favorites)
                        }
                    },
                    onAddList = { showAddDialog = true },
                    onTransfer = { showTransferDialog = true }
                )
            }
            "Guia" -> EpgSection(
                channels = channels.filter { it.kind == "live" },
                programs = epgPrograms,
                status = epgStatus,
                onPlay = openContent
            )
            "Histórico" -> HistorySection(
                history = history,
                onPlay = openContent,
                onClear = {
                    history = emptyList()
                    writeHistory(context, history)
                }
            )
            "Listas" -> PlaylistSection(
                sources = sources,
                channels = channels,
                loading = loading,
                progress = loadProgress,
                status = status,
                onAdd = { showAddDialog = true },
                onRefresh = { refreshVersion++ },
                onRemove = { source -> sources = sources.filterNot { it.id == source.id } },
                onTransfer = { showTransferDialog = true }
            )
            "Enviar do celular" -> TransferSection(onShowQr = { showTransferDialog = true })
            "Configurações" -> SettingsSection(
                onClearFavorites = {
                    favorites = emptySet()
                    writeFavorites(context, favorites)
                },
                onClearHistory = {
                    history = emptyList()
                    writeHistory(context, history)
                }
            )
            else -> HomeSection(
                channelCount = channels.size,
                sourceCount = sources.size,
                loading = loading,
                status = status,
                onOpenLive = { section = "TV ao vivo" },
                onOpenMovies = { section = "Filmes" },
                onOpenSeries = { section = "Séries" },
                onAddList = { showAddDialog = true },
                onTransfer = { showTransferDialog = true },
                recent = history.take(8).map { it.channel }.ifEmpty { channels.take(8) },
                onPlay = openContent
            )
        }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val source = PlaylistSource(
                id = UUID.randomUUID().toString(),
                name = sourceName.ifBlank { "Lista local" },
                location = uri.toString(),
                isLocalFile = true,
                type = "m3u"
            )
            sources = sources + source
            showAddDialog = false
            sourceName = "Minha lista"
            sourceUrl = ""
            sourceType = "m3u"
        }
    }

    LaunchedEffect(Unit) {
        delay(1350)
        splashVisible = false
    }

    LaunchedEffect(sources, refreshVersion) {
        writeSources(context, sources)
        if (sources.isEmpty()) {
            channels = emptyList()
            epgPrograms = emptyList()
            epgUrlsBySource = emptyMap()
            loading = false
            loadProgress = ""
            status = "Adicione uma lista M3U ou Xtream Codes para começar."
            epgStatus = "Nenhuma fonte de EPG disponível"
            epgRefreshVersion++
        } else {
            loading = true
            loadProgress = "Preparando ${sources.size} ${if (sources.size == 1) "lista" else "listas"}"
            status = "Conectando às fontes cadastradas..."
            val previousChannels = channels
            val previousEpgUrls = epgUrlsBySource
            val activeSourceIds = sources.map { it.id }.toSet()
            val loadedBySource = linkedMapOf<String, SourceLoadResult>()
            val errors = mutableListOf<String>()
            val warnings = mutableListOf<String>()

            try {
                sources.forEachIndexed { index, source ->
                    loadProgress = "${source.name} • ${index + 1}/${sources.size}"
                    val result = withContext(Dispatchers.IO) {
                        runCatching {
                            loadSource(context, source) { detail ->
                                Handler(Looper.getMainLooper()).post {
                                    if (loading) loadProgress = "${source.name} • $detail"
                                }
                            }
                        }
                    }
                    result.onSuccess { value ->
                        loadedBySource[source.id] = value
                        value.warning?.let { warnings.add("${source.name}: $it") }
                    }.onFailure { error ->
                        errors.add("${source.name}: ${describeSourceLoadFailure(error, source.location)}")
                    }
                }

                // Keep the last successful catalog for sources that failed this refresh.
                // A failed network request must not erase content that was already loaded.
                val retained = previousChannels.filter { channel ->
                    channel.sourceId in activeSourceIds && channel.sourceId !in loadedBySource.keys
                }
                val refreshed = loadedBySource.values.flatMap { it.channels }
                channels = (retained + refreshed).distinctBy { it.id }

                val retainedEpgUrls = previousEpgUrls.filterKeys { sourceId ->
                    sourceId in activeSourceIds && sourceId !in loadedBySource.keys
                }
                val refreshedEpgUrls = loadedBySource.mapNotNull { (sourceId, value) ->
                    val source = sources.firstOrNull { it.id == sourceId }
                    val url = value.epgUrl ?: source?.let { xtreamEpgUrl(it) }
                    if (url.isNullOrBlank()) null else sourceId to url
                }.toMap()
                epgUrlsBySource = retainedEpgUrls + refreshedEpgUrls
                epgRefreshVersion++

                val newlyLoadedCount = refreshed.size
                status = when {
                    loadedBySource.isNotEmpty() && errors.isEmpty() ->
                        "$newlyLoadedCount itens carregados de ${loadedBySource.size} ${if (loadedBySource.size == 1) "lista" else "listas"}."
                    loadedBySource.isNotEmpty() -> buildString {
                        append("$newlyLoadedCount itens carregados; ${errors.size} fonte(s) falharam.")
                        if (retained.isNotEmpty()) append(" Mantive ${retained.size} itens da última atualização das fontes com erro.")
                        append(" ")
                        append(errors.joinToString(" • "))
                    }
                    retained.isNotEmpty() ->
                        "Não foi possível atualizar. Mantive ${retained.size} itens da última atualização. ${errors.joinToString(" • ")}"
                    errors.isNotEmpty() -> errors.joinToString(" • ")
                    else -> "Nenhum conteúdo foi retornado pelas fontes cadastradas."
                }
                if (warnings.isNotEmpty()) status += " Aviso: ${warnings.joinToString(" • ")}"
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                status = "Falha ao atualizar as listas: ${error.message ?: error.javaClass.simpleName}."
            } finally {
                loading = false
                loadProgress = ""
            }
        }
    }

    // EPG is deliberately loaded after the channel catalog. A slow XMLTV endpoint
    // must never keep TV, movies or series hidden behind the main loading spinner.
    LaunchedEffect(epgRefreshVersion) {
        val urls = epgUrlsBySource.values.distinct()
        if (urls.isEmpty()) {
            epgPrograms = emptyList()
            epgStatus = "A lista não informou um endereço XMLTV"
        } else {
            epgStatus = "Atualizando guia em segundo plano..."
            try {
                val results = withContext(Dispatchers.IO) {
                    val programs = mutableListOf<EpgProgram>()
                    val failures = mutableListOf<String>()
                    urls.forEach { url ->
                        val result = runCatching { fetchXmltv(url) }
                        result.onSuccess { programs.addAll(it) }
                            .onFailure { failures.add(describeSourceLoadFailure(it, url)) }
                    }
                    programs to failures
                }
                epgPrograms = results.first
                epgStatus = when {
                    results.first.isNotEmpty() -> "${results.first.size} programas carregados"
                    results.second.isNotEmpty() -> "Não foi possível carregar o XMLTV: ${results.second.first()}"
                    else -> "O servidor não retornou programas XMLTV"
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                epgStatus = "Falha ao atualizar o guia: ${error.message ?: error.javaClass.simpleName}"
            } finally {
                }
        }
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val useSidebar = screenWidthDp >= 760
    val horizontalPadding = if (screenWidthDp >= 700) 20.dp else 12.dp

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            secondary = Amber,
            background = Navy,
            surface = Panel,
            onBackground = White,
            onSurface = White
        )
    ) {
        Box(Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Navy, Color(0xFF17212A), Color(0xFF211923)))
        )) {
            if (splashVisible) {
                Box(Modifier.fillMaxSize().background(Navy), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.ntplaytree_splash),
                        contentDescription = "NTPlaytree",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else if (useSidebar) {
                Row(Modifier.fillMaxSize()) {
                    SidebarNavigation(
                        section = section,
                        compact = true,
                        onSelect = {
                            section = it
                            selectedGroup = "Todas"
                            search = ""
                        }
                    )
                    Column(
                        Modifier.weight(1f).fillMaxHeight().padding(
                            start = 14.dp, end = 16.dp, top = 9.dp, bottom = 9.dp
                        )
                    ) {
                        WideHeader(section = section, status = if (loading) "Carregando • $loadProgress" else status)
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.weight(1f).fillMaxWidth()) { sectionContent() }
                    }
                }
            } else {
                Column(Modifier.fillMaxSize().padding(horizontal = horizontalPadding, vertical = 9.dp)) {
                    Header(status = if (loading) "CARREGANDO • $loadProgress" else status)
                    Spacer(Modifier.height(6.dp))
                    TopNavigationBar(section = section, onSelect = {
                        section = it
                        selectedGroup = "Todas"
                        search = ""
                    })
                    Spacer(Modifier.height(7.dp))
                    Box(Modifier.weight(1f).fillMaxWidth()) { sectionContent() }
                }
            }

            if (!splashVisible) {

                if (showAddDialog) {
                    AddListDialog(
                        sourceType = sourceType,
                        onSourceTypeChange = { sourceType = it },
                        name = sourceName,
                        onNameChange = { sourceName = it },
                        url = sourceUrl,
                        onUrlChange = { sourceUrl = it },
                        username = xtreamUsername,
                        onUsernameChange = { xtreamUsername = it },
                        password = xtreamPassword,
                        onPasswordChange = { xtreamPassword = it },
                        onDismiss = { showAddDialog = false },
                        onImportUrl = {
                            val newSource = PlaylistSource(
                                id = UUID.randomUUID().toString(),
                                name = sourceName.ifBlank { "Minha lista" },
                                location = sourceUrl.trim(),
                                isLocalFile = false,
                                type = "m3u"
                            )
                            sources = sources + newSource
                            showAddDialog = false
                            sourceName = "Minha lista"
                            sourceUrl = ""
                            sourceType = "m3u"
                        },
                        onImportXtream = {
                            val newSource = PlaylistSource(
                                id = UUID.randomUUID().toString(),
                                name = sourceName.ifBlank { "Minha lista Xtream" },
                                location = sourceUrl.trim().trimEnd('/'),
                                isLocalFile = false,
                                type = "xtream",
                                username = xtreamUsername.trim(),
                                password = xtreamPassword
                            )
                            sources = sources + newSource
                            showAddDialog = false
                            sourceName = "Minha lista"
                            sourceUrl = ""
                            xtreamUsername = ""
                            xtreamPassword = ""
                            sourceType = "m3u"
                        },
                        onChooseFile = { filePicker.launch(arrayOf("*/*")) }
                    )
                }

                if (showTransferDialog) {
                    val lanServer = remember {
                        LanImportServer(context.applicationContext) { payload ->
                            scope.launch {
                                val importedSource = withContext(Dispatchers.IO) {
                                    if (payload.content.isNotBlank()) {
                                        val directory = File(context.filesDir, "playlists").apply { mkdirs() }
                                        val file = File(directory, "${UUID.randomUUID()}.m3u")
                                        file.writeText(payload.content, Charsets.UTF_8)
                                        PlaylistSource(
                                            id = UUID.randomUUID().toString(),
                                            name = payload.name,
                                            location = Uri.fromFile(file).toString(),
                                            isLocalFile = true,
                                            type = "m3u"
                                        )
                                    } else {
                                        PlaylistSource(
                                            id = UUID.randomUUID().toString(),
                                            name = payload.name,
                                            location = payload.url,
                                            isLocalFile = false,
                                            type = "m3u"
                                        )
                                    }
                                }
                                sources = sources + importedSource
                                showTransferDialog = false
                            }
                        }
                    }
                    LaunchedEffect(lanServer) {
                        transferAddress = withContext(Dispatchers.IO) { lanServer.start() ?: "ERROR" }
                    }
                    DisposableEffect(lanServer) {
                        onDispose { lanServer.close(); transferAddress = null }
                    }
                    LanTransferDialog(
                        address = transferAddress,
                        onDismiss = { showTransferDialog = false }
                    )
                }

                selectedSeries?.let { series ->
                    val source = sources.firstOrNull { it.id == series.sourceId }
                    if (source != null) {
                        SeriesEpisodesDialog(
                            series = series,
                            source = source,
                            preloadedEpisodes = seriesCatalog.second[series.id].orEmpty(),
                            onDismiss = { selectedSeries = null },
                            onPlayEpisode = { episode ->
                                selectedSeries = null
                                openContent(episode)
                            }
                        )
                    } else {
                        AlertDialog(
                            onDismissRequest = { selectedSeries = null },
                            title = { Text("Lista não encontrada") },
                            text = { Text("Não foi possível localizar a origem desta série neste dispositivo. Atualize a lista e tente novamente.") },
                            confirmButton = { TextButton(onClick = { selectedSeries = null }) { Text("Fechar") } }
                        )
                    }
                }
                selectedChannel?.let { channel ->
                    val savedPosition = history.firstOrNull { it.channel.url == channel.url }?.positionMs ?: 0L
                    PlayerDialog(
                        channel = channel,
                        initialPositionMs = savedPosition,
                        onProgress = { position, duration ->
                            history = updateHistory(history, channel, position, duration)
                            writeHistory(context, history)
                        },
                        onDismiss = { selectedChannel = null }
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarNavigation(section: String, compact: Boolean, onSelect: (String) -> Unit) {
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(300)
        runCatching { firstFocus.requestFocus() }
    }
    val entries = listOf(
        Triple("Início", "Início", Icons.Default.Home),
        Triple("TV ao vivo", "TV", Icons.Default.LiveTv),
        Triple("Filmes", "Filmes", Icons.Default.Movie),
        Triple("Séries", "Séries", Icons.Default.VideoLibrary),
        Triple("Favoritos", "Favoritos", Icons.Default.FavoriteBorder),
        Triple("Guia", "Guia", Icons.Default.LiveTv),
        Triple("Histórico", "Histórico", Icons.Default.Refresh),
        Triple("Listas", "Listas", Icons.Default.FolderOpen),
        Triple("Enviar do celular", "Enviar", Icons.Default.FolderOpen)
    )
    Column(
        Modifier.width(if (compact) 72.dp else 188.dp).fillMaxHeight()
            .background(Brush.verticalGradient(listOf(Color(0xFF18232B), Color(0xFF090D12))))
            .border(1.dp, Color.White.copy(alpha = .06f))
            .padding(horizontal = if (compact) 6.dp else 12.dp, vertical = 10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ntplaytree_logo),
            contentDescription = "NTPlaytree",
            modifier = Modifier.size(if (compact) 44.dp else 40.dp).align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(14.dp))
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            entries.forEachIndexed { index, entry ->
                if (compact) {
                    RailNavigationItem(
                        label = entry.second,
                        icon = entry.third,
                        selected = section == entry.first,
                        focusRequester = if (index == 0) firstFocus else null
                    ) { onSelect(entry.first) }
                } else {
                    SidebarItem(
                        label = entry.first,
                        icon = entry.third,
                        selected = section == entry.first,
                        showLabel = true,
                        focusRequester = if (index == 0) firstFocus else null
                    ) { onSelect(entry.first) }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (compact) {
            RailNavigationItem("Ajustes", Icons.Default.Settings, section == "Configurações") {
                onSelect("Configurações")
            }
        } else {
            SidebarItem("Configurações", Icons.Default.Settings, section == "Configurações", true) {
                onSelect("Configurações")
            }
            Spacer(Modifier.height(8.dp))
            Text("NTPLAYTREE • ALPHA 21", color = Muted, fontSize = 8.sp, letterSpacing = .8.sp, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun RailNavigationItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth().height(50.dp)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .clip(RoundedCornerShape(13.dp))
            .background(
                if (selected || focused) Brush.horizontalGradient(listOf(Blue.copy(alpha = .28f), Cyan.copy(alpha = .14f)))
                else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .border(
                if (focused) 2.dp else if (selected) 1.dp else 1.dp,
                if (focused) Blue else if (selected) Blue.copy(alpha = .85f) else Color.Transparent,
                RoundedCornerShape(13.dp)
            )
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = if (selected || focused) White else Muted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(3.dp))
        Text(label, color = if (selected || focused) White else Muted, fontSize = 9.sp, fontWeight = if (selected || focused) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun SidebarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    showLabel: Boolean = true,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().height(if (showLabel) 46.dp else 48.dp)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .clip(RoundedCornerShape(13.dp))
            .background(
                if (selected || focused) Brush.horizontalGradient(
                    listOf(Blue.copy(alpha = .22f), AccentPink.copy(alpha = .14f))
                ) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .border(
                if (focused) 2.dp else if (selected) 1.dp else 1.dp,
                if (focused) Blue else if (selected) Blue.copy(alpha = .65f) else Color.Transparent,
                RoundedCornerShape(13.dp)
            )
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(horizontal = if (showLabel) 9.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (showLabel) Arrangement.Start else Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = if (selected || focused) Blue else Muted, modifier = Modifier.size(18.dp))
        if (showLabel) {
            Spacer(Modifier.width(9.dp))
            Text(label, color = if (selected || focused) White else Muted, fontSize = 12.sp, fontWeight = if (selected || focused) FontWeight.Bold else FontWeight.Medium)
            if (selected) {
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(Blue))
            }
        }
    }
}

private fun compactStatusLabel(status: String): String = when {
    Regex("""HTTP\s*522""", RegexOption.IGNORE_CASE).containsMatchIn(status) -> "SERVIDOR 522"
    Regex("""HTTP\s*5\d\d""", RegexOption.IGNORE_CASE).containsMatchIn(status) -> "ERRO DO SERVIDOR"
    status.contains("dns", ignoreCase = true) -> "FALHA DE DNS"
    status.contains("carregando", ignoreCase = true) || status.contains("conectando", ignoreCase = true) -> "CONECTANDO"
    status.contains("itens carregados", ignoreCase = true) || status.contains("biblioteca atualizada", ignoreCase = true) -> "BIBLIOTECA PRONTA"
    status.isBlank() -> "PRONTO"
    else -> status.uppercase(Locale.ROOT).take(26)
}

@Composable
private fun WideHeader(section: String, status: String) {
    val failed = status.contains("falha", true) || status.contains("erro", true) || status.contains("failed", true) || status.contains("unable", true) || status.contains("dns", true) || status.contains("http", true) || status.contains("tempo esgotado", true)
    val warning = status.contains("aviso", true) || status.contains("limite", true) || status.contains("parcial", true)
    val busy = status.contains("carregando", true)
    val verified = status.contains("conexão verificada", true) || status.contains("reprodução verificada", true)
    val statusColor = when {
        failed -> AccentPink
        warning -> Amber
        busy -> Blue
        verified -> Color(0xFF8CE6B0)
        else -> Muted
    }
    Row(Modifier.fillMaxWidth().height(44.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ntplaytree_logo),
            contentDescription = "NTPlaytree",
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(9.dp))
        Text("NTPlaytree", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(12.dp))
        Box(Modifier.width(1.dp).height(20.dp).background(Color.White.copy(alpha = .12f)))
        Spacer(Modifier.width(12.dp))
        Text(section.uppercase(Locale.ROOT), color = Blue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, maxLines = 1)
        Spacer(Modifier.weight(1f))
        Surface(
            color = Panel,
            shape = RoundedCornerShape(50),
            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = .34f))
        ) {
            Row(Modifier.widthIn(max = 360.dp).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(statusColor))
                Spacer(Modifier.width(8.dp))
                Text(compactStatusLabel(status), color = statusColor, fontSize = 8.sp, letterSpacing = .55.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun Header(status: String) {
    val failed = status.contains("falha", ignoreCase = true) || status.contains("erro", ignoreCase = true) || status.contains("failed", ignoreCase = true) || status.contains("unable", ignoreCase = true) || status.contains("dns", ignoreCase = true) || status.contains("http", ignoreCase = true) || status.contains("tempo esgotado", ignoreCase = true)
    val warning = status.contains("aviso", ignoreCase = true) || status.contains("limite", ignoreCase = true) || status.contains("parcial", ignoreCase = true)
    val busy = status.contains("carregando", ignoreCase = true)
    val verified = status.contains("conexão verificada", ignoreCase = true) || status.contains("reprodução verificada", ignoreCase = true)
    val statusColor = when { failed -> AccentPink; warning -> Amber; busy -> Blue; verified -> Color(0xFF8CE6B0); else -> Muted }
    Row(Modifier.fillMaxWidth().height(42.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ntplaytree_logo),
            contentDescription = "Logo NTPlaytree",
            modifier = Modifier.size(34.dp)
        )
        Spacer(Modifier.width(9.dp))
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("NT", color = Blue, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Playtree", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text("CINEMA • TV • SÉRIES", color = Muted, fontSize = 7.sp, letterSpacing = 1.2.sp)
        }
        Spacer(Modifier.weight(1f))
        Surface(
            color = Panel,
            shape = RoundedCornerShape(50),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                statusColor.copy(alpha = .34f)
            )
        ) {
            Row(
                Modifier.widthIn(max = 330.dp).padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(7.dp).clip(RoundedCornerShape(50))
                        .background(statusColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    compactStatusLabel(status),
                    color = statusColor,
                    fontSize = 8.sp,
                    letterSpacing = .55.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TopNavigationBar(section: String, onSelect: (String) -> Unit) {
    val initialFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(300)
        runCatching { initialFocus.requestFocus() }
    }
    val entries = listOf(
        "Início" to Icons.Default.Home,
        "TV ao vivo" to Icons.Default.LiveTv,
        "Filmes" to Icons.Default.Movie,
        "Séries" to Icons.Default.VideoLibrary,
        "Favoritos" to Icons.Default.FavoriteBorder,
        "Guia" to Icons.Default.LiveTv,
        "Histórico" to Icons.Default.Refresh,
        "Listas" to Icons.Default.FolderOpen,
        "Enviar do celular" to Icons.Default.FolderOpen,
        "Configurações" to Icons.Default.Settings
    )
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        entries.forEach { (label, icon) ->
            var focused by remember { mutableStateOf(false) }
            val selected = section == label
            val focusModifier = if (label == "Início") Modifier.focusRequester(initialFocus) else Modifier
            Row(
                Modifier.widthIn(min = 84.dp).height(42.dp)
                    .then(focusModifier)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected || focused) Brush.horizontalGradient(
                            listOf(Blue.copy(alpha = .22f), AccentPink.copy(alpha = .13f))
                        ) else Brush.horizontalGradient(listOf(Panel, Panel))
                    )
                    .border(
                        if (focused) 2.dp else 1.dp,
                        if (focused) Blue else if (selected) Blue.copy(alpha = .65f) else Color.White.copy(alpha = .07f),
                        RoundedCornerShape(14.dp)
                    )
                    .onFocusChanged { focused = it.isFocused }
                    .clickable { onSelect(label) }
                    .padding(horizontal = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (selected || focused) Blue else Muted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    color = if (selected || focused) White else Muted,
                    fontWeight = if (selected || focused) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun HomeSection(
    channelCount: Int,
    sourceCount: Int,
    loading: Boolean,
    status: String,
    onOpenLive: () -> Unit,
    onOpenMovies: () -> Unit,
    onOpenSeries: () -> Unit,
    onAddList: () -> Unit,
    onTransfer: () -> Unit,
    recent: List<Channel>,
    onPlay: (Channel) -> Unit
) {
    val hero = recent.firstOrNull()
    val recentMovies = recent.filter { it.kind == "movie" || it.kind == "series" }
    val recentLive = recent.filter { it.kind == "live" }
    val compactLayout = LocalConfiguration.current.screenWidthDp < 1050

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("SUA CENTRAL DE ENTRETENIMENTO", color = Blue, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
                Text("O que vamos assistir?", color = White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            Surface(color = Panel, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Amber.copy(alpha = .35f))) {
                Column(Modifier.padding(horizontal = 13.dp, vertical = 7.dp), horizontalAlignment = Alignment.End) {
                    Text("BIBLIOTECA", color = Muted, fontSize = 8.sp, letterSpacing = 1.sp)
                    Text("${channelCount} itens", color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (loading) {
                Spacer(Modifier.width(10.dp))
                CircularProgressIndicator(color = Blue, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }

        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.fillMaxWidth().height(if (compactLayout) 116.dp else 132.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF8A4B32), Color(0xFF59406F), Color(0xFF246A68))))
                .border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(18.dp))
        ) {
            if (hero?.logo != null) {
                RemoteArtwork(
                    url = hero.logo,
                    modifier = Modifier.align(Alignment.CenterEnd).width(if (compactLayout) 190.dp else 270.dp).fillMaxHeight(),
                    fallback = {
                        Image(painter = painterResource(R.drawable.ntplaytree_logo), contentDescription = null, modifier = Modifier.fillMaxSize().padding(42.dp), contentScale = ContentScale.Fit)
                    },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ntplaytree_logo),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd).size(150.dp).padding(18.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF101820), Color(0xF2101820), Color(0xAA101820), Color.Transparent))))
            Column(
                Modifier.fillMaxHeight().widthIn(max = 610.dp).padding(start = 20.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(if (hero != null) "CONTINUE DE ONDE PAROU" else "BEM-VINDO AO NTPLAYTREE", color = Blue, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Spacer(Modifier.height(5.dp))
                Text(hero?.name ?: "Seu próximo entretenimento começa aqui", color = White, fontSize = if (compactLayout) 17.sp else 19.sp, lineHeight = if (compactLayout) 20.sp else 23.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(hero?.group?.takeIf { it.isNotBlank() } ?: "TV ao vivo, filmes e séries em um só lugar.", color = Color(0xFFD8D4E8), fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (hero != null) SmallAction("CONTINUAR", Icons.Default.PlayArrow) { onPlay(hero) }
                    else SmallAction("TV AO VIVO", Icons.Default.LiveTv, onOpenLive)
                    SmallAction("FILMES", Icons.Default.Movie, onOpenMovies)
                    SmallAction("SÉRIES", Icons.Default.VideoLibrary, onOpenSeries)
                }
            }
            Surface(Modifier.align(Alignment.BottomEnd).padding(10.dp), color = Color(0xCC07111C), shape = RoundedCornerShape(50), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .10f))) {
                Text("$sourceCount ${if (sourceCount == 1) "lista" else "listas"}", color = Color(0xFFE4DFF5), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Explore", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Acesse sua programação", color = Muted, fontSize = 10.sp)
            }
            SmallAction("ADICIONAR LISTA", Icons.Default.FolderOpen, onAddList)
            Spacer(Modifier.width(7.dp))
            SmallAction("QR CODE", Icons.Default.FolderOpen, onTransfer)
        }
        Spacer(Modifier.height(7.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureCard("TV AO VIVO", "Canais e programação", Icons.Default.LiveTv, onOpenLive)
            FeatureCard("FILMES", "Sua coleção de filmes", Icons.Default.Movie, onOpenMovies)
            FeatureCard("SÉRIES", "Temporadas e episódios", Icons.Default.VideoLibrary, onOpenSeries)
        }

        if (recentMovies.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("Continue assistindo", color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(9.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                items(recentMovies, key = { it.id }) { item -> Box(Modifier.width(126.dp)) { PosterCard(channel = item, onClick = { onPlay(item) }) } }
            }
        }
        if (recentLive.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text("Seus canais recentes", color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(9.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                items(recentLive, key = { it.id }) { item -> CompactChannelCard(channel = item, onClick = { onPlay(item) }) }
            }
        }
        if (recent.isEmpty()) {
            Spacer(Modifier.height(18.dp))
            EmptyState(status, onAddList)
        }
    }
}


@Composable
private fun TransferSection(onShowQr: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Enviar lista pelo celular", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("Transfira uma lista M3U do celular para a televisão sem digitar endereços longos usando o controle remoto.", color = Muted, fontSize = 14.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Como funciona", color = Blue, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("""1. A TV cria uma conexão temporária na sua rede local.
2. Escaneie o QR Code com a câmera do celular.
3. Escolha um arquivo M3U ou informe o endereço da lista.
4. A lista será adicionada automaticamente à biblioteca da TV.""", color = White, fontSize = 14.sp, lineHeight = 22.sp)
                Text("O celular e a TV precisam estar na mesma rede Wi-Fi. A conexão é temporária e funciona somente enquanto esta tela estiver aberta.", color = Muted, fontSize = 12.sp)
                SmallAction("Mostrar QR Code", Icons.Default.FolderOpen, onShowQr)
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, detail: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.width(184.dp).height(96.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.fillMaxSize().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
                Text(value, color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(detail, color = Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Column(
        Modifier.width(148.dp).height(84.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(
                Brush.linearGradient(
                    if (focused) listOf(Color(0xFFB85B38), Color(0xFF674A83))
                    else listOf(Color(0xFF1D2930), Color(0xFF292332))
                )
            )
            .border(if (focused) 2.dp else 1.dp, if (focused) Amber else Color.White.copy(alpha = .09f), RoundedCornerShape(17.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(11.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(Modifier.size(30.dp).clip(RoundedCornerShape(10.dp)).background(Cyan.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Cyan, modifier = Modifier.size(19.dp))
        }
        Text(title, color = White, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = .3.sp, maxLines = 1)
        Text(subtitle, color = Muted, fontSize = 7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, lineHeight = 10.sp)
    }
}


@Composable
private fun LibrarySection(
    title: String,
    channels: List<Channel>,
    favorites: Set<String>,
    epgPrograms: List<EpgProgram>,
    search: String,
    onSearch: (String) -> Unit,
    selectedGroup: String,
    onGroupSelected: (String) -> Unit,
    loading: Boolean,
    status: String,
    onPlay: (Channel) -> Unit,
    onFavorite: (Channel) -> Unit,
    onAddList: () -> Unit,
    onTransfer: () -> Unit
) {
    val programsByChannel = remember(epgPrograms) {
        epgPrograms.groupBy { it.channelId.lowercase(Locale.ROOT) }
            .mapValues { (_, items) -> items.sortedBy { it.startMs } }
    }
    val groups = remember(channels) {
        listOf("Todas") + channels.map { it.group }
            .filter { it.isNotBlank() }
            .distinctBy { normalizeSearchText(it) }
            .sortedBy { normalizeSearchText(it) }
    }
    val sortedChannels = remember(channels) {
        channels.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }
    val normalizedSearch = remember(search) { normalizeSearchText(search) }
    val visible = remember(sortedChannels, selectedGroup, normalizedSearch) {
        sortedChannels.filter { channel ->
            val matchesGroup = selectedGroup == "Todas" || channel.group == selectedGroup
            val matchesSearch = normalizedSearch.isBlank() ||
                normalizeSearchText(channel.name).contains(normalizedSearch) ||
                normalizeSearchText(channel.group).contains(normalizedSearch)
            matchesGroup && matchesSearch
        }
    }
    val posterMode = title == "Filmes" || title == "Séries"
    val isLive = title == "TV ao vivo"
    val firstContentFocus = remember(title) { FocusRequester() }

    LaunchedEffect(title, channels.size) {
        if (channels.isNotEmpty()) {
            delay(260)
            runCatching { firstContentFocus.requestFocus() }
        }
    }

    Column(Modifier.fillMaxSize().focusGroup()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    when (title) {
                        "TV ao vivo" -> "ASSISTA EM TEMPO REAL"
                        "Filmes" -> "SUA PRÓXIMA SESSÃO"
                        "Séries" -> "MARATONE SUAS FAVORITAS"
                        else -> "SUA SELEÇÃO"
                    },
                    color = Blue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(title, color = White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(
                    when (title) {
                        "TV ao vivo" -> "Busque por emissora ou filtre por categoria"
                        "Filmes" -> "Pôsteres em destaque para navegar pela sua coleção"
                        "Séries" -> "Escolha uma série, temporada e episódio"
                        else -> "Acesse rapidamente os conteúdos que você salvou"
                    },
                    color = Muted,
                    fontSize = 11.sp
                )
            }
            if (loading) CircularProgressIndicator(color = Blue, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            Surface(color = PanelRaised, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Blue.copy(alpha = .22f))) {
                Text("${visible.size} itens", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
            }
            Spacer(Modifier.width(8.dp))
            SmallAction("QR", Icons.Default.FolderOpen, onTransfer)
        }

        Spacer(Modifier.height(5.dp))
        BasicTextField(
            value = search,
            onValueChange = onSearch,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 12.sp),
            decorationBox = { inner ->
                Row(
                    Modifier.fillMaxWidth().height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Panel)
                        .border(1.dp, Blue.copy(alpha = .34f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Blue, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(9.dp))
                    Box(Modifier.weight(1f)) {
                        if (search.isBlank()) Text(if (isLive) "Buscar canal, emissora ou categoria..." else "Buscar por título ou categoria...", color = Muted, fontSize = 12.sp)
                        inner()
                    }
                    if (search.isNotBlank()) {
                        Text("Limpar", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onSearch("") }.padding(start = 10.dp))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().focusable()
        )

        if (groups.size > 1) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("CATEGORIAS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Spacer(Modifier.width(9.dp))
                LazyRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp), contentPadding = PaddingValues(vertical = 1.dp)) {
                    items(groups) { group ->
                        CategoryPill(label = group, selected = selectedGroup == group) { onGroupSelected(group) }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        if (visible.isEmpty()) {
            EmptyState(
                when {
                    loading -> "Carregando conteúdo..."
                    channels.isEmpty() -> status
                    title == "Filmes" || title == "Séries" -> "Não encontrei conteúdos nesta categoria. A identificação depende dos grupos e nomes informados pela lista."
                    else -> "Nenhum canal corresponde à busca ou categoria selecionada."
                },
                onAddList
            )
        } else if (isLive) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 172.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 1.dp, end = 4.dp, top = 3.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gridItems(visible, key = { it.id }) { channel ->
                    val matchingPrograms = programsByChannel[epgKey(channel)].orEmpty()
                    val now = System.currentTimeMillis()
                    val currentProgram = matchingPrograms.firstOrNull { it.startMs <= now && it.endMs > now }
                    val nextProgram = matchingPrograms.firstOrNull { it.startMs >= now }
                    LiveChannelCard(
                        channel = channel,
                        isFavorite = channel.url in favorites,
                        currentProgram = currentProgram,
                        nextProgram = nextProgram,
                        onPlay = { onPlay(channel) },
                        onFavorite = { onFavorite(channel) },
                        focusRequester = if (channel.id == visible.firstOrNull()?.id) firstContentFocus else null
                    )
                }
            }
        } else if (posterMode) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 112.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 2.dp, end = 4.dp, top = 3.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gridItems(visible, key = { it.id }) { item ->
                    PosterCard(
                        channel = item,
                        onClick = { onPlay(item) },
                        focusRequester = if (item.id == visible.firstOrNull()?.id) firstContentFocus else null
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                items(visible, key = { it.id }) { channel ->
                    val matchingPrograms = programsByChannel[epgKey(channel)].orEmpty()
                    val now = System.currentTimeMillis()
                    val currentProgram = matchingPrograms.firstOrNull { it.startMs <= now && it.endMs > now }
                    val nextProgram = matchingPrograms.firstOrNull { it.startMs >= now }
                    ChannelRow(
                        channel = channel,
                        isFavorite = channel.url in favorites,
                        currentProgram = currentProgram,
                        nextProgram = nextProgram,
                        onPlay = { onPlay(channel) },
                        onFavorite = { onFavorite(channel) },
                        focusRequester = if (channel.id == visible.firstOrNull()?.id) firstContentFocus else null
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(label: String, selected: Boolean, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Box(
        Modifier.clip(RoundedCornerShape(50))
            .background(if (selected || focused) Brush.horizontalGradient(listOf(Blue.copy(alpha = .24f), Cyan.copy(alpha = .16f))) else Brush.horizontalGradient(listOf(Panel, Panel)))
            .border(if (focused) 2.dp else 1.dp, if (focused) Blue else if (selected) Blue.copy(alpha = .75f) else Color.White.copy(alpha = .09f), RoundedCornerShape(50))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected || focused) White else Muted, fontSize = 10.sp, fontWeight = if (selected || focused) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
    }
}


@Composable
private fun LiveChannelCard(
    channel: Channel,
    isFavorite: Boolean,
    currentProgram: EpgProgram?,
    nextProgram: EpgProgram?,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    focusRequester: FocusRequester? = null
) {
    var focused by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().height(78.dp)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .clip(RoundedCornerShape(18.dp))
            .background(if (focused) Color(0xFF3A2A27) else Panel)
            .border(if (focused) 2.dp else 1.dp, if (focused) Blue else Color.White.copy(alpha = .065f), RoundedCornerShape(18.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onPlay() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(13.dp)).background(Color(0xFF28333B)), contentAlignment = Alignment.Center) {
            RemoteArtwork(channel.logo, Modifier.fillMaxSize()) {
                Icon(Icons.Default.LiveTv, contentDescription = null, tint = Blue, modifier = Modifier.size(34.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(channel.name, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(channel.group, color = Muted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                currentProgram?.let { "AGORA  •  ${it.title}" } ?: nextProgram?.let { "A SEGUIR  •  ${formatClock(it.startMs)}" } ?: "TRANSMISSÃO AO VIVO",
                color = Blue,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
            IconButton(onClick = onFavorite, enabled = channel.url.isNotBlank(), modifier = Modifier.size(26.dp)) {
                Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos", tint = if (isFavorite) AccentPink else Muted, modifier = Modifier.size(16.dp))
            }
            Box(Modifier.size(28.dp).clip(RoundedCornerShape(50)).background(Blue.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Reproduzir", tint = Blue, modifier = Modifier.size(21.dp))
            }
        }
    }
}


@Composable
private fun PosterCard(channel: Channel, onClick: () -> Unit, focusRequester: FocusRequester? = null) {
    var focused by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth()
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .clip(RoundedCornerShape(15.dp))
            .background(Panel)
            .border(if (focused) 2.dp else 1.dp, if (focused) Blue else Color.White.copy(alpha = .07f), RoundedCornerShape(15.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(3.dp)
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(0.76f).clip(RoundedCornerShape(11.dp)).background(PanelRaised)) {
            RemoteArtwork(
                url = channel.logo,
                modifier = Modifier.fillMaxSize(),
                fallback = {
                    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF8A4A35), Color(0xFF5B3D72), Color(0xFF356D69)))), contentAlignment = Alignment.Center) {
                        Icon(if (channel.kind == "series") Icons.Default.VideoLibrary else Icons.Default.Movie, contentDescription = null, tint = Blue, modifier = Modifier.size(30.dp))
                    }
                },
                contentScale = ContentScale.Crop
            )
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(58.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xEE07101F)))))
            if (channel.year != null) {
                Surface(Modifier.align(Alignment.TopStart).padding(7.dp), color = Color(0xCC101B30), shape = RoundedCornerShape(6.dp)) {
                    Text(channel.year, color = White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
            if (focused) {
                Box(Modifier.align(Alignment.Center).size(48.dp).clip(RoundedCornerShape(50)).background(Blue), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Abrir", tint = Navy, modifier = Modifier.size(30.dp))
                }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(channel.name, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp))
        Text(channel.group.ifBlank { if (channel.kind == "series") "Série" else "Filme" }, color = Muted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
    }
}


@Composable
private fun RemoteArtwork(url: String?, modifier: Modifier = Modifier, fallback: @Composable () -> Unit) {
    RemoteArtwork(url, modifier, fallback, ContentScale.Crop)
}

@Composable
private fun RemoteArtwork(
    url: String?,
    modifier: Modifier,
    fallback: @Composable () -> Unit,
    contentScale: ContentScale
) {
    var bitmap by remember(url) { mutableStateOf(url?.let { artworkCache.get(it) }) }
    LaunchedEffect(url) {
        if (bitmap == null && !url.isNullOrBlank()) {
            val loaded = withContext(Dispatchers.IO) {
                runCatching {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.connectTimeout = 7_000
                    connection.readTimeout = 10_000
                    connection.instanceFollowRedirects = true
                    connection.setRequestProperty("User-Agent", "NTPlaytree/1.4 AndroidTV")
                    connection.setRequestProperty("Accept", "image/*,*/*")
                    try {
                        BufferedInputStream(connection.inputStream, 64 * 1024).use { input ->
                            input.mark(1024 * 1024)
                            val bounds = Options().apply { inJustDecodeBounds = true }
                            BitmapFactory.decodeStream(input, null, bounds)
                            input.reset()

                            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                                null
                            } else {
                                var sampleSize = 1
                                while (
                                    bounds.outWidth / sampleSize > 640 ||
                                    bounds.outHeight / sampleSize > 640
                                ) {
                                    sampleSize *= 2
                                }
                                val options = Options().apply {
                                    inSampleSize = sampleSize
                                    inPreferredConfig = Config.RGB_565
                                    inDither = true
                                }
                                BitmapFactory.decodeStream(input, null, options)
                            }
                        }
                    } finally {
                        connection.disconnect()
                    }
                }.getOrNull()
            }
            if (loaded != null) {
                artworkCache.put(url!!, loaded)
                bitmap = loaded
            }
        }
    }
    if (bitmap != null) {
        Image(bitmap!!.asImageBitmap(), contentDescription = null, modifier = modifier, contentScale = contentScale)
    } else {
        Box(modifier) { fallback() }
    }
}

@Composable
private fun ChannelRow(
    channel: Channel,
    isFavorite: Boolean,
    currentProgram: EpgProgram?,
    nextProgram: EpgProgram?,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    focusRequester: FocusRequester? = null
) {
    var focused by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier).clip(RoundedCornerShape(14.dp))
            .background(if (focused) PanelRaised else Panel)
            .border(if (focused) 2.dp else 1.dp, if (focused) Blue else Color.White.copy(alpha = .055f), RoundedCornerShape(14.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onPlay() }
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(62.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFF28333B))) {
            RemoteArtwork(channel.logo, Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = Blue, modifier = Modifier.size(27.dp))
                }
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(channel.name, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(channel.group, color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            currentProgram?.let { Text("AGORA • ${it.title}", color = Blue, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            nextProgram?.let { Text("A SEGUIR • ${formatClock(it.startMs)} • ${it.title}", color = Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
        IconButton(onClick = onFavorite, enabled = channel.url.isNotBlank()) {
            Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos", tint = if (isFavorite) AccentPink else Muted)
        }
        Icon(Icons.Default.PlayArrow, contentDescription = "Reproduzir", tint = Blue, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun CompactChannelCard(channel: Channel, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Column(
        Modifier.width(164.dp).height(122.dp).clip(RoundedCornerShape(13.dp)).background(Panel)
            .border(if (focused) 2.dp else 1.dp, if (focused) Blue else Color.White.copy(alpha = .07f), RoundedCornerShape(13.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }.padding(11.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(9.dp)).background(PanelRaised)) {
                RemoteArtwork(channel.logo, Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.LiveTv, contentDescription = null, tint = Blue) }
                }
            }
            Spacer(Modifier.width(9.dp))
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Blue, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(channel.name, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(channel.group, color = Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun PlaylistSection(
    sources: List<PlaylistSource>,
    channels: List<Channel>,
    loading: Boolean,
    progress: String,
    status: String,
    onAdd: () -> Unit,
    onRefresh: () -> Unit,
    onRemove: (PlaylistSource) -> Unit,
    onTransfer: () -> Unit
) {
    val wide = LocalConfiguration.current.screenWidthDp >= 850
    val is522 = Regex("""HTTP\s*522""", RegexOption.IGNORE_CASE).containsMatchIn(status)
    val failed = is522 || listOf("falha", "não foi possível", "erro", "recusada", "dns", "http", "tempo esgotado")
        .any { status.contains(it, ignoreCase = true) }
    val statusTint = when {
        loading -> Cyan
        failed -> ErrorCoral
        channels.isNotEmpty() -> Cyan
        else -> Muted
    }
    val stateLabel = when {
        loading -> "CONECTANDO"
        is522 -> "ORIGEM SEM RESPOSTA"
        failed -> "CONEXÃO COM ERRO"
        channels.isNotEmpty() -> "BIBLIOTECA PRONTA"
        else -> "SEM CONTEÚDO"
    }

    Column(Modifier.fillMaxSize().padding(bottom = 5.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("NTPLAYTREE  /  BIBLIOTECA", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.7.sp)
                Text("Fontes e conexões", color = White, fontSize = 26.sp, fontWeight = FontWeight.Black)
                Text("Controle suas listas e veja o diagnóstico de cada servidor", color = Muted, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!loading) SmallAction("ATUALIZAR", Icons.Default.Refresh, onRefresh)
                SmallAction("QR CODE", Icons.Default.FolderOpen, onTransfer)
                SmallAction("NOVA LISTA", Icons.Default.FolderOpen, onAdd)
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PlaylistMetric("FONTES", sources.size.toString(), "cadastradas", Icons.Default.FolderOpen, Modifier.weight(1f))
            PlaylistMetric("ITENS", channels.size.toString(), "na biblioteca", Icons.Default.VideoLibrary, Modifier.weight(1f))
            PlaylistMetric("SITUAÇÃO", stateLabel, if (is522) "HTTP 522" else if (loading) "tentando conexão" else "estado atual", Icons.Default.Refresh, Modifier.weight(1f))
        }

        Spacer(Modifier.height(10.dp))
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(if (wide) 1.42f else 1f).fillMaxHeight()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Panel,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusTint.copy(alpha = .48f))
                ) {
                    Row(
                        Modifier.fillMaxWidth().background(
                            Brush.horizontalGradient(
                                if (is522) listOf(Color(0xFF321D27), Color(0xFF1A202A), Panel)
                                else listOf(Color(0xFF26352D), Color(0xFF19252C), Panel)
                            )
                        ).padding(horizontal = 15.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(statusTint.copy(alpha = .14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (loading) CircularProgressIndicator(color = statusTint, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            else Icon(Icons.Default.Refresh, contentDescription = null, tint = statusTint, modifier = Modifier.size(23.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                when {
                                    loading -> "TENTANDO ATUALIZAR"
                                    is522 -> "O SERVIDOR DE ORIGEM NÃO RESPONDEU"
                                    failed -> "NÃO FOI POSSÍVEL ATUALIZAR"
                                    channels.isNotEmpty() -> "CONTEÚDO DISPONÍVEL"
                                    else -> "AGUARDANDO UMA LISTA"
                                },
                                color = statusTint, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = .9.sp
                            )
                            Text(
                                when {
                                    loading -> progress.ifBlank { "Conectando às fontes cadastradas..." }
                                    is522 -> "HTTP 522: o servidor intermediário não conseguiu conectar ao servidor de origem dentro do prazo."
                                    else -> status
                                },
                                color = White, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 3, overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (!loading && sources.isNotEmpty()) {
                            Spacer(Modifier.width(9.dp))
                            SmallAction("TENTAR", Icons.Default.Refresh, onRefresh)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("SUAS FONTES", color = White, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = PanelRaised, shape = RoundedCornerShape(50)) {
                        Text(sources.size.toString(), color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp))
                    }
                    Spacer(Modifier.weight(1f))
                    Text("${channels.size} itens", color = Muted, fontSize = 9.sp)
                }
                Spacer(Modifier.height(7.dp))
                if (sources.isEmpty()) {
                    EmptyState("Sua biblioteca ainda não tem uma fonte. Adicione uma lista M3U, Xtream Codes ou arquivo local.", onAdd)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(sources, key = { it.id }) { source ->
                            val sourceCount = channels.count { it.sourceId == source.id }
                            val host = if (source.isLocalFile) "Arquivo local" else runCatching { Uri.parse(source.location).host }.getOrNull().orEmpty().ifBlank { source.location }
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                                    .background(Brush.horizontalGradient(listOf(Color(0xFF202A32), Color(0xFF17212A))))
                                    .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.width(4.dp).height(43.dp).clip(RoundedCornerShape(4.dp)).background(if (sourceCount > 0) Cyan else ErrorCoral))
                                Spacer(Modifier.width(11.dp))
                                Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(Blue.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
                                    Icon(if (source.isLocalFile) Icons.Default.FolderOpen else Icons.Default.LiveTv, contentDescription = null, tint = Blue, modifier = Modifier.size(21.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(source.name, color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(
                                        (if (source.type == "xtream") "XTREAM" else "M3U") + "  ·  " + host,
                                        color = Muted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(if (sourceCount > 0) "$sourceCount" else "0", color = if (sourceCount > 0) Cyan else ErrorCoral, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                    Text(if (sourceCount > 0) "ITENS" else "SEM CATÁLOGO", color = Muted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { onRemove(source) }, modifier = Modifier.size(34.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remover lista", tint = ErrorCoral, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            if (wide) {
                Surface(
                    modifier = Modifier.weight(.82f).fillMaxHeight(),
                    color = Panel,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .08f))
                ) {
                    Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("DIAGNÓSTICO", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Text(if (is522) "HTTP 522" else if (failed) "Falha na conexão" else if (loading) "Conexão em andamento" else "Estado da biblioteca", color = White, fontSize = 19.sp, fontWeight = FontWeight.Black)
                        Text(
                            when {
                                is522 -> "O aplicativo recebeu uma resposta HTTP 522. Isso indica que o proxy ou CDN não conseguiu abrir conexão com o servidor de origem a tempo. A lista não chegou ao aparelho."
                                failed -> status
                                loading -> "O aplicativo está consultando a fonte cadastrada. O resultado será exibido aqui quando a tentativa terminar."
                                channels.isNotEmpty() -> "A biblioteca contém itens carregados e prontos para navegação."
                                else -> "Adicione uma fonte para iniciar a biblioteca."
                            },
                            color = Muted, fontSize = 11.sp, lineHeight = 16.sp
                        )
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = .08f)))
                        Text("PRÓXIMA AÇÃO", color = Blue, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                        Text(
                            if (is522) "Peça ao provedor para verificar o servidor de origem, o firewall, a porta HTTP/HTTPS e a rota entre a CDN e o host."
                            else if (failed) "Confira o endereço da lista, a disponibilidade do servidor e as credenciais fornecidas."
                            else "Mantenha sua fonte atualizada. Se o catálogo não carregar, consulte o diagnóstico desta tela.",
                            color = White, fontSize = 11.sp, lineHeight = 16.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Surface(color = Color(0xFF252E32), shape = RoundedCornerShape(12.dp)) {
                            Text(
                                if (is522) "Trocar Media3 por VLC não corrige HTTP 522. O erro ocorre antes da reprodução."
                                else "A reprodução só começa depois que a lista entrega endereços válidos.",
                                color = if (is522) Amber else Muted, fontSize = 10.sp, lineHeight = 14.sp, modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistMetric(
    label: String,
    value: String,
    detail: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(56.dp),
        color = Panel,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = .07f))
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 11.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(label, color = Muted, fontSize = 7.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp, maxLines = 1)
                Text(value, color = White, fontSize = 12.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(detail, color = Muted, fontSize = 7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun EpgSection(
    channels: List<Channel>,
    programs: List<EpgProgram>,
    status: String,
    onPlay: (Channel) -> Unit
) {
    val programsByChannel = remember(programs) {
        programs.groupBy { it.channelId.lowercase() }.mapValues { (_, items) -> items.sortedBy { it.startMs } }
    }
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Guia de programação", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(status, color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.height(12.dp))
        if (channels.isEmpty()) {
            Text("Adicione uma lista com canais ao vivo para consultar a programação.", color = Muted, fontSize = 14.sp)
        } else if (programs.isEmpty()) {
            Text("Nenhum programa XMLTV disponível. O guia depende de um endereço url-tvg na M3U ou xmltv.php no servidor Xtream.", color = Muted, fontSize = 14.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(channels, key = { "epg-${it.id}" }) { channel ->
                    val matching = programsByChannel[epgKey(channel)].orEmpty()
                    val now = System.currentTimeMillis()
                    val current = matching.firstOrNull { it.startMs <= now && it.endMs > now }
                    val next = matching.firstOrNull { it.startMs >= now }
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(Panel)
                            .border(1.dp, Color.White.copy(alpha = .05f), RoundedCornerShape(11.dp))
                            .clickable { onPlay(channel) }.focusable().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(channel.name, color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                current?.let { "AGORA  ${formatClock(it.startMs)}–${formatClock(it.endMs)}  ${it.title}" } ?: "Sem informação do programa atual",
                                color = if (current != null) Blue else Muted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                next?.let { "A SEGUIR  ${formatClock(it.startMs)}  ${it.title}" } ?: "Próximo programa não informado",
                                color = Muted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(Icons.Default.PlayArrow, contentDescription = "Assistir ${channel.name}", tint = Blue, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySection(
    history: List<PlaybackHistory>,
    onPlay: (Channel) -> Unit,
    onClear: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Histórico de reprodução", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (history.isNotEmpty()) SmallAction("Limpar", Icons.Default.Delete, onClear)
        }
        Spacer(Modifier.height(8.dp))
        Text("Os últimos conteúdos reproduzidos e a posição de retomada ficam neste dispositivo.", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        if (history.isEmpty()) {
            Text("Você ainda não reproduziu nenhum conteúdo.", color = Muted, fontSize = 14.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(history, key = { "history-${it.channel.id}" }) { entry ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(Panel)
                            .border(1.dp, Color.White.copy(alpha = .05f), RoundedCornerShape(11.dp))
                            .clickable { onPlay(entry.channel) }.focusable().padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Blue, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(entry.channel.name, color = White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${entry.channel.group} • ${formatHistoryDate(entry.playedAtMs)}", color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (entry.durationMs > 0L && entry.channel.kind != "live") {
                                Text("Retomar em ${formatDuration(entry.positionMs)} de ${formatDuration(entry.durationMs)}", color = Blue, fontSize = 11.sp)
                            } else {
                                Text("Reproduzido recentemente", color = Muted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeriesEpisodesDialog(
    series: Channel,
    source: PlaylistSource,
    preloadedEpisodes: List<Channel>,
    onDismiss: () -> Unit,
    onPlayEpisode: (Channel) -> Unit
) {
    var episodes by remember(series.id) { mutableStateOf(preloadedEpisodes) }
    var loading by remember(series.id) { mutableStateOf(preloadedEpisodes.isEmpty()) }
    var error by remember(series.id) { mutableStateOf("") }
    var selectedSeason by remember(series.id) { mutableStateOf("1") }
    val firstEpisodeFocus = remember(series.id) { FocusRequester() }

    LaunchedEffect(series.id, preloadedEpisodes) {
        if (preloadedEpisodes.isNotEmpty()) {
            episodes = preloadedEpisodes
            loading = false
        } else if (series.seriesId != null) {
            loading = true
            val result = withContext(Dispatchers.IO) { runCatching { loadXtreamEpisodes(source, series) } }
            result.onSuccess { episodes = it }
                .onFailure { error = describeSourceLoadFailure(it, source.location) }
            loading = false
        } else {
            loading = false
            error = "Esta série não possui episódios identificados na lista."
        }
    }

    val seasons = remember(episodes) {
        episodes.groupBy { episode ->
            episode.seasonNumber
                ?: Regex("""(?i)(?:temporada|season)\s*(\d+)""").find(episode.group)?.groupValues?.getOrNull(1)
                ?: "1"
        }.toSortedMap(compareBy { it.toIntOrNull() ?: Int.MAX_VALUE })
    }
    LaunchedEffect(seasons) {
        if (selectedSeason !in seasons.keys) selectedSeason = seasons.keys.firstOrNull() ?: "1"
    }
    LaunchedEffect(episodes, selectedSeason, loading) {
        if (!loading && seasons[selectedSeason].orEmpty().isNotEmpty()) {
            delay(220)
            runCatching { firstEpisodeFocus.requestFocus() }
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(.82f).widthIn(max = 1120.dp),
            color = Panel,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RemoteArtwork(series.logo, Modifier.size(88.dp).clip(RoundedCornerShape(12.dp))) {
                        Box(Modifier.size(88.dp).clip(RoundedCornerShape(12.dp)).background(PanelRaised), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Blue, modifier = Modifier.size(34.dp))
                        }
                    }
                    Spacer(Modifier.width(15.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(series.name, color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("${episodes.size} episódios • ${seasons.size} temporadas", color = Muted, fontSize = 12.sp)
                        if (!series.description.isNullOrBlank()) Text(series.description, color = Muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    TextButton(onClick = onDismiss) { Text("Fechar", color = Blue) }
                }
                Spacer(Modifier.height(18.dp))
                when {
                    loading -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Blue, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Carregando temporadas e episódios...", color = Muted)
                    }
                    error.isNotBlank() -> Text(error, color = ErrorCoral, fontSize = 13.sp)
                    episodes.isEmpty() -> Text("O servidor não retornou episódios para esta série.", color = Muted, fontSize = 13.sp)
                    else -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(seasons.keys.toList()) { season ->
                                FilterChip(
                                    selected = selectedSeason == season,
                                    onClick = { selectedSeason = season },
                                    label = { Text("Temporada $season") }
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        val seasonEpisodes = seasons[selectedSeason].orEmpty()
                        LazyColumn(Modifier.height(330.dp).focusGroup(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            items(seasonEpisodes, key = { it.id }) { episode ->
                                var focused by remember { mutableStateOf(false) }
                                val firstEpisodeModifier = if (episode.id == seasonEpisodes.firstOrNull()?.id) Modifier.focusRequester(firstEpisodeFocus) else Modifier
                                Row(
                                    Modifier.fillMaxWidth().then(firstEpisodeModifier).clip(RoundedCornerShape(11.dp))
                                        .background(if (focused) PanelRaised else Panel)
                                        .border(if (focused) 2.dp else 1.dp, if (focused) Blue else Color.White.copy(alpha = .05f), RoundedCornerShape(11.dp))
                                        .onFocusChanged { focused = it.isFocused }
                                        .clickable { onPlayEpisode(episode) }
                                        .padding(13.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(9.dp)).background(Brush.linearGradient(listOf(Blue.copy(alpha = .22f), Amber.copy(alpha = .30f)))), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Blue)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(episode.name, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Text("Temporada $selectedSeason${episode.episodeNumber?.let { " • Episódio $it" } ?: ""}", color = Muted, fontSize = 11.sp)
                                    }
                                    Text("ASSISTIR", color = Blue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanTransferDialog(address: String?, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(.72f).widthIn(max = 760.dp),
            color = Panel,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enviar lista para a TV", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Escaneie com a câmera do celular. TV e celular precisam estar na mesma rede Wi-Fi ou Ethernet.", color = Muted, fontSize = 13.sp)
                if (address.isNullOrBlank()) {
                    CircularProgressIndicator(color = Blue, modifier = Modifier.size(48.dp), strokeWidth = 3.dp)
                    Text("Preparando conexão local...", color = Muted, fontSize = 12.sp)
                } else if (address == "ERROR") {
                    Text("Não foi possível identificar um IP privado de Wi-Fi/Ethernet. Conecte a TV à rede local, desative VPN e tente novamente.", color = ErrorCoral, fontSize = 13.sp)
                } else {
                    QrCodeImage(address, Modifier.size(236.dp).clip(RoundedCornerShape(14.dp)).background(Color.White).padding(8.dp))
                    Text(address, color = Blue, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val healthAddress = address.replace("/?token=", "/health?token=")
                    Text("Teste da conexão local: $healthAddress", color = Cyan, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("Se a câmera não abrir o QR, digite o endereço no navegador do celular. A página permite enviar um arquivo M3U ou colar o endereço da lista. Não funciona entre redes diferentes ou com isolamento de dispositivos no roteador.", color = Muted, fontSize = 12.sp)
                }
                TextButton(onClick = onDismiss) { Text("Fechar", color = Blue) }
            }
        }
    }
}

@Composable
private fun QrCodeImage(value: String, modifier: Modifier = Modifier) {
    val bitmap = remember(value) {
        runCatching {
            val matrix = QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, 420, 420)
            Bitmap.createBitmap(matrix.width, matrix.height, Config.ARGB_8888).apply {
                for (x in 0 until matrix.width) {
                    for (y in 0 until matrix.height) {
                        setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }
            }
        }.getOrNull()
    }
    if (bitmap != null) Image(bitmap.asImageBitmap(), contentDescription = "QR Code para transferir lista", modifier = modifier, contentScale = ContentScale.Fit)
}

@Composable
private fun SettingsSection(onClearFavorites: () -> Unit, onClearHistory: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Configurações", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("NTPlaytree • versão 1.1.0-alpha22", color = Muted, fontSize = 14.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("Modo local", color = Blue, fontWeight = FontWeight.Bold)
                Text("O aplicativo não exige conta ou servidor de autenticação. Os dados de listas, favoritos e histórico ficam armazenados neste dispositivo. As credenciais Xtream são usadas somente para consultar o servidor informado por você.", color = White, fontSize = 13.sp)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("EPG e reprodução", color = Blue, fontWeight = FontWeight.Bold)
                Text("O guia lê XMLTV informado na lista M3U ou o xmltv.php do servidor Xtream. A disponibilidade e a correspondência dos programas dependem dos IDs tvg-id/EPG fornecidos pelo servidor.", color = White, fontSize = 13.sp)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("Compatibilidade", color = Blue, fontWeight = FontWeight.Bold)
                Text("O aplicativo usa Media3 como motor principal e LibVLC como alternativa. A reprodução ainda depende de o domínio resolver, o servidor responder e o fluxo ser autorizado e compatível.", color = White, fontSize = 13.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            SmallAction("Limpar favoritos", Icons.Default.Delete, onClearFavorites)
            SmallAction("Limpar histórico", Icons.Default.Delete, onClearHistory)
        }
        Text("Use somente listas e transmissões que você tenha autorização para acessar.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun EmptyState(text: String, onAdd: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(Panel).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text(text, color = Muted, fontSize = 14.sp)
        SmallAction("Adicionar lista M3U", Icons.Default.FolderOpen, onAdd)
    }
}

@Composable
private fun SmallAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Row(
        Modifier.clip(RoundedCornerShape(11.dp))
            .background(if (focused) Blue else PanelRaised)
            .border(1.dp, if (focused) Cyan else Blue.copy(alpha = .48f), RoundedCornerShape(11.dp))
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (focused) Navy else Cyan, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        Text(label, color = if (focused) Navy else White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@Composable
private fun AddListDialog(
    sourceType: String,
    onSourceTypeChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onImportUrl: () -> Unit,
    onImportXtream: () -> Unit,
    onChooseFile: () -> Unit
) {
    var validationMessage by remember(sourceType) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Panel,
        title = { Text("Adicionar conteúdo", color = White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = sourceType == "m3u", onClick = { onSourceTypeChange("m3u") }, label = { Text("M3U") })
                    FilterChip(selected = sourceType == "xtream", onClick = { onSourceTypeChange("xtream") }, label = { Text("Xtream Codes") })
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nome da lista") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        onUrlChange(it)
                        validationMessage = ""
                    },
                    label = { Text(if (sourceType == "xtream") "Servidor (ex.: https://servidor:porta)" else "Endereço M3U (http ou https)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (validationMessage.isNotBlank()) {
                    Text(validationMessage, color = ErrorCoral, fontSize = 12.sp)
                }
                if (sourceType == "m3u") {
                    Button(
                        onClick = onChooseFile,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PanelRaised, contentColor = White)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Escolher arquivo M3U")
                    }
                    Text("O endereço XMLTV será detectado automaticamente se estiver informado no cabeçalho da lista.", color = Muted, fontSize = 11.sp)
                } else {
                    OutlinedTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        label = { Text("Usuário") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Senha") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Os dados são guardados localmente para consultar player_api.php e xmltv.php no servidor informado. Não há conta NTPlaytree.", color = Muted, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            if (sourceType == "m3u") {
                TextButton(onClick = {
                    val normalizedUrl = url.trim()
                    val validUrl = normalizedUrl.startsWith("http://", ignoreCase = true) ||
                        normalizedUrl.startsWith("https://", ignoreCase = true)
                    if (!validUrl) {
                        validationMessage = "Informe o endereço completo da lista, começando com http:// ou https://."
                    } else {
                        validationMessage = ""
                        onImportUrl()
                    }
                }) {
                    Text("Importar M3U", color = Blue)
                }
            } else {
                TextButton(
                    onClick = onImportXtream,
                    enabled = (url.startsWith("http://") || url.startsWith("https://")) && username.isNotBlank() && password.isNotBlank()
                ) { Text("Conectar Xtream", color = Blue) }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Muted) } }
    )
}

private data class VlcSession(
    val libVlc: LibVLC,
    val player: VlcMediaPlayer
)

private data class VlcSessionResult(
    val session: VlcSession?,
    val error: String? = null
)

private fun androidDecoderSummary(): String {
    if (Build.VERSION.SDK_INT < 21) return "API sem consulta de MediaCodec"
    val codecs = runCatching { MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos }
        .getOrDefault(emptyArray())
    val formats = listOf(
        "video/avc" to "H.264",
        "video/hevc" to "H.265",
        "video/x-vnd.on2.vp9" to "VP9",
        "video/av01" to "AV1"
    )
    return formats.joinToString(" • ") { (mime, label) ->
        val codec = codecs.firstOrNull { info ->
            !info.isEncoder && info.supportedTypes.any { it.equals(mime, ignoreCase = true) }
        }
        val description = codec?.let {
            val kind = if (Build.VERSION.SDK_INT >= 29 && it.isHardwareAccelerated) "HW" else "dec."
            "$kind ${it.name.substringAfterLast('.') }"
        } ?: "não detectado"
        "$label: $description"
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlayerDialog(
    channel: Channel,
    initialPositionMs: Long,
    onProgress: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var playbackError by remember(channel.id) { mutableStateOf("") }
    var dnsCheck by remember(channel.id) { mutableStateOf("") }
    var retry429Count by remember(channel.id) { mutableIntStateOf(0) }
    var fillScreen by remember(channel.id) { mutableStateOf(false) }
    var useVlc by remember(channel.id) { mutableStateOf(false) }
    var vlcIsPlaying by remember(channel.id) { mutableStateOf(false) }
    var vlcLayout by remember(channel.id) { mutableStateOf<VLCVideoLayout?>(null) }
    val latestRetryCount by rememberUpdatedState(retry429Count)
    val latestUseVlc by rememberUpdatedState(useVlc)
    val latestOnProgress by rememberUpdatedState(onProgress)
    val decoderSummary = remember { androidDecoderSummary() }

    val trackSelector = remember(channel.id) {
        DefaultTrackSelector(context).apply {
            setParameters(
                buildUponParameters()
                    .setPreferredAudioLanguage("pt")
                    .setPreferredTextLanguage("pt")
                    .setSelectUndeterminedTextLanguage(false)
                    .build()
            )
        }
    }

    val player = remember(channel.url, channel.headers, trackSelector) {
        val requestHeaders = linkedMapOf(
            "Accept" to "*/*",
            "User-Agent" to "NTPlaytree/1.7 AndroidTV Media3-LibVLC",
            "Connection" to "keep-alive"
        ).apply {
            channel.headers.forEach { (name, value) ->
                val previousName = keys.firstOrNull { it.equals(name, ignoreCase = true) }
                if (previousName != null) remove(previousName)
                put(name, value)
            }
        }
        val httpFactory = OkHttpDataSource.Factory(PlaybackNetwork.client)
            .setDefaultRequestProperties(requestHeaders)
        val dataSourceFactory = DefaultDataSource.Factory(context, httpFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(8_000, 30_000, 1_500, 2_500)
            .build()
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)

        ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                val mediaItem = createNtMediaItem(channel)
                if (channel.kind != "live" && initialPositionMs > 0L) {
                    setMediaItem(mediaItem, initialPositionMs)
                } else {
                    setMediaItem(mediaItem)
                }
                prepare()
                playWhenReady = true
            }
    }

    // LibVLC is initialized only when selected, keeping the normal app footprint and startup path lighter.
    val vlcResult = remember(useVlc, context) {
        if (!useVlc) null else {
            var lib: LibVLC? = null
            runCatching {
                lib = LibVLC(
                    context,
                    arrayListOf(
                        "--no-video-title-show",
                        "--network-caching=1800",
                        "--live-caching=1800",
                        "--http-reconnect",
                        "--avcodec-hw=any"
                    )
                )
                VlcSession(lib!!, VlcMediaPlayer(lib!!))
            }.fold(
                onSuccess = { VlcSessionResult(it) },
                onFailure = { failure ->
                    runCatching { lib?.release() }
                    VlcSessionResult(null, failure.message ?: failure.javaClass.simpleName)
                }
            )
        }
    }
    val vlcSession = vlcResult?.session

    DisposableEffect(vlcSession) {
        val session = vlcSession
        val listener = if (session == null) null else object : VlcMediaPlayer.EventListener {
            override fun onEvent(event: VlcMediaPlayer.Event) {
                Handler(Looper.getMainLooper()).post {
                    when (event.type) {
                        VlcMediaPlayer.Event.Playing -> {
                            vlcIsPlaying = true
                            playbackError = ""
                        }
                        VlcMediaPlayer.Event.Paused,
                        VlcMediaPlayer.Event.Stopped,
                        VlcMediaPlayer.Event.EndReached -> vlcIsPlaying = false
                        VlcMediaPlayer.Event.EncounteredError -> {
                            vlcIsPlaying = false
                            playbackError = "O LibVLC também não conseguiu abrir este fluxo. Verifique o endereço, DNS, autorização e disponibilidade do servidor."
                        }
                    }
                }
            }
        }
        if (listener != null) session?.player?.setEventListener(listener)
        onDispose {
            runCatching { session?.player?.stop() }
            runCatching { session?.player?.detachViews() }
            runCatching { session?.player?.release() }
            runCatching { session?.libVlc?.release() }
        }
    }


    LaunchedEffect(useVlc, vlcSession, vlcLayout, channel.url) {
        if (!useVlc) {
            vlcIsPlaying = false
            return@LaunchedEffect
        }
        val session = vlcSession
        if (session == null) {
            playbackError = "Não foi possível iniciar o LibVLC: ${vlcResult?.error ?: "erro ao carregar o motor VLC"}"
            return@LaunchedEffect
        }
        if (vlcLayout == null) return@LaunchedEffect

        playbackError = "Abrindo transmissão com LibVLC..."
        runCatching {
            val media = Media(session.libVlc, Uri.parse(channel.url))
            media.setHWDecoderEnabled(true, false)
            media.addOption(":network-caching=1800")
            media.addOption(":live-caching=1800")
            media.addOption(":http-reconnect")
            channel.headers.forEach { (name, value) ->
                when (name.lowercase(Locale.ROOT)) {
                    "user-agent" -> media.addOption(":http-user-agent=$value")
                    "referer", "referrer" -> media.addOption(":http-referrer=$value")
                    "origin" -> media.addOption(":http-origin=$value")
                    "cookie" -> media.addOption(":http-cookie=$value")
                }
            }
            session.player.media = media
            media.release()
            session.player.play()
        }.onFailure {
            playbackError = "Falha ao iniciar o LibVLC: ${it.message ?: it.javaClass.simpleName}"
        }
    }

    DisposableEffect(player, channel.url) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                if (latestUseVlc) return
                val responseCode = findHttpResponseCode(error)
                if (channel.kind == "live" && responseCode == 429 && latestRetryCount < 7) {
                    val nextAttempt = latestRetryCount + 1
                    retry429Count = nextAttempt
                    playbackError = "O servidor limitou as conexões (HTTP 429). Nova tentativa $nextAttempt de 7..."
                    return
                }

                val networkFailure = responseCode != null ||
                    findPlaybackCause<java.net.UnknownHostException>(error) != null ||
                    findPlaybackCause<java.net.SocketTimeoutException>(error) != null ||
                    findPlaybackCause<java.net.ConnectException>(error) != null ||
                    findPlaybackCause<javax.net.ssl.SSLException>(error) != null
                val errorName = error.errorCodeName.uppercase(Locale.ROOT)
                val decoderOrParserFailure = listOf("DECODER", "DECODING", "RENDERER", "PARSING", "UNSUPPORTED")
                    .any { it in errorName }

                if (!networkFailure && decoderOrParserFailure) {
                    playbackError = "O Media3 não conseguiu decodificar este formato. Testando o motor VLC..."
                    vlcLayout = null
                    useVlc = true
                } else {
                    playbackError = describePlaybackFailure(error, channel.url)
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && !latestUseVlc) {
                    playbackError = ""
                    retry429Count = 0
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(retry429Count, useVlc) {
        if (!useVlc && retry429Count in 1..7) {
            val waitMs = (2_000L * retry429Count).coerceAtMost(14_000L)
            delay(waitMs)
            player.seekToDefaultPosition()
            player.prepare()
            player.playWhenReady = true
        }
    }

    LaunchedEffect(useVlc, player) {
        if (useVlc) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_IDLE || player.playerError != null) player.prepare()
            player.playWhenReady = true
        }
    }

    LaunchedEffect(player, useVlc) {
        if (!useVlc) {
            while (true) {
                delay(5_000L)
                val duration = player.duration.takeIf { it > 0L } ?: 0L
                latestOnProgress(player.currentPosition.coerceAtLeast(0L), duration)
            }
        }
    }

    LaunchedEffect(vlcSession, useVlc) {
        if (useVlc && vlcSession != null) {
            while (true) {
                delay(5_000L)
                val duration = vlcSession.player.length.takeIf { it > 0L } ?: 0L
                latestOnProgress(vlcSession.player.time.coerceAtLeast(0L), duration)
            }
        }
    }

    DisposableEffect(player) {
        onDispose {
            val duration = player.duration.takeIf { it > 0L } ?: 0L
            latestOnProgress(player.currentPosition.coerceAtLeast(0L), duration)
            player.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black,
            shape = RoundedCornerShape(0.dp)
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black)) {
                if (!useVlc) {
                    AndroidView(
                        factory = { viewContext ->
                            PlayerView(viewContext).apply {
                                this.player = player
                                useController = true
                                resizeMode = if (fillScreen) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
                                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                                setShowSubtitleButton(true)
                                setShowRewindButton(true)
                                setShowFastForwardButton(true)
                                controllerAutoShow = true
                                controllerShowTimeoutMs = 5_000
                                controllerHideOnTouch = false
                                isFocusable = true
                                isFocusableInTouchMode = true
                                keepScreenOn = true
                                setOnKeyListener { _, keyCode, event ->
                                    if (event.action != AndroidKeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                    when (keyCode) {
                                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                                            if (player.isPlaying) player.pause() else player.play()
                                            showController()
                                            true
                                        }
                                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY -> { player.play(); showController(); true }
                                        AndroidKeyEvent.KEYCODE_MEDIA_PAUSE -> { player.pause(); showController(); true }
                                        AndroidKeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> { player.seekForward(); showController(); true }
                                        AndroidKeyEvent.KEYCODE_MEDIA_REWIND -> { player.seekBack(); showController(); true }
                                        AndroidKeyEvent.KEYCODE_DPAD_CENTER,
                                        AndroidKeyEvent.KEYCODE_ENTER,
                                        AndroidKeyEvent.KEYCODE_DPAD_LEFT,
                                        AndroidKeyEvent.KEYCODE_DPAD_RIGHT,
                                        AndroidKeyEvent.KEYCODE_DPAD_UP,
                                        AndroidKeyEvent.KEYCODE_DPAD_DOWN -> { showController(); false }
                                        else -> false
                                    }
                                }
                                post { requestFocus(); showController() }
                            }
                        },
                        update = { view ->
                            view.player = player
                            view.keepScreenOn = true
                            view.resizeMode = if (fillScreen) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (vlcSession != null) {
                    AndroidView(
                        factory = { viewContext ->
                            VLCVideoLayout(viewContext).apply {
                                isFocusable = true
                                isFocusableInTouchMode = true
                                keepScreenOn = true
                                setOnKeyListener { _, keyCode, event ->
                                    if (event.action != AndroidKeyEvent.ACTION_DOWN) return@setOnKeyListener false
                                    when (keyCode) {
                                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                                        AndroidKeyEvent.KEYCODE_DPAD_CENTER,
                                        AndroidKeyEvent.KEYCODE_ENTER -> {
                                            if (vlcSession.player.isPlaying) vlcSession.player.pause() else vlcSession.player.play()
                                            true
                                        }
                                        AndroidKeyEvent.KEYCODE_MEDIA_PLAY -> { vlcSession.player.play(); true }
                                        AndroidKeyEvent.KEYCODE_MEDIA_PAUSE -> { vlcSession.player.pause(); true }
                                        else -> false
                                    }
                                }
                                vlcSession.player.attachViews(this, null, false, false)
                                vlcLayout = this
                                post { requestFocus() }
                            }
                        },
                        update = { view -> view.keepScreenOn = true },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("O motor VLC não foi inicializado.", color = White)
                    }
                }

                Row(
                    Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xDD090A0F), Color.Transparent)))
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(channel.name, color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            if (channel.kind == "live") "AO VIVO • ${if (useVlc) "LIBVLC" else "MEDIA3"}" else "${if (useVlc) "LIBVLC" else "MEDIA3"} PLAYER",
                            color = Blue,
                            fontSize = 10.sp,
                            letterSpacing = 1.3.sp
                        )
                    }
                    SmallAction(if (useVlc) "VOLTAR MEDIA3" else "TESTAR VLC", Icons.Default.PlayArrow) {
                        playbackError = if (useVlc) "Voltando para Media3..." else "Testando LibVLC manualmente..."
                        vlcLayout = null
                        useVlc = !useVlc
                    }
                    TextButton(onClick = { fillScreen = !fillScreen }) {
                        Icon(if (fillScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen, contentDescription = null, tint = Blue)
                        Spacer(Modifier.width(5.dp))
                        Text(if (fillScreen) "Ajustar" else "Tela cheia", color = White)
                    }
                    TextButton(onClick = onDismiss) { Text("Fechar", color = White) }
                }

                if (useVlc && vlcSession != null) {
                    Button(
                        onClick = {
                            if (vlcSession.player.isPlaying) vlcSession.player.pause() else vlcSession.player.play()
                        },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = if (playbackError.isBlank()) 22.dp else 124.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = Navy)
                    ) { Text(if (vlcIsPlaying) "Pausar" else "Reproduzir", fontWeight = FontWeight.Bold) }
                }

                if (playbackError.isNotBlank()) {
                    Column(
                        Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color(0xF0141820))
                            .border(1.dp, AccentPink.copy(alpha = .35f))
                            .padding(horizontal = 22.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(playbackError, color = Color(0xFFFFA1BE), fontSize = 12.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                if (useVlc) {
                                    vlcLayout = null
                                    useVlc = false
                                    playbackError = "Voltando para Media3..."
                                } else {
                                    vlcLayout = null
                                    useVlc = true
                                    playbackError = "Testando LibVLC..."
                                }
                            }) { Text(if (useVlc) "Media3" else "Testar VLC", color = Blue) }
                            TextButton(onClick = {
                                playbackError = "Tentando novamente..."
                                retry429Count = 0
                                if (useVlc) {
                                    vlcSession?.player?.stop()
                                    vlcSession?.player?.play()
                                } else {
                                    player.setMediaItem(createNtMediaItem(channel))
                                    player.prepare()
                                    player.playWhenReady = true
                                }
                            }) { Text("Tentar", color = White) }
                        }
                        Text("DECODIFICADORES DO ANDROID • $decoderSummary", color = Muted, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        if (playbackError.contains("DNS", ignoreCase = true)) {
                            val host = Uri.parse(channel.url).host.orEmpty()
                            TextButton(onClick = {
                                dnsCheck = "Consultando DNS do host $host..."
                                scope.launch {
                                    val result = withContext(Dispatchers.IO) {
                                        runCatching { PlaybackNetwork.resolveHost(host).map { it.hostAddress }.distinct() }
                                    }
                                    dnsCheck = result.fold(
                                        onSuccess = { addresses -> if (addresses.isEmpty()) "DNS sem endereços IP." else "DNS resolvido: ${addresses.joinToString()}" },
                                        onFailure = { "DNS não resolveu $host: ${it.message ?: it.javaClass.simpleName}" }
                                    )
                                }
                            }) { Text("Testar DNS", color = Blue) }
                            if (dnsCheck.isNotBlank()) Text(dnsCheck, color = White, fontSize = 10.sp)
                        }
                        if (playbackError.contains("DNS", ignoreCase = true)) {
                            Text("A falha acontece antes da etapa de vídeo. Trocar o decodificador ou usar VLC não corrige um domínio sem resolução DNS.", color = Color(0xFFFFD18B), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun createNtMediaItem(channel: Channel, urlOverride: String? = null): MediaItem {
    val url = urlOverride ?: channel.url
    val uri = Uri.parse(url)
    val path = (uri.encodedPath ?: uri.lastPathSegment.orEmpty()).lowercase(Locale.ROOT)
    val normalizedUrl = url.lowercase(Locale.ROOT)
    val extension = path.substringAfterLast('.', "")
    val queryType = uri.getQueryParameter("type")?.lowercase(Locale.ROOT).orEmpty()
    val outputType = uri.getQueryParameter("output")?.lowercase(Locale.ROOT).orEmpty()
    val formatType = uri.getQueryParameter("format")?.lowercase(Locale.ROOT).orEmpty()
    val mimeType = when {
        extension == "m3u8" || queryType.contains("m3u8") || outputType in setOf("m3u8", "hls") || formatType in setOf("m3u8", "hls") || "output=m3u8" in normalizedUrl -> MimeTypes.APPLICATION_M3U8
        extension == "mpd" || queryType == "dash" || formatType == "mpd" || "format=mpd" in normalizedUrl -> MimeTypes.APPLICATION_MPD
        extension == "ts" || extension == "m2ts" || queryType in setOf("ts", "mpegts") || outputType in setOf("ts", "mpegts") || formatType in setOf("ts", "mpegts") -> "video/mp2t"
        extension in setOf("mp4", "m4v", "mov") -> "video/mp4"
        extension == "mkv" -> "video/x-matroska"
        extension == "webm" -> "video/webm"
        extension == "mp3" -> "audio/mpeg"
        else -> null
    }
    return MediaItem.Builder()
        .setUri(uri)
        .apply { if (mimeType != null) setMimeType(mimeType) }
        .build()
}

private fun findHttpResponseCode(error: Throwable?): Int? {
    var current = error
    while (current != null) {
        if (current is HttpDataSource.InvalidResponseCodeException) return current.responseCode
        current = current.cause
    }
    return null
}

private inline fun <reified T : Throwable> findPlaybackCause(error: Throwable?): T? {
    var current = error
    val visited = HashSet<Throwable>()
    while (current != null && visited.add(current)) {
        if (current is T) return current
        current = current.cause
    }
    return null
}

private fun findHttpStatusCode(error: Throwable): Int? {
    var current: Throwable? = error
    val visited = HashSet<Throwable>()
    while (current != null && visited.add(current)) {
        val match = Regex("""\bHTTP\s+(\d{3})\b""", RegexOption.IGNORE_CASE).find(current.message.orEmpty())
        match?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { return it }
        current = current.cause
    }
    return null
}

private fun describeSourceLoadFailure(error: Throwable, sourceUrl: String): String {
    val host = runCatching { Uri.parse(sourceUrl).host }.getOrNull().orEmpty()
    val hostSuffix = if (host.isBlank()) "" else " Host: $host."
    val httpCode = findHttpStatusCode(error)
    return when {
        httpCode == 522 ->
            "HTTP 522 (Gateway Timeout): o servidor intermediário/CDN recebeu a requisição, mas não conseguiu conectar ao servidor de origem dentro do prazo. O aplicativo recebeu a resposta; não é falha do VLC, Media3 ou decodificador. O provedor precisa verificar o host de origem, firewall, porta e rota.$hostSuffix"
        httpCode == 521 ->
            "HTTP 521: o servidor de origem recusou a conexão do proxy/CDN. O provedor precisa verificar se o serviço está ativo e se o firewall permite a conexão.$hostSuffix"
        httpCode == 523 ->
            "HTTP 523: o proxy/CDN não conseguiu alcançar o servidor de origem. O provedor precisa revisar o endereço e a rota do host.$hostSuffix"
        httpCode == 524 ->
            "HTTP 524: a conexão com a origem foi estabelecida, mas o servidor demorou demais para responder. O provedor precisa revisar a carga e o processamento do servidor.$hostSuffix"
        httpCode != null && httpCode in 500..599 ->
            "O servidor intermediário ou de origem respondeu com HTTP $httpCode. A falha está no serviço remoto; tente novamente mais tarde ou solicite verificação ao provedor.$hostSuffix"
        httpCode != null ->
            "O servidor respondeu com HTTP $httpCode ao baixar a lista. Confira o endereço, as permissões e as credenciais da fonte.$hostSuffix"
        findPlaybackCause<java.net.UnknownHostException>(error) != null ->
            "Falha de DNS: o Android não conseguiu localizar o domínio da lista. Confirme o endereço com o provedor e teste a rede.$hostSuffix"
        findPlaybackCause<java.net.SocketTimeoutException>(error) != null ||
            findPlaybackCause<java.io.InterruptedIOException>(error) != null ->
            "Tempo esgotado ao baixar a lista. A conexão não terminou dentro do limite configurado.$hostSuffix"
        findPlaybackCause<java.net.ConnectException>(error) != null ->
            "Conexão recusada ao baixar a lista. Confira host, porta e disponibilidade do serviço.$hostSuffix"
        findPlaybackCause<javax.net.ssl.SSLException>(error) != null ->
            "Falha de segurança HTTPS/TLS ao baixar a lista.$hostSuffix"
        else -> error.message?.takeIf { it.isNotBlank() } ?: "Falha desconhecida ao ler a lista.$hostSuffix"
    }
}

private fun describePlaybackFailure(error: PlaybackException, streamUrl: String): String {
    val responseCode = findHttpResponseCode(error)
    val host = runCatching { Uri.parse(streamUrl).host }.getOrNull().orEmpty()
    val hostSuffix = if (host.isBlank()) "" else " Servidor: $host."
    return when {
        responseCode == 401 || responseCode == 403 -> "O servidor recusou o acesso (HTTP $responseCode). Confira usuário, senha, validade do link e cabeçalhos exigidos pelo provedor.$hostSuffix"
        responseCode == 404 || responseCode == 410 -> "O endereço desta transmissão não existe ou expirou (HTTP $responseCode). Solicite ao provedor um link válido.$hostSuffix"
        responseCode == 429 -> "O servidor limitou as conexões (HTTP 429). Aguarde um pouco e tente novamente.$hostSuffix"
        responseCode != null && responseCode in 500..599 -> "O servidor de transmissão apresentou erro (HTTP $responseCode). O problema está no servidor ou na CDN do provedor.$hostSuffix"
        responseCode != null -> "O servidor respondeu com HTTP $responseCode. Verifique o endereço e as permissões da transmissão.$hostSuffix"
        findPlaybackCause<java.net.UnknownHostException>(error) != null -> "Falha de DNS: o DNS do Android falhou e as consultas alternativas Cloudflare/Google também não conseguiram obter um IP para ${host.ifBlank { "o domínio informado na lista" }}. A conexão não chegou ao servidor. Isso pode indicar domínio sem registro/expirado ou bloqueio de DNS-over-HTTPS; confirme com o provedor o host ativo."
        findPlaybackCause<java.net.SocketTimeoutException>(error) != null ||
            findPlaybackCause<java.io.InterruptedIOException>(error) != null -> "Tempo de conexão esgotado. O servidor não respondeu dentro do prazo ou está bloqueando a conexão.$hostSuffix"
        findPlaybackCause<java.net.ConnectException>(error) != null -> "A conexão foi recusada pelo servidor. Verifique o host, a porta e a disponibilidade do serviço.$hostSuffix"
        findPlaybackCause<javax.net.ssl.SSLException>(error) != null -> "Falha na conexão HTTPS/TLS. O certificado ou a configuração segura do servidor pode estar inválida.$hostSuffix"
        error.errorCodeName.contains("PARSING", ignoreCase = true) || error.errorCodeName.contains("UNSUPPORTED", ignoreCase = true) -> "O servidor respondeu, mas o formato da transmissão não foi reconhecido. O link pode não ser HLS, DASH ou um formato compatível.$hostSuffix"
        else -> "Falha ao conectar à transmissão (${error.errorCodeName}). Confira o DNS, a conexão com o servidor e se o link ainda está ativo.$hostSuffix"
    }
}


private fun playlistBrowserUserAgent(context: Context): String {
    val systemAgent = runCatching {
        android.webkit.WebSettings.getDefaultUserAgent(context)
    }.getOrNull().orEmpty()
    if (systemAgent.isNotBlank()) {
        // Use the device's installed Chromium/WebView version, without the WebView marker.
        return systemAgent
            .replace("; wv", "", ignoreCase = true)
            .replace("Version/4.0 ", "")
            .take(512)
    }
    return "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}; ${Build.MODEL}) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
}

private fun loadSource(
    context: Context,
    source: PlaylistSource,
    onProgress: (String) -> Unit = {}
): SourceLoadResult {
    if (source.type == "xtream") return loadXtreamSource(source)

    if (source.isLocalFile) {
        onProgress("abrindo arquivo M3U")
        val uri = Uri.parse(source.location)
        return if (uri.scheme == "file") {
            File(uri.path ?: error("Caminho do arquivo inválido."))
                .bufferedReader(Charsets.UTF_8)
                .use { reader -> parseM3u(reader, source) { count -> onProgress("lendo $count itens") } }
        } else {
            context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
                parseM3u(reader, source) { count -> onProgress("lendo $count itens") }
            } ?: error("Não foi possível abrir o arquivo M3U.")
        }
    }

    onProgress("conectando ao servidor")
    val request = Request.Builder()
        .url(source.location)
        .header("User-Agent", playlistBrowserUserAgent(context))
        .header("Accept", "application/vnd.apple.mpegurl, application/x-mpegURL, audio/x-mpegurl, audio/mpegurl, text/plain, */*")
        .header("Cache-Control", "no-cache")
        .header("Pragma", "no-cache")
        // Do not set Accept-Encoding manually: OkHttp then negotiates gzip and
        // transparently decompresses the response, as a browser does.
        .get()
        .build()

    return PlaybackNetwork.playlistClient.newCall(request).execute().use { response ->
        val finalHost = response.request.url.host
        val body = response.body
        require(response.isSuccessful) {
            "O servidor respondeu HTTP ${response.code} ao baixar a lista M3U (destino: $finalHost)."
        }
        require(body != null) { "O servidor respondeu HTTP ${response.code}, mas não enviou conteúdo M3U." }

        val contentType = body.contentType()?.toString() ?: "tipo não informado"
        val contentLength = body.contentLength()
        val sizeDescription = if (contentLength >= 0L) {
            if (contentLength >= 1_048_576L) "${contentLength / 1_048_576L} MB" else "${contentLength / 1024L} KB"
        } else "tamanho informado pelo servidor desconhecido"
        onProgress("HTTP ${response.code} • $sizeDescription • processando catálogo")

        try {
            body.charStream().buffered().use { reader ->
                parseM3u(reader, source) { count -> onProgress("lendo $count itens") }
            }
        } catch (error: Exception) {
            val invalidPlaylist = error.message.orEmpty().contains("não é uma lista M3U válida", ignoreCase = true)
            if (invalidPlaylist) {
                throw java.io.IOException(
                    "O endereço respondeu HTTP ${response.code}, mas o conteúdo não é uma M3U válida. " +
                        "Tipo recebido: $contentType; $sizeDescription; destino: $finalHost. " +
                        "O servidor pode ter enviado uma página HTML ou uma resposta diferente da lista.",
                    error
                )
            }
            throw error
        }
    }
}

private fun buildSeriesCatalog(channels: List<Channel>): Pair<List<Channel>, Map<String, List<Channel>>> {
    val explicitSeries = channels.filter { it.kind == "series" }
    val m3uEpisodes = channels.filter { it.kind == "episode" }
    val grouped = m3uEpisodes.groupBy { episode ->
        "${episode.sourceId}:${(episode.seriesTitle ?: episode.name).trim().lowercase(Locale.ROOT)}"
    }
    val episodesBySeries = linkedMapOf<String, List<Channel>>()
    val generatedSeries = grouped.map { (key, episodes) ->
        val first = episodes.first()
        val title = first.seriesTitle?.takeIf { it.isNotBlank() } ?: first.name
        val id = "m3u-series:${key.hashCode()}"
        episodesBySeries[id] = episodes.sortedWith(
            compareBy<Channel> { it.seasonNumber?.toIntOrNull() ?: 1 }
                .thenBy { it.episodeNumber?.toIntOrNull() ?: Int.MAX_VALUE }
                .thenBy { it.name.lowercase(Locale.ROOT) }
        )
        Channel(
            id = id,
            name = title,
            url = "",
            group = first.group,
            logo = first.logo,
            tvgId = null,
            sourceId = first.sourceId,
            kind = "series",
            seriesTitle = title,
            description = first.description
        )
    }
    return (explicitSeries + generatedSeries).distinctBy { it.id } to episodesBySeries
}

private fun parseM3uAttributes(line: String): Map<String, String> {
    // Accept single-quoted, double-quoted and unquoted M3U attributes.
    val pattern = Regex("""([A-Za-z0-9_-]+)\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s,]+))""")
    return pattern.findAll(line).associate { match ->
        val value = listOf(2, 3, 4).firstNotNullOfOrNull { index ->
            match.groupValues.getOrNull(index)?.takeIf { it.isNotEmpty() }
        }.orEmpty()
        match.groupValues[1].lowercase(Locale.ROOT) to value
    }
}

private fun extinfDisplayName(line: String): String {
    var quote: Char? = null
    line.forEachIndexed { index, char ->
        if (quote != null) {
            if (char == quote) quote = null
        } else {
            when (char) {
                '"', '\'' -> quote = char
                ',' -> return line.substring(index + 1).trim()
            }
        }
    }
    return ""
}

/** Reads one M3U line without allowing a malformed, extremely long line to exhaust the heap. */
private fun readM3uLine(reader: BufferedReader): String? {
    val line = StringBuilder(256)
    var sawCharacter = false
    var tooLong = false
    while (true) {
        val value = reader.read()
        if (value == -1) {
            if (!sawCharacter) return null
            break
        }
        if (value == '\n'.code) break
        if (value == '\r'.code) {
            reader.mark(1)
            val next = reader.read()
            if (next != '\n'.code && next != -1) reader.reset()
            break
        }
        sawCharacter = true
        if (!tooLong) {
            if (line.length < MAX_M3U_LINE_CHARS) line.append(value.toChar()) else tooLong = true
        }
    }
    // Ignore only the oversized line; continue parsing the rest of the playlist.
    return if (tooLong) OVERSIZED_M3U_LINE else line.toString()
}

private fun parseInlineStreamHeaders(rawLine: String): Pair<String, Map<String, String>> {
    val separator = rawLine.indexOf('|')
    if (separator < 0) return rawLine.trim() to emptyMap()
    val baseUrl = rawLine.substring(0, separator).trim()
    val headers = linkedMapOf<String, String>()
    rawLine.substring(separator + 1).split('&').forEach { pair ->
        val key = pair.substringBefore('=').trim()
        val rawValue = pair.substringAfter('=', "").trim()
        if (key.isBlank() || rawValue.isBlank()) return@forEach
        val value = runCatching { URLDecoder.decode(rawValue, "UTF-8") }.getOrDefault(rawValue)
        val normalizedKey = when (key.lowercase(Locale.ROOT)) {
            "user-agent", "user_agent" -> "User-Agent"
            "referer", "referrer" -> "Referer"
            "origin" -> "Origin"
            "cookie" -> "Cookie"
            else -> key.take(80)
        }
        headers[normalizedKey] = value.take(8192)
    }
    return baseUrl to headers
}

private fun parseM3u(
    reader: BufferedReader,
    source: PlaylistSource,
    onProgress: ((Int) -> Unit)? = null
): SourceLoadResult {
    var header: String? = null
    while (header == null) {
        val line = readM3uLine(reader) ?: break
        if (line.isNotBlank()) header = line.trim().removePrefix("\uFEFF").trim()
    }
    require(header?.startsWith("#EXTM3U", ignoreCase = true) == true) {
        "A resposta do endereço não é uma lista M3U válida. Verifique se o link é o endereço completo da lista."
    }

    val output = ArrayList<Channel>()
    var pendingName = "Canal"
    var pendingGroup = "Sem categoria"
    var pendingLogo: String? = null
    var pendingTvgId: String? = null
    var pendingDescription: String? = null
    var pendingYear: String? = null
    var pendingContentType: String? = null
    val pendingHeaders = linkedMapOf<String, String>()
    val headerAttributes = parseM3uAttributes(header.orEmpty())
    val epgUrl = headerAttributes["url-tvg"]
        ?: headerAttributes["x-tvg-url"]
        ?: headerAttributes["tvg-url"]

    while (output.size < MAX_M3U_ITEMS) {
        val rawLine = readM3uLine(reader) ?: break
        if (rawLine == OVERSIZED_M3U_LINE) {
            pendingName = "Canal"
            pendingGroup = "Sem categoria"
            pendingLogo = null
            pendingTvgId = null
            pendingDescription = null
            pendingYear = null
            pendingContentType = null
            pendingHeaders.clear()
            continue
        }
        val line = rawLine.trim().removePrefix("\uFEFF").trim()
        when {
            line.isBlank() -> Unit
            line.startsWith("#EXTVLCOPT:", ignoreCase = true) -> {
                val option = line.substringAfter(':')
                val key = option.substringBefore('=').trim().lowercase(Locale.ROOT)
                val value = option.substringAfter('=', "").trim().trim('"', '\'')
                when (key) {
                    "http-user-agent" -> if (value.isNotBlank()) pendingHeaders["User-Agent"] = value.take(8192)
                    "http-referrer", "http-referer" -> if (value.isNotBlank()) pendingHeaders["Referer"] = value.take(8192)
                    "http-origin" -> if (value.isNotBlank()) pendingHeaders["Origin"] = value.take(8192)
                    "http-cookie" -> if (value.isNotBlank()) pendingHeaders["Cookie"] = value.take(8192)
                }
            }
            line.startsWith("#EXTHTTP:", ignoreCase = true) -> {
                val rawHeaders = line.substringAfter(':').trim()
                runCatching {
                    val json = JSONObject(rawHeaders)
                    json.keys().forEach { key ->
                        val value = json.optString(key, "")
                        if (key.isNotBlank() && value.isNotBlank()) pendingHeaders[key.take(80)] = value.take(8192)
                    }
                }
            }
            line.startsWith("#KODIPROP:", ignoreCase = true) -> {
                val property = line.substringAfter(':')
                if (property.substringBefore('=').contains("stream_headers", ignoreCase = true)) {
                    property.substringAfter('=', "").split('&').forEach { pair ->
                        val key = pair.substringBefore('=').trim()
                        val value = pair.substringAfter('=', "").trim()
                        if (key.isNotBlank() && value.isNotBlank()) pendingHeaders[key.take(80)] = value.take(8192)
                    }
                }
            }
            line.startsWith("#EXTINF", ignoreCase = true) -> {
                val attributes = parseM3uAttributes(line)
                pendingName = extinfDisplayName(line).ifBlank {
                    attributes["tvg-name"].orEmpty().ifBlank { "Canal" }
                }.take(220)
                pendingGroup = (attributes["group-title"]?.ifBlank { null } ?: "Sem categoria").take(140)
                pendingLogo = attributes["tvg-logo"]?.ifBlank { null }?.take(2048)
                pendingTvgId = attributes["tvg-id"]?.ifBlank { null }?.take(256)
                pendingDescription = (attributes["description"] ?: attributes["plot"])?.take(1800)
                pendingYear = (attributes["tvg-year"] ?: attributes["year"])?.take(12)
                pendingContentType = attributes["tvg-type"] ?: attributes["type"] ?: attributes["content-type"] ?: attributes["media-type"]
            }
            line.startsWith("#EXTGRP", ignoreCase = true) -> {
                pendingGroup = line.substringAfter(':', "Sem categoria").trim().ifBlank { "Sem categoria" }.take(140)
            }
            !line.startsWith('#') -> {
                val (urlWithoutHeaders, inlineHeaders) = parseInlineStreamHeaders(line)
                val resolvedUrl = resolveStreamUrl(urlWithoutHeaders, source)
                if (resolvedUrl.isNotBlank() && resolvedUrl.length <= 8192) {
                    val episodeInfo = parseEpisodeName(pendingName, pendingGroup)
                    val contentKind = classifyM3uContent(pendingGroup, pendingName, urlWithoutHeaders, pendingContentType, episodeInfo)
                    val isEpisode = contentKind == "episode"
                    val episodeDisplayName = if (episodeInfo != null) {
                        "E${episodeInfo.episode} • ${episodeInfo.episodeTitle}"
                    } else pendingName
                    val channelHeaders = LinkedHashMap(pendingHeaders).apply { putAll(inlineHeaders) }

                    output.add(
                        Channel(
                            id = "${source.id}:${output.size}",
                            name = if (isEpisode) episodeDisplayName.take(220) else pendingName,
                            url = resolvedUrl,
                            group = pendingGroup,
                            logo = pendingLogo,
                            tvgId = pendingTvgId,
                            sourceId = source.id,
                            kind = contentKind,
                            seriesTitle = episodeInfo?.seriesTitle?.take(220),
                            seasonNumber = episodeInfo?.season,
                            episodeNumber = episodeInfo?.episode,
                            description = pendingDescription,
                            year = pendingYear,
                            headers = if (channelHeaders.isEmpty()) emptyMap() else channelHeaders.toMap()
                        )
                    )
                    if (output.size % 1000 == 0) onProgress?.invoke(output.size)
                }
                pendingName = "Canal"
                pendingGroup = "Sem categoria"
                pendingLogo = null
                pendingTvgId = null
                pendingDescription = null
                pendingYear = null
                pendingContentType = null
                pendingHeaders.clear()
            }
        }
    }

    val hitItemLimit = output.size >= MAX_M3U_ITEMS
    require(output.isNotEmpty()) {
        "A lista M3U foi recebida, mas não contém endereços de mídia válidos."
    }
    val warning = if (hitItemLimit) "Limite de $MAX_M3U_ITEMS itens atingido; o restante da lista foi ignorado para proteger a memória do dispositivo." else null
    return SourceLoadResult(output, epgUrl, warning)
}

private fun loadXtreamSource(source: PlaylistSource): SourceLoadResult {
    require(source.username.isNotBlank() && source.password.isNotBlank()) { "Informe usuário e senha Xtream." }
    val server = source.location.trim().trimEnd('/')
    require(server.startsWith("http://") || server.startsWith("https://")) { "O servidor Xtream precisa começar com http:// ou https://." }
    val user = encodePathSegment(source.username)
    val pass = encodePathSegment(source.password)
    val query = "username=${encodeQuery(source.username)}&password=${encodeQuery(source.password)}"

    val endpointNames = listOf(
        "get_live_categories",
        "get_vod_categories",
        "get_series_categories",
        "get_live_streams",
        "get_vod_streams",
        "get_series"
    )
    val executor = Executors.newFixedThreadPool(endpointNames.size)
    val futures = endpointNames.associateWith { actionName ->
        executor.submit(Callable {
            runCatching {
                val body = fetchText(
                    "$server/player_api.php?$query&action=$actionName",
                    connectTimeoutMs = 8_000,
                    readTimeoutMs = 17_000
                ).trim()
                require(body.startsWith("[")) { "Resposta inválida ao consultar $actionName." }
                JSONArray(body)
            }
        })
    }

    val arrays = mutableMapOf<String, JSONArray>()
    val endpointWarnings = mutableListOf<String>()
    try {
        futures.forEach { (actionName, future) ->
            val result = runCatching { future.get(28, TimeUnit.SECONDS).getOrThrow() }
            result.onSuccess { arrays[actionName] = it }
                .onFailure { error ->
                    future.cancel(true)
                    // Category endpoints are optional; content endpoints are not.
                    if (actionName.endsWith("_streams") || actionName == "get_series") {
                        endpointWarnings.add("$actionName: ${error.message ?: "tempo esgotado"}")
                    }
                }
        }
    } finally {
        executor.shutdownNow()
    }

    fun categoryMap(actionName: String): Map<String, String> {
        val array = arrays[actionName] ?: return emptyMap()
        return buildMap {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val id = item.optString("category_id", "")
                val name = item.optString("category_name", "")
                if (id.isNotBlank() && name.isNotBlank()) put(id, name)
            }
        }
    }

    val liveCategories = categoryMap("get_live_categories")
    val movieCategories = categoryMap("get_vod_categories")
    val seriesCategories = categoryMap("get_series_categories")
    val output = mutableListOf<Channel>()

    arrays["get_live_streams"]?.let { array ->
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val id = item.optString("stream_id", "")
            if (id.isBlank()) continue
            val categoryId = item.optString("category_id", "")
            val name = item.optString("name", "Canal $id")
            val directSource = item.optString("direct_source", "").takeIf { it.startsWith("http://", true) || it.startsWith("https://", true) }
            val liveExtension = item.optString("container_extension", "ts").removePrefix(".").ifBlank { "ts" }
            output += Channel(
                id = "${source.id}:live:$id",
                name = name,
                url = directSource ?: "$server/live/$user/$pass/$id.$liveExtension",
                group = liveCategories[categoryId] ?: item.optString("category_name", "TV ao vivo").ifBlank { "TV ao vivo" },
                logo = item.optString("stream_icon", "").takeIf { it.isNotBlank() && it != "null" },
                tvgId = item.optString("epg_channel_id", "").takeIf { it.isNotBlank() && it != "null" },
                sourceId = source.id,
                kind = "live"
            )
        }
    }

    arrays["get_vod_streams"]?.let { array ->
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val id = item.optString("stream_id", "")
            if (id.isBlank()) continue
            val categoryId = item.optString("category_id", "")
            val extension = item.optString("container_extension", "mp4").ifBlank { "mp4" }
            output += Channel(
                id = "${source.id}:movie:$id",
                name = item.optString("name", "Filme $id"),
                url = "$server/movie/$user/$pass/$id.$extension",
                group = movieCategories[categoryId] ?: item.optString("category_name", "Filmes").ifBlank { "Filmes" },
                logo = item.optString("stream_icon", "").takeIf { it.isNotBlank() && it != "null" },
                tvgId = null,
                sourceId = source.id,
                kind = "movie",
                description = item.optString("plot", "").takeIf { it.isNotBlank() && it != "null" },
                year = item.optString("year", item.optString("releasedate", "")).take(4).takeIf { it.isNotBlank() && it != "null" }
            )
        }
    }

    arrays["get_series"]?.let { array ->
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val id = item.optString("series_id", "")
            if (id.isBlank()) continue
            val categoryId = item.optString("category_id", "")
            val title = item.optString("name", "Série $id")
            output += Channel(
                id = "${source.id}:series:$id",
                name = title,
                url = "",
                group = seriesCategories[categoryId] ?: item.optString("category_name", "Séries").ifBlank { "Séries" },
                logo = item.optString("cover", "").takeIf { it.isNotBlank() && it != "null" },
                tvgId = null,
                sourceId = source.id,
                kind = "series",
                seriesId = id,
                seriesTitle = title,
                description = item.optString("plot", "").takeIf { it.isNotBlank() && it != "null" },
                year = item.optString("releaseDate", item.optString("year", "")).take(4).takeIf { it.isNotBlank() && it != "null" }
            )
        }
    }

    require(output.isNotEmpty()) {
        val detail = endpointWarnings.joinToString(" • ").ifBlank { "O servidor não retornou conteúdo nos endpoints Xtream." }
        "Nenhum canal, filme ou série foi recebido. $detail Confira o endereço, usuário, senha e permissões da conta."
    }
    val warning = endpointWarnings.takeIf { it.isNotEmpty() }?.joinToString(" • ")
    return SourceLoadResult(output, xtreamEpgUrl(source), warning)
}

private fun loadXtreamEpisodes(source: PlaylistSource, series: Channel): List<Channel> {
    val seriesId = series.seriesId ?: error("Identificador da série ausente.")
    val server = source.location.trimEnd('/')
    val query = "username=${encodeQuery(source.username)}&password=${encodeQuery(source.password)}&action=get_series_info&series_id=${encodeQuery(seriesId)}"
    val root = JSONObject(fetchText("$server/player_api.php?$query"))
    val episodesObject = root.optJSONObject("episodes")
    val output = mutableListOf<Channel>()
    if (episodesObject != null) {
        val seasonKeys = episodesObject.keys().asSequence().toList().sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }
        seasonKeys.forEach { season ->
            val array = episodesObject.optJSONArray(season) ?: return@forEach
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                if (id.isBlank()) continue
                val number = item.optString("episode_num", (i + 1).toString())
                val title = item.optString("title", "Episódio $number")
                val extension = item.optString("container_extension", "mp4").ifBlank { "mp4" }
                output += Channel(
                    id = "${source.id}:episode:$id",
                    name = "E$number • $title",
                    url = "$server/series/${encodePathSegment(source.username)}/${encodePathSegment(source.password)}/$id.$extension",
                    group = "Temporada $season • ${series.name}",
                    logo = null,
                    tvgId = null,
                    sourceId = source.id,
                    kind = "episode",
                    seriesId = seriesId,
                    seriesTitle = series.name,
                    seasonNumber = season,
                    episodeNumber = number,
                    description = item.optString("info", "").takeIf { it.isNotBlank() && it != "null" }
                )
            }
        }
    }
    if (episodesObject == null) {
        val array = root.optJSONArray("episodes")
        if (array != null) {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                if (id.isBlank()) continue
                val extension = item.optString("container_extension", "mp4").ifBlank { "mp4" }
                output += Channel(
                    id = "${source.id}:episode:$id",
                    name = item.optString("title", "Episódio ${i + 1}"),
                    url = "$server/series/${encodePathSegment(source.username)}/${encodePathSegment(source.password)}/$id.$extension",
                    group = series.name,
                    logo = null,
                    tvgId = null,
                    sourceId = source.id,
                    kind = "episode",
                    seriesId = seriesId,
                    seriesTitle = series.name,
                    seasonNumber = "1",
                    episodeNumber = (i + 1).toString()
                )
            }
        }
    }
    return output
}

private fun epgKey(channel: Channel): String = (channel.tvgId?.takeIf { it.isNotBlank() } ?: channel.id).lowercase()

private fun loadXtreamShortEpgBatch(
    channels: List<Channel>,
    sourcesById: Map<String, PlaylistSource>
): List<EpgProgram> {
    val executor = Executors.newFixedThreadPool(6)
    return try {
        val futures: List<Future<List<EpgProgram>>> = channels.mapNotNull { channel ->
            val source = sourcesById[channel.sourceId] ?: return@mapNotNull null
            executor.submit(Callable<List<EpgProgram>> {
                runCatching { loadXtreamShortEpg(source, channel) }.getOrDefault(emptyList())
            })
        }
        futures.flatMap { future ->
            runCatching { future.get(18, TimeUnit.SECONDS) }.getOrDefault(emptyList())
        }
    } finally {
        executor.shutdownNow()
    }
}

private fun loadXtreamShortEpg(source: PlaylistSource, channel: Channel): List<EpgProgram> {
    val streamId = channel.id.substringAfterLast(':')
    val server = source.location.trimEnd('/')
    val query = "username=${encodeQuery(source.username)}&password=${encodeQuery(source.password)}&action=get_short_epg&stream_id=${encodeQuery(streamId)}&limit=2"
    val root = JSONObject(fetchText("$server/player_api.php?$query", connectTimeoutMs = 8_000, readTimeoutMs = 10_000))
    val listings = root.optJSONArray("epg_listings") ?: root.optJSONArray("data") ?: return emptyList()
    return buildList {
        for (index in 0 until listings.length()) {
            val item = listings.optJSONObject(index) ?: continue
            val start = item.optLong("start_timestamp", 0L).let { if (it > 0L) it * 1000L else parseXmltvDate(item.optString("start", "")) }
            val end = item.optLong("stop_timestamp", item.optLong("end_timestamp", 0L)).let { if (it > 0L) it * 1000L else parseXmltvDate(item.optString("end", "")) }
            val title = decodeXtreamText(item.optString("title", ""))
            if (start > 0L && end > start && title.isNotBlank()) {
                add(
                    EpgProgram(
                        channelId = epgKey(channel),
                        title = title,
                        description = decodeXtreamText(item.optString("description", "")),
                        startMs = start,
                        endMs = end
                    )
                )
            }
        }
    }
}

private fun decodeXtreamText(value: String): String {
    if (value.isBlank()) return value
    val decoded = runCatching { String(Base64.decode(value, Base64.DEFAULT), Charsets.UTF_8).trim() }.getOrNull()
    return decoded?.takeIf {
        it.isNotBlank() && it.any(Char::isLetterOrDigit) && '\uFFFD' !in it && it.none { char -> char.isISOControl() }
    } ?: value
}

private fun fetchText(address: String, connectTimeoutMs: Int = 20_000, readTimeoutMs: Int = 60_000): String {
    val request = Request.Builder()
        .url(address)
        .header("User-Agent", "NTPlaytree/1.7 AndroidTV")
        .header("Accept", "application/json, application/xml, text/xml, */*")
        .get()
        .build()
    val totalTimeoutMs = (connectTimeoutMs + readTimeoutMs).coerceIn(8_000, 30_000)
    val client = PlaybackNetwork.apiClient.newBuilder()
        .connectTimeout(connectTimeoutMs.toLong(), TimeUnit.MILLISECONDS)
        .readTimeout(readTimeoutMs.toLong(), TimeUnit.MILLISECONDS)
        .callTimeout(totalTimeoutMs.toLong(), TimeUnit.MILLISECONDS)
        .build()
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) error("O servidor respondeu HTTP ${response.code}.")
        return response.body?.string() ?: ""
    }
}

private fun xtreamEpgUrl(source: PlaylistSource): String? {
    if (source.type != "xtream" || source.username.isBlank() || source.password.isBlank()) return null
    return "${source.location.trimEnd('/')}/xmltv.php?username=${encodeQuery(source.username)}&password=${encodeQuery(source.password)}"
}

private fun encodeQuery(value: String): String = URLEncoder.encode(value, "UTF-8")
private fun encodePathSegment(value: String): String = encodeQuery(value).replace("+", "%20")

private fun fetchXmltv(address: String): List<EpgProgram> {
    val request = Request.Builder()
        .url(address)
        .header("User-Agent", "NTPlaytree/1.7 AndroidTV")
        .header("Accept", "application/xml, text/xml, */*")
        .header("Accept-Encoding", "identity")
        .get()
        .build()
    PlaybackNetwork.epgClient.newCall(request).execute().use { response ->
        require(response.isSuccessful) { "O servidor respondeu HTTP ${response.code} ao baixar o guia XMLTV." }
        val body = response.body ?: error("O servidor retornou um XMLTV vazio.")
        return body.charStream().buffered().use { reader -> parseXmltv(reader) }
    }
}

private fun parseXmltv(xml: String): List<EpgProgram> = parseXmltv(StringReader(xml))

private fun parseXmltv(reader: Reader): List<EpgProgram> {
    val parser = Xml.newPullParser()
    parser.setInput(reader)
    val programs = ArrayList<EpgProgram>()
    val now = System.currentTimeMillis()
    val windowStart = now - 6L * 60L * 60L * 1000L
    val windowEnd = now + 48L * 60L * 60L * 1000L
    val maxPrograms = 12_000

    var inProgramme = false
    var channelId = ""
    var startMs = 0L
    var endMs = 0L
    var title = ""
    var description = ""
    var event = parser.eventType

    while (event != XmlPullParser.END_DOCUMENT && programs.size < maxPrograms) {
        when (event) {
            XmlPullParser.START_TAG -> when (parser.name.lowercase(Locale.ROOT)) {
                "programme" -> {
                    inProgramme = true
                    channelId = parser.getAttributeValue(null, "channel") ?: ""
                    startMs = parseXmltvDate(parser.getAttributeValue(null, "start").orEmpty())
                    endMs = parseXmltvDate(parser.getAttributeValue(null, "stop").orEmpty())
                    title = ""
                    description = ""
                }
                "title" -> if (inProgramme) title = parser.nextText().trim()
                "desc" -> if (inProgramme) description = parser.nextText().trim()
            }
            XmlPullParser.END_TAG -> if (parser.name.equals("programme", true)) {
                if (
                    channelId.isNotBlank() &&
                    title.isNotBlank() &&
                    endMs > startMs &&
                    endMs > windowStart &&
                    startMs < windowEnd
                ) {
                    programs.add(EpgProgram(channelId, title, description, startMs, endMs))
                }
                inProgramme = false
            }
        }
        event = parser.next()
    }
    return programs
}

private fun parseXmltvDate(value: String): Long {
    if (value.isBlank()) return 0L
    val formats = listOf("yyyyMMddHHmmss Z", "yyyyMMddHHmmss")
    for (pattern in formats) {
        val parsed = runCatching {
            SimpleDateFormat(pattern, Locale.US).apply { isLenient = true }.parse(value.trim())?.time
        }.getOrNull()
        if (parsed != null) return parsed
    }
    return 0L
}

private fun formatClock(timeMs: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timeMs))
private fun formatHistoryDate(timeMs: Long): String = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(timeMs))
private fun formatDuration(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds) else "%02d:%02d".format(minutes, seconds)
}

private fun touchHistory(current: List<PlaybackHistory>, channel: Channel): List<PlaybackHistory> {
    if (channel.url.isBlank()) return current
    val previous = current.firstOrNull { it.channel.url == channel.url }
    val entry = PlaybackHistory(
        channel = channel,
        positionMs = previous?.positionMs ?: 0L,
        durationMs = previous?.durationMs ?: 0L,
        playedAtMs = System.currentTimeMillis()
    )
    return listOf(entry) + current.filterNot { it.channel.url == channel.url }.take(49)
}

private fun updateHistory(
    current: List<PlaybackHistory>,
    channel: Channel,
    positionMs: Long,
    durationMs: Long
): List<PlaybackHistory> {
    if (channel.url.isBlank()) return current
    val live = channel.kind == "live"
    val previous = current.firstOrNull { it.channel.url == channel.url }
    val entry = PlaybackHistory(
        channel = channel,
        positionMs = if (live) 0L else positionMs.coerceAtLeast(0L),
        durationMs = if (live) 0L else durationMs.coerceAtLeast(0L),
        playedAtMs = previous?.playedAtMs ?: System.currentTimeMillis()
    )
    return listOf(entry) + current.filterNot { it.channel.url == channel.url }.take(49)
}

private fun resolveStreamUrl(line: String, source: PlaylistSource): String {
    val candidate = line.trim()
    if (Regex("^[A-Za-z][A-Za-z0-9+.-]*:").containsMatchIn(candidate)) return candidate
    if (source.isLocalFile) return candidate
    if (candidate.startsWith("//")) {
        val scheme = runCatching { URL(source.location).protocol }.getOrDefault("http")
        return "$scheme:$candidate"
    }
    return runCatching { URL(URL(source.location), candidate).toString() }
        .getOrElse { runCatching { URI(source.location).resolve(candidate).toString() }.getOrDefault(candidate) }
}

private fun readSources(context: Context): List<PlaylistSource> {
    val raw = context.getSharedPreferences("ntplaytree_local", Context.MODE_PRIVATE)
        .getString("sources", "[]") ?: "[]"
    return runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    PlaylistSource(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        location = item.getString("location"),
                        isLocalFile = item.optBoolean("local", false),
                        type = item.optString("type", "m3u"),
                        username = item.optString("username", ""),
                        password = item.optString("password", "")
                    )
                )
            }
        }
    }.getOrDefault(emptyList())
}

private fun writeSources(context: Context, sources: List<PlaylistSource>) {
    val array = JSONArray()
    sources.forEach { source ->
        array.put(
            JSONObject().apply {
                put("id", source.id)
                put("name", source.name)
                put("location", source.location)
                put("local", source.isLocalFile)
                put("type", source.type)
                put("username", source.username)
                put("password", source.password)
            }
        )
    }
    context.getSharedPreferences("ntplaytree_local", Context.MODE_PRIVATE)
        .edit().putString("sources", array.toString()).apply()
}

private fun readFavorites(context: Context): Set<String> {
    return context.getSharedPreferences("ntplaytree_local", Context.MODE_PRIVATE)
        .getStringSet("favorites", emptySet())?.toSet() ?: emptySet()
}

private fun writeFavorites(context: Context, favorites: Set<String>) {
    context.getSharedPreferences("ntplaytree_local", Context.MODE_PRIVATE)
        .edit().putStringSet("favorites", favorites.toSet()).apply()
}

private fun readHistory(context: Context): List<PlaybackHistory> {
    val raw = context.getSharedPreferences("ntplaytree_local", Context.MODE_PRIVATE)
        .getString("history", "[]") ?: "[]"
    return runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val channelJson = item.optJSONObject("channel") ?: continue
                val channel = Channel(
                    id = channelJson.optString("id", ""),
                    name = channelJson.optString("name", "Conteúdo"),
                    url = channelJson.optString("url", ""),
                    group = channelJson.optString("group", ""),
                    logo = channelJson.optString("logo", "").takeIf { it.isNotBlank() && it != "null" },
                    tvgId = channelJson.optString("tvgId", "").takeIf { it.isNotBlank() && it != "null" },
                    sourceId = channelJson.optString("sourceId", ""),
                    kind = channelJson.optString("kind", "live"),
                    seriesId = channelJson.optString("seriesId", "").takeIf { it.isNotBlank() && it != "null" },
                    seriesTitle = channelJson.optString("seriesTitle", "").takeIf { it.isNotBlank() && it != "null" },
                    seasonNumber = channelJson.optString("seasonNumber", "").takeIf { it.isNotBlank() && it != "null" },
                    episodeNumber = channelJson.optString("episodeNumber", "").takeIf { it.isNotBlank() && it != "null" },
                    description = channelJson.optString("description", "").takeIf { it.isNotBlank() && it != "null" },
                    year = channelJson.optString("year", "").takeIf { it.isNotBlank() && it != "null" }
                )
                if (channel.url.isNotBlank()) {
                    add(
                        PlaybackHistory(
                            channel = channel,
                            positionMs = item.optLong("position", 0L),
                            durationMs = item.optLong("duration", 0L),
                            playedAtMs = item.optLong("playedAt", 0L)
                        )
                    )
                }
            }
        }
    }.getOrDefault(emptyList())
}

private fun writeHistory(context: Context, history: List<PlaybackHistory>) {
    val array = JSONArray()
    history.take(50).forEach { entry ->
        val channel = entry.channel
        val channelJson = JSONObject().apply {
            put("id", channel.id)
            put("name", channel.name)
            put("url", channel.url)
            put("group", channel.group)
            put("logo", channel.logo)
            put("tvgId", channel.tvgId)
            put("sourceId", channel.sourceId)
            put("kind", channel.kind)
            put("seriesId", channel.seriesId)
            put("seriesTitle", channel.seriesTitle)
            put("seasonNumber", channel.seasonNumber)
            put("episodeNumber", channel.episodeNumber)
            put("description", channel.description)
            put("year", channel.year)
        }
        array.put(
            JSONObject().apply {
                put("channel", channelJson)
                put("position", entry.positionMs)
                put("duration", entry.durationMs)
                put("playedAt", entry.playedAtMs)
            }
        )
    }
    context.getSharedPreferences("ntplaytree_local", Context.MODE_PRIVATE)
        .edit().putString("history", array.toString()).apply()
}
