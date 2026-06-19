package com.hfad.ismlarmanosi2023.fragments.liked

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hfad.ismlarmanosi2023.R
import com.hfad.ismlarmanosi2023.data.NamesData
import com.hfad.ismlarmanosi2023.dataLiked.ImageUtil
import com.hfad.ismlarmanosi2023.dataLiked.LikedData
import com.hfad.ismlarmanosi2023.dataLiked.LikedViewModel
import com.hfad.ismlarmanosi2023.databinding.RowLayoutLikedBinding
import java.io.File
import java.io.FileOutputStream

class LikedAdapter : RecyclerView.Adapter<LikedAdapter.MyViewHolder>() {

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

                val imageResId = if (i.gender == "O'g'il bolalar ismi" || i.gender == "O'gil bolalar ismi" || i.gender == "Ўғил болалар исми" || i.gender == "Ўгил болалар исми") {
                    ImageUtil.boyImages[imageIndex]
                } else {
                    ImageUtil.girlImage[imageIndex]
                }

                Glide.with(holder.itemView.context)
                    .load(imageResId)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.photoL)
            }
        }

        Glide.with(holder.itemView.context)
            .load(R.drawable.ic_liked)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.likeL)

        Glide.with(holder.itemView.context)
            .load(R.drawable.sharing_m)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.shareL)


        val itemToDelete = dataList[position] // Retrieve the LikedData object to delete
        val viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(holder.itemView.context.applicationContext as Application)
                .create(LikedViewModel::class.java)

        binding.likeL.setOnClickListener {

            viewModel.deleteData(itemToDelete) // Call the deleteData function from ViewModel
            dataList = dataList.filterNot { it.id == itemToDelete.id }
            notifyItemRemoved(position)
        }

        binding.shareL.setOnClickListener {
            shareItem(holder.itemView.context, binding)
        }
    }

    private fun shareItem(context: Context, binding: RowLayoutLikedBinding) {
        try {
            val bitmap = generateLayoutPhoto(binding)
            val file = File(context.cacheDir, "shared_name_${System.currentTimeMillis()}.jpg")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Ilova uchun havola: https://play.google.com/store/apps/details?id=${context.packageName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Name"))
        } catch (e: Exception) {
            Log.e("LikedAdapter", "Error sharing item", e)
        }
    }

    private fun generateLayoutPhoto(binding: RowLayoutLikedBinding): Bitmap {
        val view = binding.root
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
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