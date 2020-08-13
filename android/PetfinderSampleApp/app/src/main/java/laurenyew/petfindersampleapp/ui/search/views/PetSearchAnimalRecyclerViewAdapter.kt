package laurenyew.petfindersampleapp.ui.search.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import laurenyew.petfindersampleapp.R
import laurenyew.petfindersampleapp.repository.models.AnimalModel
import java.util.*
import kotlin.collections.ArrayList

class PetSearchAnimalRecyclerViewAdapter :
    RecyclerView.Adapter<AnimalViewHolder>() {

    private val job = Job()
    private var data: MutableList<AnimalModel> = ArrayList()
    private var pendingDataUpdates = ArrayDeque<List<AnimalModel>>()

    private val scope = CoroutineScope(Dispatchers.Default + job)

    //RecyclerView Diff.Util (List Updates)
    fun updateData(newData: List<AnimalModel>?) {
        if (scope.isActive) {
            val data = newData ?: ArrayList()
            pendingDataUpdates.add(data)
            if (pendingDataUpdates.size <= 1) {
                updateDataInternal(data)
            }
        }
    }

    //If the adapter is destroyed, cancel any running jobs
    fun onDestroy() {
        job.cancel()
        pendingDataUpdates.clear()
    }

    /**
     * Handle the diff util update on a background thread
     * (this can take O(n) time so we don't want it on the main thread)
     */
    private fun updateDataInternal(newData: List<AnimalModel>?) {
        val oldData = ArrayList(data)

        scope.launch {
            val diffCallback = createDataDiffCallback(oldData, newData)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            if (isActive) {
                withContext(Dispatchers.Main) {
                    applyDataDiffResult(newData, diffResult)
                }
            }
        }
    }

    /**
     * UI thread callback to apply the diff result to the adapter
     * and take in the latest update
     */
    private fun applyDataDiffResult(newData: List<AnimalModel>?, diffResult: DiffUtil.DiffResult) {
        if (pendingDataUpdates.isNotEmpty()) {
            pendingDataUpdates.remove()
        }

        //Apply the data to the view
        data.clear()
        if (newData != null) {
            data.addAll(newData)
        }
        diffResult.dispatchUpdatesTo(this)

        //Take in the next latest update
        if (pendingDataUpdates.isNotEmpty()) {
            val latestDataUpdate = pendingDataUpdates.pop()
            pendingDataUpdates.clear()
            updateDataInternal(latestDataUpdate)
        }
    }

    private fun createDataDiffCallback(
        oldData: List<AnimalModel>?,
        newData: List<AnimalModel>?
    ): DiffUtil.Callback =
        AnimalDataDiffCallback(oldData, newData)
    //endregion

    //region RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_result_preview_view, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val item = data[position]
        val context = holder.itemView.context
        holder.nameTextView.text = item.name ?: "Name TBD"
        val age = item.age ?: context.getString(R.string.unknown)
        val sex = item.sex ?: context.getString(R.string.unknown)
        val size = item.size ?: context.getString(R.string.unknown)
        holder.basicInfoTextView.text = context.getString(R.string.basic_info_formatted_string, age, sex, size)
        holder.descriptionTextView.text = item.description
        Picasso.get()
            .load(item.photoUrl)
            .fit()
            .error(R.drawable.ic_baseline_image_24)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int = data.size
//endregion
}