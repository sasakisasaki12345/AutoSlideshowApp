package com.example.autoslideshowapp

import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.content.ContentUris
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //verを確認
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //パーミッションを確認
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //許可あれば以下対応

                //画像情報取得
                val resolver = contentResolver
                val cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )

                //まず1枚目を表示
                if (cursor.moveToFirst()) {
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)

                    Log.d("AAA",imageUri.toString())

                    //表示後にどうするか

                    next_button.setOnClickListener {
                        if (cursor.moveToNext()) { //次をデータを指す。可能なら次の画像表示
                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            imageView.setImageURI(imageUri)

                            Log.d("AAA",imageUri.toString())

                        } else { //次のデータを指せない場合。不可能なら1番最初の画像を行事
                            cursor.moveToFirst()

                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            imageView.setImageURI(imageUri)

                            Log.d("AAA",imageUri.toString())

                        }

                    }

                    back_button.setOnClickListener {
                        if (cursor.moveToPrevious()) {//前のデータを確認。trueなら前の画像を表示
                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            imageView.setImageURI(imageUri)

                            Log.d("AAA",imageUri.toString())

                        } else {//faulsなら1番最後の画像を表示
                            cursor.moveToLast()
                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            imageView.setImageURI(imageUri)

                            Log.d("AAA",imageUri.toString())

                        }

                    }

                    start_button.setOnClickListener {
                        if(start_button.text == "再生") {//ボタンのテキストが再生か一時停止か判断

                            start_button.text = "停止"//ボタンのテキストを一時停止に
                            next_button.isEnabled = false
                            back_button.isEnabled = false

                            mTimer = Timer()

                            //ボタンのテキストが再生なら再生の対応
                            mTimer!!.schedule(object :TimerTask() {
                                override fun run() {
                                    if (cursor.moveToNext()) { //次があるか確認
                                    //true 出力
                                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                        val id = cursor.getLong(fieldIndex)
                                        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                        mHandler.post {
                                            imageView.setImageURI(imageUri)
                                        }

                                        Log.d("AAA",imageUri.toString())

                                    } else { //false はじめを出力
                                        cursor.moveToFirst()
                                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                        val id = cursor.getLong(fieldIndex)
                                        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                        mHandler.post {
                                            imageView.setImageURI(imageUri)
                                        }

                                        Log.d("AAA",imageUri.toString())

                                    }
                               }
                            }
                            ,2000,2000)

                            //

                        }else{
                            //ボタンのテキストが一時停止、一時停止の対応
                            if(mTimer != null){ //timerが稼働していることを確認
                                mTimer!!.cancel()
                                mTimer = null
                            }
                            start_button.text = "再生"//ボタンのテキストを再生に
                            next_button.isEnabled = true
                            back_button.isEnabled = true


                        }

                    }

                } else {
                    //1枚目もなければ終了
                    cursor.close()
                }


            } else { //パーミッションが不許可、許可ダイヤログを表示
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }

        } else { //verが5系統以下
            getContentsInfo()
        }
    }

    private fun getContentsInfo() {
        //画像の情報を取得するメソッド


    }

}