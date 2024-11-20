package com.jorge.lab3_ex1.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo("city")
    var cityName: String



) {

}