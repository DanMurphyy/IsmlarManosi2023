package com.hfad.ismlarmanosi2023.fragments.quotes

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.ads.AdRequest
import com.hfad.ismlarmanosi2023.R
import com.hfad.ismlarmanosi2023.databinding.FragmentQuoteBinding
import com.hfad.ismlarmanosi2023.remote.Constants
import com.hfad.ismlarmanosi2023.utils.notification.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class QuoteFragment : Fragment() {

    private var _binding: FragmentQuoteBinding? = null
    private val binding get() = _binding!!

    private val adapter: QuoteAdapter by lazy { QuoteAdapter() }
    private val mQuoteViewModel: QuoteViewModel by viewModels()

    private var quotePosition: Int = -1

    @Inject
    lateinit var adRequest: AdRequest

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                scheduleDailyNotification(requireContext())
            } else {
                showPermissionDeniedDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuoteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        quotePosition = arguments?.getInt(Constants.QUESTION_POSITION, -1) ?: -1

        binding.adView9.loadAd(adRequest)
        binding.adView10.loadAd(adRequest)
        binding.adView11.loadAd(adRequest)
//        loadInterstitialAd(requireContext())

        requestLocationPermission()

        mQuoteViewModel.quoteData.observe(viewLifecycleOwner) { data ->
            val shuffledData = data.shuffled()
            adapter.setData(shuffledData)

            if (quotePosition != -1) {
                val targetItem = data[quotePosition]
                val targetQuotePosition = shuffledData.indexOf(targetItem)

                showRecyclerView(targetQuotePosition)
            } else {

                showRecyclerView(adapter.itemCount / 2)
            }

        }

        binding.share.setOnClickListener {

//            showInterstitialAd(requireActivity())

            generateLayoutPhoto()
            shareLayoutPhoto()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quotes_manu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_quote -> {
                disclaimerAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                scheduleDailyNotification(requireContext())
            }

            else -> {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.notification_permission_needed))
            .setMessage(getString(R.string.receive_notifications))
            .setPositiveButton(R.string.settings) { _, _ ->
                openAppSettings()
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun disclaimerAlert() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.disclaimer))
            .setMessage(getString(R.string.disclaimer_info).trimIndent())
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun scheduleDailyNotification(context: Context) {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 11)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("daily_notification")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        val workManager = WorkManager.getInstance(context)
        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                scheduleDailyNotification(context)
            }
        }
    }

    private fun showRecyclerView(position: Int) {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.post {
            recyclerView.scrollToPosition(position)
        }
    }

    private fun generateLayoutPhoto(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            binding.recyclerView.width, binding.recyclerView.height, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        binding.recyclerView.draw(canvas)

        return bitmap
    }

    private fun shareLayoutPhoto() {
        val bitmap = generateLayoutPhoto()

        val file = File(requireContext().cacheDir, "layout_image.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.close()

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(
            Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                requireContext(), "com.danmurphyy.masjidgo" + ".provider", file
            )
        )
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.link_for_app) + "https://play.google.com/store/apps/details?id=com.danmurphyy.masjidgo"
        )
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(Intent.createChooser(shareIntent, "Share layout photo with caption"))
    }

    companion object {
        fun newInstance(quotePosition: Int = -1): QuoteFragment {
            val fragment = QuoteFragment()
            val args = Bundle().apply {
                putInt(Constants.QUESTION_POSITION, quotePosition)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}