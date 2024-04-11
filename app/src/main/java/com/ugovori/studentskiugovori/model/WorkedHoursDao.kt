package com.ugovori.studentskiugovori.model

import com.ugovori.studentskiugovori.model.dataclasses.WorkedHours
import com.ugovori.studentskiugovori.model.dataclasses.WorkedHoursRealm

import io.realm.Realm
import io.realm.RealmResults
import java.util.*

fun Realm.getWorkedHours() : WorkedHoursDao = WorkedHoursDao(this)

class WorkedHoursDao (private val realm: Realm) {

    fun addToDb(workedHour: WorkedHours) {
        realm.executeTransactionAsync {
            val item = WorkedHoursRealm(
                workedHour.id,
                workedHour.date.toEpochDay(),
                "${workedHour.timeStart.hour}:${ workedHour.timeStart.minute }",
                "${workedHour.timeEnd.hour}:${ workedHour.timeEnd.minute }",
                workedHour.moneyEarned.toDouble(),
                workedHour.hours.toDouble(),
                workedHour.hourlyPay.toDouble(),
                workedHour.completed
            )
            it.insert(item)
        }
    }

    fun getWorkedHours(): RealmResults<WorkedHoursRealm> {
        return realm.where(WorkedHoursRealm::class.java).findAllAsync()
    }

    fun deleteWorkedHoursFromDb(id: UUID) {
        realm.executeTransactionAsync {
            val result = it.where(WorkedHoursRealm::class.java).equalTo("id", id).findFirst()
            result?.deleteFromRealm()
        }
    }

    fun deleteAll(workedHoursRealm: WorkedHoursRealm) {
        realm.executeTransactionAsync {
            val result = it.where(WorkedHoursRealm::class.java).equalTo("id", workedHoursRealm.id) .findAll()
            result.deleteAllFromRealm()
        }
    }

    fun clearDb(){
        realm.executeTransactionAsync {
            val result = it.where(WorkedHoursRealm::class.java).findAll()
            result.deleteAllFromRealm()
        }
    }

}