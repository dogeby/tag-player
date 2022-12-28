package com.dogeby.tagplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dogeby.tagplayer.database.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(entities: List<VideoEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateVideos(entities: List<VideoEntity>): Int

    @Query(value = "DELETE FROM videos")
    suspend fun deleteVideos(): Int

    @Query(
        value = """
            DELETE FROM videos
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteVideos(ids: List<Long>): Int

    @Query(
        value = """
            SELECT * FROM videos
            ORDER BY name
        """,
    )
    fun getVideoEntities(): Flow<List<VideoEntity>>
}
