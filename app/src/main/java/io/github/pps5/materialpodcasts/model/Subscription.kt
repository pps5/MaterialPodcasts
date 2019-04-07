package io.github.pps5.materialpodcasts.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "subscription")
data class Subscription(@PrimaryKey var collectionId: Long)