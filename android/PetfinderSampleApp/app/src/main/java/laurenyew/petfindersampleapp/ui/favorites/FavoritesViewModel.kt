package laurenyew.petfindersampleapp.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import laurenyew.petfindersampleapp.repository.PetSearchRepository
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val repository: PetSearchRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Favorites Fragment"
    }
    val text: LiveData<String> = _text
}