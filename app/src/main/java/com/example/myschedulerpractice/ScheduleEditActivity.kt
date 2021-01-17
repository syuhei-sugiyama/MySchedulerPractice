package com.example.myschedulerpractice

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleEditActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        // Realmのインスタンス取得
        realm = Realm.getDefaultInstance()

        // IntentクラスのgetLongExtraメソッドを使い、MainActivityで格納した「schedule_id」の値を取り出し
        // 取得できなかった場合はデフォルトで「-1」を取得
        // つまり、-1なら新規登録、それ以外は更新
        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        // 更新の場合
        if (scheduleId != -1L) {
            /**
             * インテントから取得したscheduleIdを条件に、equalToメソッドにより、比較するカラム名をidカラムとしてScheduleモデルを検索
             * findFirstメソッドにより、検索結果のうち、最初のオブジェクト(Scheduleクラスのインスタンス(=1レコード))を取得
              */
            val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()
            /**
             * 取得したScheduleクラスのインスタンスから、詳細画面の各ビューへ表示するために、それぞれのカラムの値をセットする
             * なので、更新時は、もともと登録していた内容がセットされた状態で、詳細画面が表示される
             */
            dateEdit.setText(
                DateFormat.format("yyyy/MM/dd", schedule?.date))
            titleEdit.setText(schedule?.title)
            detailEdit.setText(schedule?.detail)
            // visibilityプロパティを利用
            // View.VISIBLEで表示、View.INVISIBLEで非表示
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.INVISIBLE
        }

        // 「保存」ボタンが押下された時の処理
        save.setOnClickListener {
            when (scheduleId) {
                // 新規登録時
                -1L -> {
                    /**
                     * DBへの書き込みには、トランザクションを使用する
                     * executeTransaction・・・トランザクションを実行する
                     */
                    realm.executeTransaction {
                        // RealmQueryクラスのmaxメソッドを使用し、Scheduleモデルのidカラムの最大値を取得
                        val maxId = realm.where<Schedule>().max("id")
                        // maxId + 1することで、追加するレコードのidが常に最大値となるようにする
                        // エルビス演算子(?:)により、maxIdがnullの場合はLong型の0を取得
                        val nextId = (maxId?.toLong() ?: 0L) + 1
                        // RealmインスタンスのcreateObjectメソッドを使用して、データを1行追加する
                        // この時、idカラムに、nextIdの値を持ったScheduleクラスのインスタンスを受け取る
                        // 受け取ったScheduleクラスのインスタンスに、他のカラムの値を設定する
                        val schedule = realm.createObject<Schedule>(nextId)
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            schedule.date = it
                        }
                        schedule.title = titleEdit.text.toString()
                        schedule.detail = detailEdit.text.toString()
                    }
                    // Ankoのalert関数を使用
                    alert("追加しました") {
                        // OKボタンを押下した時の処理を{}内に記述
                        yesButton { finish() }
                    }.show()
                }
                else -> {
                    // 更新時
                    realm.executeTransaction {
                        /**
                         * Scheduleモデルにて、対象カラムがidカラムで、インテントから取得したscheduleIdを条件にして検索
                         * 検索結果のうち、一番最初の、Scheduleクラスのインスタンスを取得
                         */
                        val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()
                        /**
                         * 取得したScheduleクラスのインスタンスへ、詳細画面にて入力された内容を格納していく
                         */
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            schedule?.date = it
                        }
                        schedule?.title = titleEdit.text.toString()
                        schedule?.detail = detailEdit.text.toString()
                    }
                    // 更新完了時、「更新しました」という文言のダイアログ表示
                    alert("更新しました") {
                        // OKボタンが押下された時の処理
                        yesButton { finish() }
                    }.show()
                }
            }
        }

        delete.setOnClickListener {
            realm.executeTransaction {
                // レコード削除には、RealmObjectクラスのdeleteFromRealmを使用
                realm.where<Schedule>().equalTo("id", scheduleId)
                    ?.findFirst()?.deleteFromRealm()
            }
            alert("削除しました") {
                yesButton { finish() }
            }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e: IllegalArgumentException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                null
            }
        }
        return date
    }
}