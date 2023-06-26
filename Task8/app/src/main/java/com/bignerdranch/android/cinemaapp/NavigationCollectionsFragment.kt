package com.bignerdranch.android.cinemaapp

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NavigationCollectionsFragment : Fragment(), CollectionAdapter.OnCollectionClickListener {

    private lateinit var collectionAdapter: CollectionAdapter
    private val collections = mutableListOf<Collection>()
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // инициализация SharedPrefsHelper
        sharedPrefsHelper = SharedPrefsHelper(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_navigation_collections, container, false)

        // инициализация адаптера коллекций
        collectionAdapter = CollectionAdapter(collections)
        collectionAdapter.setOnCollectionClickListener(this)

        // установка адаптера для RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.collectionsRecyclerView)
        recyclerView.adapter = collectionAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // Загрузка коллекций из Shared Preferences
        collections.addAll(sharedPrefsHelper.loadCollections())
        collectionAdapter.notifyDataSetChanged()

        val createCollectionButton = view.findViewById<Button>(R.id.createCollectionButton)
        createCollectionButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), createCollectionButton)
            popupMenu.inflate(R.menu.menu_add)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_add -> {
                        // запуск активности CreateCollectionScreen для создания новой коллекции
                        val intent = Intent(requireContext(), CreateCollectionScreen::class.java)
                        startActivityForResult(intent, REQUEST_CREATE_COLLECTION)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        return view
    }

    // обработка результата возвращаемого из активности
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CREATE_COLLECTION && resultCode == RESULT_OK) {
            val name = data?.getStringExtra(CreateCollectionScreen.EXTRA_COLLECTION_NAME)
            val iconId = data?.getIntExtra(CreateCollectionScreen.EXTRA_SELECTED_ICON, R.drawable.ico1)
            if (name != null && iconId != null) {
                val collection = Collection(name, iconId)
                collections.add(collection)
                collectionAdapter.notifyDataSetChanged()
                sharedPrefsHelper.saveCollections(collections)
            }
        }
    }

    // удаление выбранной коллекции
    override fun onDeleteClicked(collection: Collection) {
        collections.remove(collection)
        collectionAdapter.notifyDataSetChanged()
        sharedPrefsHelper.saveCollections(collections)
    }

    // код запроса для создания коллекции
    companion object {
        private const val REQUEST_CREATE_COLLECTION = 1
    }
}
