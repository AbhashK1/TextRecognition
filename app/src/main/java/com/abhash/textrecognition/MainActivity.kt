package com.abhash.textrecognition

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    lateinit var img:ImageView
    lateinit var textview:TextView
    lateinit var snapBtn:Button
    lateinit var detectBtn:Button

    lateinit var imageBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img=findViewById(R.id.image)
        textview=findViewById(R.id.text)
        snapBtn=findViewById(R.id.snapBtn)
        detectBtn=findViewById(R.id.detectBtn)

        detectBtn.setOnClickListener { detectTxt() }

        snapBtn.setOnClickListener { dispatchTakePictureIntent() }
    }

    private val REQUEST_IMAGE_CAPTURE=1

    private fun dispatchTakePictureIntent(){
        val takePictureIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE)
        } catch (e:ActivityNotFoundException){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            imageBitmap=data?.extras?.get("data") as Bitmap
            img.setImageBitmap(imageBitmap)
        }
    }

    private fun detectTxt(){
        val image=InputImage.fromBitmap(imageBitmap,0)
        val recognizerLatin=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val recognizerDevanagari=TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
        val result=recognizerLatin.process(image)
            .addOnSuccessListener { visionText->
                processText(visionText)
            }
            .addOnFailureListener{e ->
                Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
            }
    }

    private fun processText(visionText: Text?) {
        val blocks= visionText?.textBlocks
        if (blocks != null) {
            if(blocks.size==0) {
                Toast.makeText(applicationContext, "No Text", Toast.LENGTH_LONG).show()
                return
            }
            for(block in visionText.textBlocks){
                val txt=block.text
                textview.text = txt
            }
        }
    }

}