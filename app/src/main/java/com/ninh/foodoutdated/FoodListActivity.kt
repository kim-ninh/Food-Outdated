package com.ninh.foodoutdated

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.app.ActivityCompat
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ninh.foodoutdated.FoodListActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class FoodListActivity : AppCompatActivity() {
    private var actionMode: ActionMode? = null
    private lateinit var recyclerView: RecyclerView
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var tracker: SelectionTracker<*>
    private var actionModeVisible = false
    private var productDAO: ProductDAO? = null
    var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val actionModeCallBack: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.delete_context, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.item_delete -> {
                    //TODO: delete selected item
                    val selection = tracker!!.selection
                    val it: Iterator<*> = selection.iterator()
                    while (it.hasNext()) {
                        val id = it.next() as Long
                        val adapterIndex = productDAO!!.deleteById(id)
                        if (adapterIndex != -1) {
                            recyclerView!!.adapter!!.notifyItemRemoved(adapterIndex)
                        }
                    }
                    Logger.i("%s", selection.toString())
                    actionMode!!.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            actionModeVisible = false
            clearAllSelection()
        }
    }

    private fun clearAllSelection() {
        if (tracker!!.selection.size() != 0) {
            tracker!!.clearSelection()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)
        Logger.addLogAdapter(AndroidLogAdapter())
        val intent = Intent(this, AddProductActivity::class.java)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { startActivityForResult(intent, ADD_PRODUCT_REQUEST) }
        productDAO = ProductDAO(this@FoodListActivity)
        recyclerView = findViewById(R.id.my_recycler_view)

        // use a linear layout manager
        layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)
        mAdapter = MyAdapter(productDAO!!.loadAll())
        (mAdapter as MyAdapter).setContext(this)
        recyclerView.setAdapter(mAdapter)
        recyclerView.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
        val tracker = SelectionTracker.Builder(
                "my-selection-id",
                recyclerView,
                MyItemKeyProvider(recyclerView),
                MyDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build()
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Any?>() {
            override fun onItemStateChanged(key: Any, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                val totalSelection = tracker.getSelection().size()
                if (!actionModeVisible && totalSelection == 1) {
                    actionMode = startSupportActionMode(actionModeCallBack)
                    actionModeVisible = true
                }
                if (actionMode == null) return
                if (totalSelection == 0) {
                    actionMode!!.finish()
                    return
                }
                actionMode!!.title = String.format("%d selected", totalSelection)
            }
        })
        (mAdapter as MyAdapter).setTracker(tracker)
        this.tracker = tracker
        if (!checkPermissionGranted()) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PRODUCT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                var newProduct: Product? = null
                if (data != null) {
                    newProduct = data.getParcelableExtra("new_product")
                    productDAO!!.add(newProduct)
                    val size = productDAO!!.size()
                    recyclerView!!.adapter!!.notifyItemInserted(size - 1)
                    Snackbar.make(recyclerView!!, "Add successful!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show()
                }
            }
        }
    }

    private fun checkPermissionGranted(): Boolean {
        var arePermissionsGranted = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                arePermissionsGranted = false
            }
        }
        return arePermissionsGranted
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST) return
        var arePermissionsGranted = true
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                arePermissionsGranted = false
            }
        }
        if (!arePermissionsGranted && Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
        }
    }

    companion object {
        private const val ADD_PRODUCT_REQUEST = 1
        private const val PERMISSION_REQUEST = 484
    }
}