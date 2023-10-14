@file:Suppress("COMPATIBILITY_WARNING")

package bg.bdz.schedule.features.schedule

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bg.bdz.schedule.R
import bg.bdz.schedule.databinding.FragmentScheduleBinding
import bg.bdz.schedule.di.Dependencies
import bg.bdz.schedule.features.datepicker.DatePickerFragment
import bg.bdz.schedule.features.stations.SearchHistory
import bg.bdz.schedule.features.stations.StationsAdapter
import bg.bdz.schedule.models.Station
import bg.bdz.schedule.network.Bdz
import bg.bdz.schedule.utils.KeyboardEventListener
import bg.bdz.schedule.utils.SeparateOnScrollDownListener
import bg.bdz.schedule.utils.setOnSingleClickListener
import bg.bdz.schedule.utils.updateText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding: FragmentScheduleBinding get() = _binding!!

    private lateinit var adapter: TrainAdapter

    private lateinit var fromAdapter: StationsAdapter
    private lateinit var toAdapter: StationsAdapter

    private val viewModel: ScheduleViewModel by viewModels {
        ScheduleViewModel.Factory(
            Dependencies.provideSimplePreferences(),
            Dependencies.provideSearchHistory(),
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        // SwipeToRefresh

        swipeContainer.setOnRefreshListener(viewModel::refresh)

        // From/To fields

        initializeFromSearchView()
        initializeToSearchView()

        // Date field

        dateEditText.setOnSingleClickListener {
            viewModel.date.value?.let { date ->
                DatePickerFragment
                    .newInstance(date)
                    .apply { setTargetFragment(this@ScheduleFragment, RC_PICK_DATE) }
                    .show(requireFragmentManager(), "date")
            }
        }

        // Swap button

        swapButton.setOnClickListener { viewModel.onSwapButtonClicked() }

        // List

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrainAdapter()
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(SeparateOnScrollDownListener(separator))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(binding) {
            viewModel.refreshing.observe(viewLifecycleOwner) { isRefreshing ->
                swipeContainer.isRefreshing = isRefreshing
            }

            viewModel.date.observe(viewLifecycleOwner, ::renderDate)
            viewModel.schedule.observe(viewLifecycleOwner, ::renderSchedule)

            viewModel.fromStation.observe(viewLifecycleOwner) { station ->
                fromSearchView.updateText(station.name)
                fromSearchView.approveAsValid()
            }

            viewModel.toStation.observe(viewLifecycleOwner) { station ->
                toSearchView.updateText(station.name)
                toSearchView.approveAsValid()
            }

            // FIXME disabled until StationsAdapter sync problems are fixed
            /*viewModel.recentSearches.observe(viewLifecycleOwner) { searches ->
                fromAdapter.setRecentSearches(searches)
                toAdapter.setRecentSearches(searches)
            }*/
        }
    }

    private fun renderDate(date: LocalDate) = with(binding) {
        val dateText = DATE_FORMATTER.format(date)
        when (date) {
            // Today
            LocalDate.now() -> dateEditText.updateText(getString(R.string.today_s, dateText))
            // Tomorrow
            LocalDate.now().plusDays(1) -> dateEditText.updateText(getString(R.string.tomorrow_s, dateText))
            // Some other day
            else -> dateEditText.updateText(dateText)
        }
    }

    private fun initializeFromSearchView() = with(binding) {
        fromAdapter = StationsAdapter(
            context = requireActivity(),
            originalItems = Bdz.stations,
            maxRecentsCount = SearchHistory.MAX_RECENTS
        )
        fromSearchView.setAdapter(fromAdapter)
        fromSearchView.isDropDownAlwaysVisible = true
        fromSearchView.onActionListener = {
            val station = fromAdapter.findStation(fromSearchView.text.toString())
            updateFromStation(station)
        }
        fromSearchView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val station = fromAdapter.getItem(position)
            updateFromStation(station)
        }
        fromSearchView.setOnClickListener {
            fromSearchView.refreshFiltering()
            fromSearchView.showDropDown()
        }
        fromSearchView.setOnDismissListener {
            val station = fromAdapter.findStation(fromSearchView.text.toString())
            if (station != null) {
                fromSearchView.dismissKeybardAndClearFocus(requireActivity())
            }
        }
    }

    private fun initializeToSearchView() = with(binding) {
        toAdapter = StationsAdapter(
            context = requireActivity(),
            originalItems = Bdz.stations,
            maxRecentsCount = SearchHistory.MAX_RECENTS
        )
        toSearchView.setAdapter(toAdapter)
        toSearchView.isDropDownAlwaysVisible = true
        toSearchView.onActionListener = {
            val station = toAdapter.findStation(toSearchView.text.toString())
            updateToStation(station)
        }
        toSearchView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val station = toAdapter.getItem(position)
            updateToStation(station)
        }
        toSearchView.setOnClickListener {
            toSearchView.refreshFiltering()
            toSearchView.showDropDown()
        }
        toSearchView.setOnDismissListener {
            val station = toAdapter.findStation(toSearchView.text.toString())
            if (station != null) {
                toSearchView.dismissKeybardAndClearFocus(requireActivity())
            }
        }
    }

    private fun updateFromStation(station: Station?) = with(binding) {
        if (station != null && station.name.length > 1) {
            viewModel.onFromStationChanged(station)
            fromSearchView.dismissKeybardAndClearFocus(requireActivity())
        }
    }

    private fun updateToStation(station: Station?) = with(binding) {
        if (station != null && station.name.length > 1) {
            viewModel.onToStationChanged(station)
            toSearchView.dismissKeybardAndClearFocus(requireActivity())
        }
    }

    private fun clearFocus() = with(binding) {
        fromSearchView.clearFocus()
        toSearchView.clearFocus()
        dateEditText.clearFocus()
    }

    override fun onResume() {
        super.onResume()

        clearFocus()

        KeyboardEventListener(requireActivity(), lifecycle) { keyboardShown ->
            if (!keyboardShown) {
                clearFocus()
            }
        }
    }

    private fun renderSchedule(schedule: ScheduleState) = with(binding) {
        when (schedule) {
            is ScheduleState.Loading -> {
                errorTextView.isVisible = false
                progressBar.show()
                emptyView.root.isVisible = false
                adapter.submitList(emptyList())
            }
            is ScheduleState.Success -> {
                errorTextView.isVisible = false
                progressBar.hide()

                // A hack to force layout of items in order to update their old heights. Should have a better way!
                adapter.notifyDataSetChanged()

                adapter.submitList(schedule.trains)
                if (schedule.trains.isNotEmpty()) {
                    emptyView.root.isVisible = false
                    recyclerView.isVisible = true
                } else {
                    emptyView.root.isVisible = true
                    recyclerView.isVisible = false
                }
            }
            is ScheduleState.Error -> {
                errorTextView.isVisible = true
                progressBar.hide()
                emptyView.root.isVisible = false
                adapter.submitList(emptyList())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == RC_PICK_DATE && data != null) {
                val date: LocalDate = data.getSerializableExtra(DatePickerFragment.ARG_DATE) as LocalDate
                viewModel.onDateChanged(date)
        }
    }

    companion object {
        const val RC_PICK_DATE = 1234

        private val DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    }
}