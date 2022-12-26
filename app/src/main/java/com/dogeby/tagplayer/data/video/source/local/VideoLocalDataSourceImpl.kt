package com.dogeby.tagplayer.data.video.source.local

import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

@Singleton
class VideoLocalDataSourceImpl @Inject constructor(
    private val contextResolver: ContentResolver,
) : VideoLocalDataSource {

    private val _videoDataList = MutableStateFlow<List<VideoData>>(emptyList())
    override val videoDataList: StateFlow<List<VideoData>> = _videoDataList

    override suspend fun updateVideoDataList() = runCatching {
        val tmpVideoDataList = mutableListOf<VideoData>()

        withContext(Dispatchers.IO) {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL,
                    )
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.RELATIVE_PATH
                } else {
                    MediaStore.Video.Media.DATA
                },
                MediaStore.Video.Media.SIZE,
            )
            val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

            contextResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val pathColumn =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
                    } else {
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    }
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                while (cursor.moveToNext()) {
                    tmpVideoDataList.add(
                        VideoData(
                            id = cursor.getLong(idColumn),
                            name = cursor.getString(nameColumn),
                            duration = cursor.getInt(durationColumn),
                            parentFolder = formatPathToParentFolder(cursor.getString(pathColumn)),
                            size = cursor.getInt(sizeColumn),
                        ),
                    )
                }
            }
            _videoDataList.value = tmpVideoDataList
        }
    }

    private fun formatPathToParentFolder(path: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path.dropLast(1)
        } else {
            path.split('/').run {
                if (lastIndex <= 0) "" else get(lastIndex - 1)
            }
        }
    }
}
