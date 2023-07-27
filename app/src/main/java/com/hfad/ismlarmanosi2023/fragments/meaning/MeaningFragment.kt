package com.hfad.ismlarmanosi2023.fragments.meaning

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.hfad.ismlarmanosi2023.BuildConfig
import com.hfad.ismlarmanosi2023.R
import com.hfad.ismlarmanosi2023.dataLiked.LikedData
import com.hfad.ismlarmanosi2023.dataLiked.LikedViewModel
import com.hfad.ismlarmanosi2023.databinding.FragmentMeaningBinding
import java.io.File
import java.io.FileOutputStream


class MeaningFragment : Fragment() {
    private var _binding: FragmentMeaningBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<MeaningFragmentArgs>()
    private val mLikedViewModel: LikedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMeaningBinding.inflate(inflater, container, false)

        showInfo()
        binding.shareM.setOnClickListener {
            generateLayoutPhoto()
            shareLayoutPhoto("Ilova uchun havola:")
        }
        binding.likeM.setOnClickListener {
            insertDataToDB()
        }

        return (binding.root)
    }

    private fun showInfo() {
        binding.meaningName.text = args.currentItem.name
        binding.meaningGender.text = args.currentItem.gender
        binding.meaningMeaning.text = args.currentItem.meaning
        binding.meaningOrigin.text = args.currentItem.origin

        val boyImages = arrayOf(
            R.drawable.boy1,
            R.drawable.boy2,
            R.drawable.boy3,
            R.drawable.boy4,
            R.drawable.boy5,
            R.drawable.boy6,
            R.drawable.boy7,
            R.drawable.boy8,
            R.drawable.boy9,
            R.drawable.boy10,
        )
        val girlImage = arrayOf(
            R.drawable.girl1,
            R.drawable.girl2,
            R.drawable.girl3,
            R.drawable.girl4,
            R.drawable.girl5,
            R.drawable.girl6,
            R.drawable.girl7,
            R.drawable.girl8,
            R.drawable.girl9,
            R.drawable.girl10,
        )

        when (args.currentItem.gender) {
            "Erkak ismi" -> {
                val randomBoyImage = boyImages.random()
                binding.photoM.setImageResource(randomBoyImage)
            }

            else -> {
                val randomGirlImage = girlImage.random()
                binding.photoM.setImageResource(randomGirlImage)
            }
        }
    }


    private fun insertDataToDB() {
        val mId = args.currentItem.id
        val mName = args.currentItem.name
        val mGender = args.currentItem.gender
        val mMeaning = args.currentItem.meaning
        val mOrigin = args.currentItem.origin

        val newData = LikedData(mId, mName, mGender, mMeaning, mOrigin)
        mLikedViewModel.insertData(newData)
        showSnackBar()


    }

    private fun showSnackBar(){
        val snackBar = Snackbar.make(
            requireView(), getString(R.string.succesfullyAdded), Snackbar.LENGTH_SHORT
        )
        // Set the background color of the Snackbar to the specified color resource
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.background)
        snackBar.view.setBackgroundColor(backgroundColor)

        // Set the text color of the Snackbar
        val textColor = Color.BLACK // Set the desired text color here
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            ?.setTextColor(textColor)

        // Set the bottom margin for the Snackbar view
        val layoutParams = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(20, 20, 20, 50) // Specify the margin in pixels here
        snackBar.view.layoutParams = layoutParams
        snackBar.duration = Snackbar.LENGTH_SHORT / 5

        snackBar.show()
        Handler().postDelayed({
            snackBar.dismiss()
        }, 1000)
    }

    private fun generateLayoutPhoto(): Bitmap {
        // Find the root view of your layout
        val rootView = binding.root

        // Create a bitmap with the dimensions of the RecyclerView
        val bitmap = Bitmap.createBitmap(binding.loCardView.width,
            binding.loCardView.height,
            Bitmap.Config.ARGB_8888)
        // Create a canvas using the bitmap
        val canvas = Canvas(bitmap)
        // Draw the background of the root view onto the canvas
        rootView.draw(canvas)
        // Translate the canvas to the location of the RecyclerView within the root view
        canvas.translate(binding.loCardView.left.toFloat(), binding.loCardView.top.toFloat())
        // Draw the RecyclerView onto the canvas
        binding.loCardView.draw(canvas)

        return bitmap
    }


    private fun shareLayoutPhoto(caption: String) {
        // Generate a bitmap of the specific view
        val bitmap = generateLayoutPhoto()

        // Save the bitmap to a temporary file
        val file = File(requireContext().cacheDir, "layout_image.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.close()

        // Create a share intent for the temporary file and the caption
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file))
        shareIntent.putExtra(Intent.EXTRA_TEXT, caption)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Show the share dialog
        startActivity(Intent.createChooser(shareIntent, "Share layout photo with caption"))
    }

}