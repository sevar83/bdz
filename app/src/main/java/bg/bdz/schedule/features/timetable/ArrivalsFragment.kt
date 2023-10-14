@file:Suppress("COMPATIBILITY_WARNING")

package bg.bdz.schedule.features.timetable

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bg.bdz.schedule.R
import bg.bdz.schedule.databinding.FragmentTimetableBinding
import bg.bdz.schedule.di.Dependencies


class ArrivalsFragment : Fragment(R.layout.fragment_timetable) {

    private var _binding: FragmentTimetableBinding? = null
    private val binding: FragmentTimetableBinding get() = _binding!!

    private lateinit var viewModel: TimeTablesViewModel

    private lateinit var adapter: TrainStatusAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProvider(
            requireActivity().viewModelStore,
            TimeTablesViewModel.Factory(
                Dependencies.provideSimplePreferences(),
                Dependencies.provideSearchHistory(),
                requireActivity()
            )
        )[TimeTablesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainer.setOnRefreshListener(viewModel::refresh)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = TrainStatusAdapter(isArriving = true)
        recyclerView.adapter = adapter

        val divider = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(divider)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?): Unit = with(binding) {
        super.onActivityCreated(savedInstanceState)

        viewModel.refreshingArrivals.observe(viewLifecycleOwner) { isRefreshing ->
            swipeContainer.isRefreshing = isRefreshing
        }

        viewModel.arriving.observe(viewLifecycleOwner, ::render)
    }

    private fun render(timeTableState: TimeTableState) = with(binding) {
        when (timeTableState) {
            is TimeTableState.Loading -> {
                emptyView.root.isVisible = false
                errorTextView.isVisible = false
                progressBar.show()
                adapter.submitList(emptyList())
            }
            is TimeTableState.Success -> {
                errorTextView.isVisible = false
                progressBar.hide()
                adapter.submitList(timeTableState.trains)
                if (timeTableState.trains.isNotEmpty()) {
                    emptyView.root.isVisible = false
                    recyclerView.isVisible = true
                } else {
                    emptyView.root.isVisible = true
                    recyclerView.isVisible = false
                }
            }
            is TimeTableState.Error -> {
                emptyView.root.isVisible = false
                errorTextView.isVisible = true
                progressBar.hide()
                adapter.submitList(emptyList())
            }
        }
    }
}