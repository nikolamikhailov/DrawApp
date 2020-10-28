package com.example.drawapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.drawapp.model.MainToolItem
import com.example.drawapp.model.TOOL
import com.example.drawapp.uimodel.ToolsLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DrawActivity : AppCompatActivity(), DrawViewHolder {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
    }

    private val viewModel: ViewModel by viewModel()

    private lateinit var toolsLayouts: List<ToolsLayout>
    private lateinit var mainToolbar: ToolsLayout

    private var rationaleDialog: AlertDialog? = null

    private lateinit var mService: LoadImageService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LoadImageService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    override fun onClicked() {
        hiddenToolbarMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
        viewModel.viewState.observe(this, Observer(::render))
    }

    private fun initUi() {
        mainToolbar = tools as ToolsLayout
        showHideToolbar.setImageResource(
            if (mainToolbar.isVisible) R.drawable.ic_visible
            else R.drawable.ic_unvisible
        )
        toolsLayouts = listOf(
            palette as ToolsLayout,
            sizes as ToolsLayout,
            shapes as ToolsLayout
        )
        showHideToolbar.setOnClickListener {
            if (mainToolbar.isVisible) {
                hiddenToolbarMode()
            } else shownToolbarMode()
        }
        clearCanvas.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnClearClick)
            drawView.clear()
        }
        saveCanvas.setOnClickListener { onSaveBtnClick() }
        toolsLayouts[0].setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnColorClick(it))
        }
        toolsLayouts[1].setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnShapeClick(it))
        }
        toolsLayouts[2].setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnSizeClick(it))
        }
        mainToolbar.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnToolbarClicked(it))
        }
    }

    private fun render(viewState: ViewState) {
        mainToolbar.render(
            viewState.toolsList.map {
                MainToolItem(
                    tool = it,
                    isSelected = viewState.selectedTool == it,
                    paintsState = viewState.paintState
                )
            }
        )
        val selectedLayout = when (viewState.selectedTool) {
            TOOL.PALETTE -> palette
            TOOL.SHAPE -> shapes
            TOOL.SIZE -> sizes
            else -> null
        }
        with(toolsLayouts) {
            find { it.isVisible }?.isVisible = false
            find { it == selectedLayout }?.let { it.isVisible = true }
        }
        // TOD create child classes from ToolsLayout and avoid indexes
        toolsLayouts[0].render(viewState.colorList)
        toolsLayouts[1].render(viewState.shapeList)
        toolsLayouts[2].render(viewState.sizeList)
        drawView.render(viewState.paintState)
    }

    private fun hiddenToolbarMode() {
        mainToolbar.isVisible = false
        showHideToolbar.setImageResource(R.drawable.ic_visible)
        toolsLayouts.forEach { it.isVisible = false }
    }

    private fun shownToolbarMode() {
        toolsLayouts.forEach { it.isVisible = false }
        mainToolbar.isVisible = true
        showHideToolbar.setImageResource(R.drawable.ic_unvisible)
    }

    private fun onSaveBtnClick() {
        if (isPermissionGranted()) {
            sendImage()
        } else {
            val isNeedRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (isNeedRationale) {
                showRationaleDialog()
            } else {
                requestPermission()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, LoadImageService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun sendImage() {
        viewModel.processUiEvent(UiEvent.OnSaveClick)

        if (mBound) {
            mService.saveImage(drawView.getBitmap())
            toast("Your image saved!")
        } else toast("Your image didn't save!")
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            sendImage()
        } else {
            val isNeedRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (isNeedRationale) {
                showRationaleDialog()
            }
        }
    }

    private fun showRationaleDialog() {
        rationaleDialog = AlertDialog.Builder(this)
            .setTitle("Разрешение нужно для загрузки картинки")
            .setPositiveButton("OK") { _, _ -> requestPermission() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

}

