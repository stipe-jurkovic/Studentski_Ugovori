package com.example.studentskiugovori.model.dataclasses

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

open class WorkedHoursRealm(
    var id: UUID = UUID.randomUUID(),
    var date: Long,
    var timeStart: String,
    var timeEnd: String,
    var moneyEarned: Double,
    var hours: Double = 0.0,
    var hourlyPay: Double,
    var completed: Boolean = true,
) : RealmObject() {
    constructor() : this(
        UUID.randomUUID(),
        LocalDate.now().toEpochDay(),
        "${LocalTime.now().hour}:${LocalTime.now().minute}",
        "${LocalTime.now().hour}:${LocalTime.now().minute}",
        0.0,
        0.0,
        0.0,
        false
    )
}


class RealmLiveData<T : RealmModel>(val realmResults: RealmResults<T>) :
    LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        realmResults.addChangeListener(listener)
    }

    override fun onInactive() {
        realmResults.removeChangeListener(listener)
    }
}