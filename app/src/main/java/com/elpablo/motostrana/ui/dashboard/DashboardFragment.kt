package com.elpablo.motostrana.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.elpablo.motostrana.App
import com.elpablo.motostrana.databinding.FragmentDashboardBinding
import com.elpablo.motostrana.ui.MainActivity
import com.elpablo.motostrana.utils.BaseFragment
import com.elpablo.motostrana.utils.CONST
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class DashboardFragment :
    BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private lateinit var adapter: DashboardRecyclerViewAdapter
    private lateinit var viewModel: DashboardViewModel
    private var event = CONST.NEWS                              //по умолчанию отображаем новости
    private var lastTabPosition = 0                             //для сохранения выбранного таба

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)!!.showBottomNav()
        binding.headerText.text = "Новости"

        if (savedInstanceState != null) {
            lastTabPosition = savedInstanceState.getInt(TAB_POSITION)
            event = savedInstanceState.getString(EVENT).toString()
            if (event == CONST.NEWS) {
                binding.headerText.text = "Новости"
            } else {
                binding.headerText.text = "Мероприятия"
            }
        }

        binding.buttonLogout.setOnClickListener {
            activity?.finish()
        }

        App.initUser()
        initTabs()
        initRecyclerView()
        initViewModel(event)

    }

    private fun initTabs() {
        //Обработка Tabs
        binding.dashboardTabs.getTabAt(lastTabPosition)?.select()
        binding.dashboardTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        event = CONST.NEWS
                        lastTabPosition = tab.position
                        binding.headerText.text = "Новости"
                    }
                    1 -> {
                        event = CONST.EVENT
                        lastTabPosition = tab.position
                        binding.headerText.text = "Мероприятия"
                    }
                }
                initViewModel(event)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    //Инициализируем RecyclerView
    private fun initRecyclerView() =
        with(binding) {
            adapter = DashboardRecyclerViewAdapter()
            eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            eventRecyclerView.adapter = adapter
        }

    //Подпысываем viewModel
    private fun initViewModel(event: String) {
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        viewModel.getEvents(event).observe(viewLifecycleOwner) { _event ->
            adapter.submitList(_event)
        }
        binding.progressBar.visibility = View.GONE
    }

    //сохраняем состояние
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAB_POSITION, lastTabPosition)
        outState.putString(EVENT, event)
    }

    companion object {
        private const val TAB_POSITION = "lastTabPosition"
        private const val EVENT = "event"
    }
}