package com.dogeby.tagplayer.data.video.source.local

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class VideoLocalDataSourceImpl @Inject constructor(
    @ApplicationContext appContext: Context,
) : VideoLocalDataSource {

    private val contextResolver = appContext.contentResolver
    override val contentUri: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }.toString()

    override suspend fun getVideoDataList() = runCatching {
        val tmpVideoDataList = mutableListOf<VideoData>()

        withContext(Dispatchers.IO) {
            val collection = Uri.parse(contentUri)

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
                            fileName = cursor.getString(nameColumn),
                            duration = cursor.getInt(durationColumn),
                            path = cursor.getString(pathColumn),
                            size = cursor.getLong(sizeColumn),
                        ),
                    )
                }
            }
            tmpVideoDataList
        }
    }
}
