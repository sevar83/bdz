package bg.bdz.schedule.features.timetable

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bg.bdz.schedule.R
import bg.bdz.schedule.di.Dependencies
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.include_empty.*



class ArrivalsFragment : Fragment(R.layout.fragment_timetable) {

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
        )
            .get(TimeTablesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = TrainStatusAdapter(isArriving = true)
        recyclerView.adapter = adapter

        val divider = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(divider)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.arriving.observe(viewLifecycleOwner, ::render)
    }

    private fun render(timeTableState: TimeTableState) {
        when (timeTableState) {
            is TimeTableState.Loading -> {
                emptyView.isVisible = false
                errorTextView.isVisible = false
                progressBar.show()
                adapter.submitList(emptyList())
            }
            is TimeTableState.Success -> {
                errorTextView.isVisible = false
                progressBar.hide()
                adapter.submitList(timeTableState.trains)
                if (timeTableState.trains.isNotEmpty()) {
                    emptyView.isVisible = false
                    recyclerView.isVisible = true
                } else {
                    emptyView.isVisible = true
                    recyclerView.isVisible = false
                }
            }
            is TimeTableState.Error -> {
                emptyView.isVisible = false
                errorTextView.isVisible = true
                progressBar.hide()
                adapter.submitList(emptyList())
            }
        }
    }
}