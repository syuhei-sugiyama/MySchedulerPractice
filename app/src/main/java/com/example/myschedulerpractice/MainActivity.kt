package com.example.myschedulerpractice

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    // Realmクラスのプロパティ
    // 後からonCreate内で初期化するため、lateinit修飾子付与
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        // Realmクラスのインスタンス取得
        realm = Realm.getDefaultInstance()
        /**
         * Realmインスタンスデータを取得するクエリを発行する
         * クエリ発行時、whereメソッドの型引数にて、モデルの型(=モデルクラスとして作成したクラス)を指定する
         * そのあと、findAllメソッドですべてのスケジュールを取得でき、変数に格納
         */
        val schedules = realm.where<Schedule>().findAll()
        /**
         * ScheduleAdapterクラスのインスタンスを生成し、リストビューに設定する
         * リストビューへのアダプターの設定に、adapterプロパティを利用
         */
        listView.adapter = ScheduleAdapter(schedules)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            startActivity<ScheduleEditActivity>()
        }

        // リストビューの項目がタップされた時のリスナーを、setOnItemClickListenerメソッドで登録
        /**
         * AdapterView.OnItemClickListenerインタフェースを実装したクラスは
         * onItemClickメソッドをオーバーライドして処理を実装するが、このIFがSAMインタフェースの為
         * SAM変換が利用可能
         */
        listView.setOnItemClickListener { parent, view, position, id ->
            // 第三引数のpositionにて、タップされた項目のリスト上の位置を取得
            // getItemAtPositionメソッドで、リスト内の指定された位置に関連するデータつまり
            // Scheduleのインスタンスを取得
            val schedule = parent.getItemAtPosition(position) as Schedule
            // 取得したScheduleインスタンスからidを取得し、intentに、keyに"schedule_id"を設定して
            // idをScheduleEditActivityに渡す
            startActivity<ScheduleEditActivity>(
                "schedule_id" to schedule.id
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // アクティビティ終了時、closeメソッドで、Realmのインスタンスを破棄し、リソースを開放
        realm.close()
    }
}