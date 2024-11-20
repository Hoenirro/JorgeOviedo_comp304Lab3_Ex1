package com.jorge.lab3_ex1.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [City::class] , version = 1)
abstract class CityDataBase : RoomDatabase(){
    abstract fun getCityDao() : CityDAO


    // multi-threading
    companion object{
        @Volatile
        private var INSTANCE : CityDataBase? = null
        fun getInstance(context: Context): CityDataBase {
            synchronized(this){//no  race conditions
                var inst = INSTANCE
                if (inst == null){
                    inst = Room.databaseBuilder(context,
                        CityDataBase::class.java,
                        "citiesDB").
                    build()
                }
                INSTANCE = inst
                return inst
            }


        }


    }


}