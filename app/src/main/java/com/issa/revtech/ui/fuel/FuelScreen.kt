package com.issa.revtech.ui.fuel

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview

private val ChuckyRed = Color(0xFFFF3030)
private val ChuckyBlack = Color(0xFF101012)
private val ChuckyCard = Color(0xFF1C1C21)
private val ChuckyMuted = Color(0xFFAAAAAF)

private data class LocalTrack(
    val title: String,
    val artist: String,
    val uri: Uri
)

@Composable
fun FuelScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var tracks by remember { mutableStateOf<List<LocalTrack>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Your soundtrack. Your ride.") }
    var playbackPosition by remember { mutableLongStateOf(0L) }
    var trackDuration by remember { mutableLongStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }
    var isShuffleEnabled by rememberSaveable { mutableStateOf(false) }
    var isRepeatEnabled by rememberSaveable { mutableStateOf(false) }

    val player = remember { mutableStateOf<MediaPlayer?>(null) }

    val audioPermission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    fun loadTracks() {
        if (
            ContextCompat.checkSelfPermission(context, audioPermission) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            status = "Allow audio access to load your music"
            return
        }

        val found = mutableListOf<LocalTrack>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )

        try {
            context.contentResolver.query(
                collection,
                projection,
                "${MediaStore.Audio.Media.IS_MUSIC} != 0",
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )?.use { cursor ->
                val idColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                        ?.takeIf { it.isNotBlank() } ?: "Unknown track"
                    val artist = cursor.getString(artistColumn)
                        ?.takeIf { it.isNotBlank() && it != "<unknown>" }
                        ?: "Unknown artist"

                    found.add(
                        LocalTrack(
                            title = title,
                            artist = artist,
                            uri = ContentUris.withAppendedId(collection, id)
                        )
                    )
                }
            }

            tracks = found

            status = if (found.isEmpty()) {
                "No local music found on this phone"
            } else {
                "${found.size} tracks available offline"
            }

            if (currentIndex >= found.size) {
                currentIndex = -1
            }
        } catch (_: Exception) {
            status = "Couldn't read music files"
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            loadTracks()
        } else {
            status = "Audio permission was not granted"
        }
    }

    // Automatically reload the library after screen recreation or rotation.
    LaunchedEffect(Unit) {
        if (
            ContextCompat.checkSelfPermission(context, audioPermission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            loadTracks()
        }
    }

    fun playTrack(index: Int) {
        if (index !in tracks.indices) return

        try {
            player.value?.release()
            player.value = null
            isPlaying = false
            playbackPosition = 0L
            trackDuration = 0L

            val newPlayer = MediaPlayer()

            newPlayer.setDataSource(context, tracks[index].uri)

            newPlayer.setOnCompletionListener {
                if (isRepeatEnabled) {
                    try {
                        it.seekTo(0)
                        it.start()
                        isPlaying = true
                        playbackPosition = 0L
                    } catch (_: IllegalStateException) {
                        isPlaying = false
                    }
                } else if (tracks.isNotEmpty()) {
                    val nextIndex = if (
                        isShuffleEnabled && tracks.size > 1
                    ) {
                        (tracks.indices.filter { it != index }).random()
                    } else {
                        (index + 1) % tracks.size
                    }

                    playTrack(nextIndex)
                } else {
                    isPlaying = false
                }
            }

            newPlayer.prepare()
            newPlayer.start()

            player.value = newPlayer
            currentIndex = index
            trackDuration = newPlayer.duration.toLong().coerceAtLeast(0L)
            playbackPosition = 0L
            isPlaying = true
            status = "NOW PLAYING OFFLINE"
        } catch (_: Exception) {
            player.value?.release()
            player.value = null
            isPlaying = false
            status = "Couldn't play this track"
        }
    }

    // Update the seek bar while a song plays.
    LaunchedEffect(isPlaying, currentIndex, player.value) {
        while (isPlaying) {
            val activePlayer = player.value ?: break

            try {
                if (!isSeeking && activePlayer.isPlaying) {
                    playbackPosition =
                        activePlayer.currentPosition.toLong()
                    trackDuration =
                        activePlayer.duration.toLong().coerceAtLeast(0L)
                }
            } catch (_: IllegalStateException) {
                break
            }

            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.value?.release()
            player.value = null
        }
    }

    fun formatTime(milliseconds: Long): String {
        val totalSeconds = (milliseconds / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return "%d:%02d".format(minutes, seconds)
    }

    val currentTrack = tracks.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChuckyBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CHUCKY",
                    color = ChuckyRed,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    fontSize = 24.sp
                )

                Text(
                    text = "// MUSIC",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(ChuckyCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "Music library",
                    tint = ChuckyRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = "YOUR RIDE. YOUR SOUND.",
            color = ChuckyMuted,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium
        )

        // Album-art placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF481313),
                            Color(0xFF211116),
                            Color(0xFF101012)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(138.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF17171B)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF5B2025),
                                        Color(0xFF242126),
                                        Color(0xFF0D0D0F)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Album artwork placeholder",
                            tint = ChuckyRed,
                            modifier = Modifier.size(58.dp)
                        )
                    }
                }

                Text(
                    text = "CHUCKY // SOUNDTRACK",
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 3.sp,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "MADE FOR THE ROAD",
                    color = ChuckyMuted,
                    letterSpacing = 2.sp,
                    fontSize = 9.sp
                )
            }
        }

        // Current track
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currentTrack?.title ?: "Choose your soundtrack",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = currentTrack?.artist ?: status,
                    color = ChuckyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { status = "Favourites coming soon" }
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Add to favourites",
                    tint = ChuckyRed
                )
            }
        }

        // Seek bar
        Column {
            Slider(
                value = playbackPosition.toFloat().coerceIn(
                    0f,
                    trackDuration.coerceAtLeast(1L).toFloat()
                ),
                onValueChange = { newPosition ->
                    isSeeking = true
                    playbackPosition = newPosition.toLong()
                },
                onValueChangeFinished = {
                    try {
                        player.value?.seekTo(playbackPosition.toInt())
                    } catch (_: IllegalStateException) {
                        // Player may have been released.
                    }
                    isSeeking = false
                },
                valueRange = 0f..trackDuration.coerceAtLeast(1L).toFloat(),
                enabled = player.value != null && trackDuration > 0L,
                colors = SliderDefaults.colors(
                    thumbColor = ChuckyRed,
                    activeTrackColor = ChuckyRed,
                    inactiveTrackColor = Color.DarkGray
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(playbackPosition),
                    color = ChuckyMuted,
                    fontSize = 11.sp
                )

                Text(
                    text = formatTime(trackDuration),
                    color = ChuckyMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    isShuffleEnabled = !isShuffleEnabled
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Toggle shuffle",
                    tint = if (isShuffleEnabled) ChuckyRed else ChuckyMuted
                )
            }

            IconButton(
                onClick = {
                    if (tracks.isNotEmpty()) {
                        val previous = if (currentIndex <= 0) {
                            tracks.lastIndex
                        } else {
                            currentIndex - 1
                        }
                        playTrack(previous)
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous track",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Button(
                onClick = {
                    val active = player.value

                    if (active != null && currentIndex >= 0) {
                        try {
                            if (isPlaying && active.isPlaying) {
                                active.pause()
                                isPlaying = false
                            } else {
                                active.start()
                                isPlaying = true
                            }
                        } catch (_: IllegalStateException) {
                            if (currentIndex in tracks.indices) {
                                playTrack(currentIndex)
                            }
                        }
                    } else if (tracks.isNotEmpty()) {
                        val indexToPlay =
                            currentIndex.takeIf { it in tracks.indices } ?: 0
                        playTrack(indexToPlay)
                    } else {
                        status = "Load your music library below"
                    }
                },
                modifier = Modifier.size(66.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChuckyRed
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            IconButton(
                onClick = {
                    if (tracks.isNotEmpty()) {
                        val next =
                            if (currentIndex >= tracks.lastIndex) 0
                            else currentIndex + 1
                        playTrack(next)
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next track",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = {
                    isRepeatEnabled = !isRepeatEnabled
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Toggle repeat",
                    tint = if (isRepeatEnabled) ChuckyRed else ChuckyMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FuelScreenPreview() {
    FuelScreen(
        navController = androidx.navigation.compose.rememberNavController()
    )
}










