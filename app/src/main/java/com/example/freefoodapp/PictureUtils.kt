package com.example.freefoodapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.media.ExifInterface

/**
 * Orients and scales image
 */
fun orientBitmap(path:String, bmp : Bitmap) : Bitmap{
    var gfgExifB : ExifInterface = ExifInterface(path)
    var gfgRotationB : Int = gfgExifB.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
    var angle : Int = 0
    if(gfgRotationB == ExifInterface.ORIENTATION_ROTATE_90){
        angle = 90
    } else if(gfgRotationB == ExifInterface.ORIENTATION_ROTATE_180){
        angle = 180
    } else if(gfgRotationB == ExifInterface.ORIENTATION_ROTATE_270){
        angle = 270
    }
    var mat : Matrix = Matrix()
    mat.postRotate(angle.toFloat())
    var newbmp = createBitmap(bmp,0,0, bmp.width, bmp.height, mat, true)
    return newbmp
    //hello world s
}

fun getScaledBitmap(path:String, activity: Activity): Bitmap {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)
    return getScaledBitmap(path,size.x,size.y)
}
fun getScaledBitmap(path: String, destWidth : Int, destHeight : Int) :
        Bitmap {
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path,options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    var inSampleSize = 1
    if(srcHeight > destHeight || srcWidth > destWidth){
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        val sampleScale = if (heightScale > widthScale){
            heightScale
        } else {
            widthScale
        }
        inSampleSize = Math.round(sampleScale)
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    return BitmapFactory.decodeFile(path,options)
}