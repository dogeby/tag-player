package com.dogeby.tagplayer.datastore.app

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.dogeby.tagplayer.AppPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AppPreferencesSerializer @Inject constructor() : Serializer<AppPreferences> {

    override val defaultValue: AppPreferences
        get() = AppPreferences.getDefaultInstance()
            .toBuilder()
            .setAppThemeMode(AppThemeMode.SYSTEM_SETTING.ordinal)
            .setAutoRotation(false)
            .build()

    override suspend fun readFrom(input: InputStream): AppPreferences {
        return try {
            AppPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AppPreferences, output: OutputStream) {
        return t.writeTo(output)
    }
}
