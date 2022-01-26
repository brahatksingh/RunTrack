package com.brahatksingh.runtrack.Repositories

import com.brahatksingh.runtrack.db.Run
import com.brahatksingh.runtrack.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDAO: RunDAO
){
    suspend fun insertRun(run : Run) = runDAO.insertRun(run)

    suspend fun deleteRun(run : Run) = runDAO.deleteRun(run)

    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDAO.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis() = runDAO.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDAO.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()

    fun getTotalDistance() = runDAO.getTotalDistance()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()

    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()
}