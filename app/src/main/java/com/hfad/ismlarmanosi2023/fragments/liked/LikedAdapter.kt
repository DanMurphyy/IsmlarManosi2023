package com.hfad.ismlarmanosi2023.fragments.liked

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.ismlarmanosi2023.BuildConfig
import com.hfad.ismlarmanosi2023.data.NamesData
import com.hfad.ismlarmanosi2023.dataLiked.ImageUtil
import com.hfad.ismlarmanosi2023.dataLiked.LikedData
import com.hfad.ismlarmanosi2023.dataLiked.LikedViewModel
import com.hfad.ismlarmanosi2023.databinding.RowLayoutLikedBinding
import java.io.File
import java.io.FileOutputStream

class LikedAdapter : RecyclerView.Adapter<LikedAdapter.MyViewHolder>() {

    private lateinit var appContext: Context

    var dataList = emptyList<LikedData>()
    var dataOriginList = emptyList<NamesData>()


    class MyViewHolder(internal val binding: RowLayoutLikedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RowLayoutLikedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        val binding = holder.binding
        Log.d("LikedAdapter", "Position: $position, CurrentItem ID: ${currentItem.id}")


        for (i in dataOriginList) {
            if (i.id == currentItem.id) {
                Log.d("LikedAdapter", "Matched NamesData ID: ${i.id}")

                binding.lName.text = i.name
                binding.lGender.text = i.gender
                binding.lMeaning.text = i.meaning
                binding.lOrigin.text = i.origin

                val imageIndex = currentItem.imageIndex

                if (i.gender == "O'g'il bolalar ismi" || i.gender == "O'gil bolalar ismi") {
                    binding.photoL.setImageResource(ImageUtil.boyImages[imageIndex])
                } else {
                    binding.photoL.setImageResource(ImageUtil.girlImage[imageIndex])
                }
            }
        }


        val itemToDelete = dataList[position] // Retrieve the LikedData object to delete
        val viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(holder.itemView.context.applicationContext as Application)
                .create(LikedViewModel::class.java)

        binding.likeL.setOnClickListener {

            viewModel.deleteData(itemToDelete) // Call the deleteData function from ViewModel
            dataList = dataList.filterNot { it.id == itemToDelete.id }
            notifyItemRemoved(position)
        }

        fun generateLayoutPhoto(): Bitmap {
            // Find the root view of your layout
            val rootView = binding.root

            // Create a bitmap with the dimensions of the RecyclerView
            val bitmap = Bitmap.createBitmap(
                binding.root.width,
                binding.root.height,
                Bitmap.Config.ARGB_8888
            )
            // Create a canvas using the bitmap
            val canvas = Canvas(bitmap)
            // Draw the background of the root view onto the canvas
            rootView.draw(canvas)
            // Translate the canvas to the location of the RecyclerView within the root view
            canvas.translate(binding.root.left.toFloat(), binding.root.top.toFloat())
            // Draw the RecyclerView onto the canvas
            binding.root.draw(canvas)

            return bitmap
        }


        fun shareLayoutPhoto(caption: String) {
            // Generate a bitmap of the specific view
            val bitmap = generateLayoutPhoto()

            // Save the bitmap to a temporary file
            val file = File(appContext.cacheDir, "layout_image.jpg")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.close()

            // Create a share intent for the temporary file and the caption
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/jpeg"
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    appContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file
                )
            )
            shareIntent.putExtra(Intent.EXTRA_TEXT, caption)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Get the Activity context from the itemView's context
            val activityContext = holder.itemView.context as Activity

            // Show the share dialog using the Activity context
            activityContext.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share layout photo with caption"
                )
            )
        }

        binding.shareL.setOnClickListener {
            appContext = binding.root.context.applicationContext
            generateLayoutPhoto()
            shareLayoutPhoto("Ilova uchun havola:")
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(likedData: List<LikedData>) {
        val likedDiffUtil = LikedDiffUtil(dataList, likedData)
        val likedDiffUtilResult = DiffUtil.calculateDiff(likedDiffUtil)
        this.dataList = likedData
        likedDiffUtilResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    fun setNamesData(namesData: List<NamesData>) {
        this.dataOriginList = namesData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}