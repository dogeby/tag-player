package com.dogeby.tagplayer.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.dogeby.tagplayer.VideoListPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class VideoListPreferencesSerializer @Inject constructor() : Serializer<VideoListPreferences> {

    override val defaultValue: VideoListPreferences = VideoListPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): VideoListPreferences =
        try {
            VideoListPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: VideoListPreferences, output: OutputStream) {
        return t.writeTo(output)
    }
}
