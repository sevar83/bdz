package bg.bdz.schedule.features.timetable

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import bg.bdz.schedule.R
import bg.bdz.schedule.di.Dependencies
import bg.bdz.schedule.features.stations.SearchHistory
import bg.bdz.schedule.features.stations.Stations
import bg.bdz.schedule.features.stations.StationsAdapter
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.utils.KeyboardEventListener
import bg.bdz.schedule.utils.updateText
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_timetables_pager.*

class TimeTablesFragment : Fragment(R.layout.fragment_timetables_pager) {

    private lateinit var viewModel: TimeTablesViewModel

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var stationsAdapter: StationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity().viewModelStore,
            TimeTablesViewModel.Factory(
                Dependencies.provideSimplePreferences(),
                Dependencies.provideSearchHistory(),
                requireActivity()
            )
        )
            .get(TimeTablesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerAdapter = PagerAdapter(view.context, childFragmentManager)
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPage(position)
            }
        })

        timeTablesButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.setCurrentPage(checkedId)
            }
        }
        repeat(pagerAdapter.count) { position ->
            timeTablesButtonGroup.addView(
                MaterialButton(requireContext(), null, R.attr.materialButtonOutlinedStyle).apply {
                    id = position
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    text = pagerAdapter.getPageTitle(position)
                }
            )
        }
        if (savedInstanceState == null) {
            timeTablesButtonGroup.check(PAGE_DEPARTURES)
        }

        initializeStation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.station.observe(viewLifecycleOwner) { station ->
            onStationChanged(station, dissmissDropDown = false)
            stationAutoCompleteTextView.approveAsValid()
        }
        viewModel.recentSearches.observe(viewLifecycleOwner) { searches ->
            stationsAdapter.setRecentSearches(searches)
        }
        viewModel.currentPage.observe(viewLifecycleOwner) { currentPage ->
            pager.currentItem = currentPage
            timeTablesButtonGroup.check(currentPage)
        }
    }

    override fun onResume() {
        super.onResume()

        KeyboardEventListener(requireActivity(), lifecycle) { keyboardShown ->
            if (!keyboardShown) {
                clearFocus()
            }
        }
    }

    private fun initializeStation() {
        stationsAdapter = StationsAdapter(
            context = requireActivity(),
            originalItems = Stations.STATIONS,
            maxRecentsCount = SearchHistory.MAX_RECENT_SEARCHES
        )

        stationAutoCompleteTextView.setAdapter(stationsAdapter)
        stationAutoCompleteTextView.isDropDownAlwaysVisible = true
        stationAutoCompleteTextView.onActionListener = {
            val station = stationsAdapter.findStation(Station(stationAutoCompleteTextView.text.toString()))
            onStationChanged(station, dissmissDropDown = true)
        }
        stationAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val station = stationsAdapter.getItem(position)
            onStationChanged(station, dissmissDropDown = true)
        }
        stationAutoCompleteTextView.setOnClickListener {
            stationAutoCompleteTextView.refreshFiltering()
            stationAutoCompleteTextView.showDropDown()
        }
        stationAutoCompleteTextView.setOnDismissListener {
            val station = stationsAdapter.findStation(Station(stationAutoCompleteTextView.text.toString()))
            if (station != null) {
                stationAutoCompleteTextView.dismissKeybardAndClearFocus(requireActivity())
            }
        }
    }

    private fun onStationChanged(station: Station?, dissmissDropDown: Boolean) {
        if (station != null && station.name.length > 1) {
            stationAutoCompleteTextView.updateText(station.name)
            viewModel.setStation(station)

            if (dissmissDropDown) {
                stationAutoCompleteTextView.dismissKeybardAndClearFocus(requireActivity())
            }
        }
    }

    private fun clearFocus() {
        stationAutoCompleteTextView.clearFocus()
    }

    class PagerAdapter(
        private val context: Context,
        fm: FragmentManager
    ) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = PAGE_COUNT

        override fun getItem(position: Int): Fragment {
            return when (position) {
                PAGE_ARRIVALS -> ArrivalsFragment()
                PAGE_DEPARTURES -> DeparturesFragment()
                else -> throw IllegalStateException()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                PAGE_ARRIVALS -> context.getString(R.string.tab_arrivals)
                PAGE_DEPARTURES -> context.getString(R.string.tab_departures)
                else -> throw IllegalStateException()
            }
        }
    }

    companion object {
        const val PAGE_COUNT = 2
        const val PAGE_ARRIVALS = 0
        const val PAGE_DEPARTURES = 1
    }
}