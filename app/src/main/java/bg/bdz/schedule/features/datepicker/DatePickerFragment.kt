package bg.bdz.schedule.features.datepicker

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import bg.bdz.schedule.utils.withArgs
import org.threeten.bp.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = requireArguments().getSerializable(ARG_DATE) as LocalDate
        val dialog = DatePickerDialog(requireContext(), this, date.year, date.monthValue - 1, date.dayOfMonth)
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000

        return dialog
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        val date = LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
        val resultIntent = Intent().putExtra(ARG_DATE, date)
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, resultIntent)
    }

    companion object {
        fun newInstance(date: LocalDate) = DatePickerFragment().withArgs(ARG_DATE to date)

        const val ARG_DATE: String = "date"     // LocalDate
    }
}