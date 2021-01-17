package com.example.myschedulerpractice

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * データを格納する、モデルクラス
 * メンバ変数・・・DBのフィールド(カラム、列)
 * このモデルクラスの１つのインスタンス・・・DBのレコード(行)
 * 他のクラスから継承できるようにopen修飾子付与
 */
open class Schedule : RealmObject() {
    // カラム「id」は一意にするため、@PrimaryKeyアノテーション付与
    // スケジュール１つ１つを連番で管理するためのid
    @PrimaryKey
    var id: Long = 0
    // 日付
    var date: Date = Date()
    // タイトル
    var title: String = ""
    // 詳細内容
    var detail: String = ""
}