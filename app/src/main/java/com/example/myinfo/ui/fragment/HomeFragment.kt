package com.example.myinfo.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.myinfo.R
import com.example.myinfo.databinding.FragmentHomeBinding
import com.example.myinfo.utils.app.DataStorePrefs
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Save current user pref
    private var userName: String = ""
    private var userAge: String = ""
    private var neverShowAgain: Boolean = false
    private var animateFlag = 0

    // For PDF generate
    private val STORAGE_CODE = 1001

    // DataStore variable
    private lateinit var dataStorePrefs: DataStorePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStorePrefs = DataStorePrefs(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        observeFlag()

        binding.lottieTraveler.playAnimation()
        // Check the never show again data
        // then display the intro dialog fragment
        observeUserData()

        //binding.mainTitle.setDelay(400)
        //binding.mainTitle.animateText("Please introduce yourself, adventurer $userName.")
        //binding.mainTitle.text = getString(R.string.please_introduce)

        binding.proceedIntroTv.setOnClickListener {
            showIntroDialog()
        }

        return binding.root
    }

    private fun storeUserData(name: String, age: String, neverShow: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            dataStorePrefs.storeUserData(name, age, neverShow)
        }
    }
    
    private fun storeAnimateTextFlag(){
        viewLifecycleOwner.lifecycleScope.launch { 
            dataStorePrefs.setAnimateTextFlag(1)
        }
    }
    
    private fun observeFlag(){
        var flagValue: Int
        dataStorePrefs.getAnimateTextFlag.asLiveData().observe(viewLifecycleOwner, { flag ->
            when (flag) {
                1 -> {
                    flagValue =  flag 
                    animateFlag = flagValue
                }
            }
        })
    }

    private fun observeUserData() {
        val animateTextFlag = 0
        dataStorePrefs.getAnimateTextFlag.asLiveData().observe(viewLifecycleOwner, { flag ->
            when (flag) {
                1 -> animateTextFlag == 1
            }
        })

        dataStorePrefs.userNameFlow.asLiveData().observe(viewLifecycleOwner, { name ->
            when (name) {
                "XXX", "", " " -> {
                    binding.proceedIntroTv.text = getString(R.string.proceed_to_introduction)
                    binding.mainTitle.text = getString(R.string.please_introduce)
                }
                else -> {
                    if (animateFlag == 1) {
                        binding.homeHelloTv.text = "Welcome!"
                        binding.proceedIntroTv.text = getString(R.string.change_name)
                        binding.mainTitle.text = (getString(R.string.little_story) + " $name?")
                    } else {
                        binding.homeHelloTv.text = "Welcome!"
                        binding.proceedIntroTv.text = getString(R.string.change_name)
                        binding.mainTitle.animateText(getString(R.string.little_story) + " $name?")
                    }
                }
            }
        })

        dataStorePrefs.userNeverShowAgain.asLiveData().observe(viewLifecycleOwner, { neverShow ->
            when (neverShow) {
                true -> {
                    binding.proceedIntroTv.visibility = View.GONE
                }
                else -> {}
            }
        })
    }

    private fun showIntroDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_frag_intro)

        val name = dialog.findViewById<TextInputEditText>(R.id.intro_name_et)
        val age = dialog.findViewById<TextInputEditText>(R.id.intro_age_et)
        val neverShow = dialog.findViewById<CheckBox>(R.id.never_show_again_tv)

        dialog.findViewById<MaterialButton>(R.id.submit_feedback_btn).setOnClickListener {

            userName = name.text.toString()
            userAge = age.text.toString()
            neverShowAgain = neverShow.isChecked

            if (userName.isEmpty() || userAge.isEmpty()) {
                dialog.findViewById<TextInputLayout>(R.id.nameLayout).error = "Required field"
                dialog.findViewById<TextInputLayout>(R.id.ageLayout).error = "Required field"
            } else {
                storeUserData(userName, userAge, neverShowAgain)
                storeAnimateTextFlag()
                dialog.dismiss()
            }
        }

        dialog.findViewById<MaterialButton>(R.id.feedback_closeBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Transfer to Summary Fragment
    private fun savePDF() {
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val mFilePath =
            Environment.getExternalStorageDirectory()
                .toString() + "/" + "PRASANTHMANIMARAN" + mFileName + ".pdf"

        try {

            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()

            val data = " Prasanth Manimaran Surgery App Details"

            val thread = Thread {
                try {
                    val url = URL("https://images.dog.ceo/breeds/saluki/n02091831_3400.jpg")
                    val image = Image.getInstance(url)
                    mDoc.addAuthor(data.split(" ")[0])
                    mDoc.addHeader("header", "Sharing to friends")
                    mDoc.addTitle("Prasanth Manimaran")
                    mDoc.add(Paragraph(data))
                    mDoc.add(image)
                    mDoc.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()

            Toast.makeText(
                requireContext(),
                "$mFileName.pdf\n is created at\n $mFilePath",
                Toast.LENGTH_SHORT
            ).show()
            println("\"$mFileName.pdf\\n is created at\\n $mFilePath\"")

        } catch (e: Exception) {
            println(e.toString())
            Toast.makeText(requireContext(), "" + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun permission() {
        if (checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_CODE
            )
        } else {
            savePDF()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePDF()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Access has been denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}