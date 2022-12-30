package com.dogeby.tagplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dogeby.tagplayer.database.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VideoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertVideos(entities: List<VideoEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun updateVideos(entities: List<VideoEntity>): Int

    @Transaction
    open suspend fun cacheVideos(entities: List<VideoEntity>) {
        deleteVideosNotInIds(entities.map { it.id })
        updateVideos(entities)
        insertVideos(entities)
    }

    @Query(value = "DELETE FROM videos")
    abstract suspend fun deleteVideos(): Int

    @Query(
        value = """
            DELETE FROM videos
            WHERE id IN (:ids)
        """,
    )
    abstract suspend fun deleteVideos(ids: List<Long>): Int

    @Query(
        value = """
            DELETE FROM videos
            WHERE id NOT IN (:ids)
        """
    )
    abstract suspend fun deleteVideosNotInIds(ids: List<Long>)

    @Query(
        value = """
            SELECT * FROM videos
            ORDER BY name
        """,
    )
    abstract fun getVideoEntities(): Flow<List<VideoEntity>>
}
