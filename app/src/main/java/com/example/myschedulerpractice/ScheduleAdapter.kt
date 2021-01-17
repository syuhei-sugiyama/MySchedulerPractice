package com.example.myschedulerpractice

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class ScheduleAdapter(data: OrderedRealmCollection<Schedule>?) : RealmBaseAdapter<Schedule>(data) {
    // インナークラス
    inner class ViewHolder(cell: View) {
        val date = cell.findViewById<TextView>(android.R.id.text1)
        val title = cell.findViewById<TextView>(android.R.id.text2)
    }

    // リストビューの行として表示するためのメソッド
    // スクロール時、convertViewには、見えなくなったセルのオブジェクトがセットされている(=再利用)
    // position : リストビューのセルの位置
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        when (convertView) {
            null -> {
                // セルに設定するビューを新規作成
                // LayoutInflaterクラスは、XMLファイルからビューを生成する機能を提供するクラス
                // fromメソッドで、LayoutInflaterクラスのインスタンスを生成
                val inflater = LayoutInflater.from(parent?.context)
                // 実際に画面レイアウトを作成するのが、inflateメソッド。XMLファイルからビューを生成
                /**
                 * 第一引数・・・ビューを作成したいレイアウトXMLのリソースID
                 * →android.R.layout.simple_list_item_2は、Android SDKにもともと用意されているレイアウトXMLファイル
                 * 　このレイアウトには、RelativeLayoutの中に、text1,text2というIDのテキストビューが配置されている。
                 * 　今回は、これらのビューをセル用ビューとして使い、日付とタイトルを表示する
                 */
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                /**
                 * ViewHolderクラスは、インナークラスとして作成した、Viewオブジェクトを保持するためのもの
                 * 引数のViewオブジェクトには、レイアウトXML「android.R.layout.simple_list_item_2」が適用されており、
                 * 内部に配置するtext1,text2のIDを持つテキストビューの内容をメンバ変数として保持
                 */
                // ViewHolderクラスのインスタンスを生成
                viewHolder = ViewHolder(view)
                /**
                 * セル用ビューのタグの中に保持。tagプロパティを使用。ビューにタグをつける
                 * →convertViewを使いまわすときに、convertViewからfindViewByIdでビューを取り出すのではなく、
                 * 　タグとしてつけておいた、ViewHolderクラスのインスタンスをtagプロパティから取り出せるようにしておく
                 */
                view.tag = viewHolder
            }
            else -> {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
        }

        /**
         * RealmBaseAdapterクラスのadapterDataプロパティに、安全呼び出し演算子「?.」を使用してnullチェック実施
         * nullでない場合のみrun関数が実行される
         */
        adapterData?.run {
            val schedule = get(position)
            viewHolder.date.text =
                DateFormat.format("yyyy/MM/dd", schedule.date)
            viewHolder.title.text = schedule.title
        }
        return view
    }
}