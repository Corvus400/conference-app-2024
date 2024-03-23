package io.github.droidkaigi.confsched.model

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    fun getTimetableStream(): Flow<Timetable>
    fun getTimetableItemWithBookmarkStream(id: TimetableItemId): Flow<Pair<TimetableItem, Boolean>>
    @Composable
    fun timetable(): Timetable

    @Composable
    fun timetableItemWithBookmark(id: TimetableItemId): Pair<TimetableItem, Boolean>?
    suspend fun toggleBookmark(id: TimetableItemId)
}
