package io.github.castrors.githubarchkotlin.ui.list

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.github.castrors.githubarchkotlin.data.GithubRepository
import io.github.castrors.githubarchkotlin.data.database.Owner
import io.github.castrors.githubarchkotlin.data.database.Repo
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    @Mock
    private lateinit var repository: GithubRepository
    @Mock
    lateinit var observer: Observer<List<Repo>>
    @Mock
    lateinit var observerLoading: Observer<Boolean>

    val liveData: MutableLiveData<List<Repo>> = MutableLiveData()
    lateinit var viewModel: MainActivityViewModel



    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = MainActivityViewModel(repository)
    }

    @Test
    fun shouldFetchRepositoriesWithSuccess() {
        liveData.value = listOf(createDummyItem())
        `when`(repository.githubRepositoriesList).thenReturn(liveData)

        viewModel.isLoadingLiveData.observeForever(observerLoading)
        viewModel.getRepoList().observeForever(observer)

        verify(observerLoading).onChanged(false)
        verify(observer).onChanged(liveData.value)
    }

    private fun createDummyItem() = Repo(1, "name", "fullName", Owner("login", "url"), "description", 1, 1)

}
