package laurenyew.petfindersampleapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import laurenyew.petfindersampleapp.repository.models.AnimalModel
import laurenyew.petfindersampleapp.repository.networking.commands.SearchPetsCommands
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetSearchRepository @Inject constructor(
    private val searchPetCommand: SearchPetsCommands
) {
    suspend fun getNearbyDogs(location: String): LiveData<List<AnimalModel>> {
        val data = MutableLiveData<List<AnimalModel>>()
        try {
            data.value = searchPetCommand.searchForNearbyDogs(location)
        } catch (e: Exception) {

        }
        return data
    }
}