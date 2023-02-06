package com.dogeby.tagplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dogeby.tagplayer.database.model.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTags(entities: List<TagEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTags(entities: List<TagEntity>): Int

    @Query(
        value = """
            UPDATE tags
            SET name = :name
            WHERE id is :id
        """,
    )
    suspend fun modifyTagName(id: Long, name: String)

    @Query(value = "DELETE FROM tags")
    suspend fun deleteTags(): Int

    @Query(
        value = """
            DELETE FROM tags
            WHERE id IN (:ids)
        """,
    )
    suspend fun deleteTags(ids: List<Long>): Int

    @Query(
        value = """
        SELECT * FROM tags
        ORDER BY name
    """,
    )
    fun getTagEntities(): Flow<List<TagEntity>>

    @Query(
        value = """
            SELECT * FROM tags
            WHERE id IN (:ids)
            ORDER BY name
        """,
    )
    fun getTagEntities(ids: List<Long>): Flow<List<TagEntity>>

    @Query(
        value = """
            SELECT * FROM tags
            WHERE name LIKE '%'||:nameKeyword||'%'
            ORDER BY name
        """,
    )
    fun getTagEntities(nameKeyword: String): Flow<List<TagEntity>>
}
