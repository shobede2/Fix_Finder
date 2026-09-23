package com.example.fix_finder

import com.example.fix_finder.data.api.FixFinderApiService
import com.example.fix_finder.data.model.*
import com.example.fix_finder.data.repository.ResultState
import com.example.fix_finder.data.repository.ServiceRepository
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ServiceRepositoryTest {

    private lateinit var mockApiService: FakeServiceApiService
    private lateinit var repository: ServiceRepository

    @Before
    fun setUp() {
        mockApiService = FakeServiceApiService()
        repository = ServiceRepository(mockApiService)
    }

    @Test
    fun getCategories_returnsSuccessWithList() = runTest {
        mockApiService.categoriesResponse = Response.success(
            listOf(
                ServiceCategory("plumbing", "Plumbing", "ic_plumbing", "Plumbing services"),
                ServiceCategory("electrical", "Electrical", "ic_electrical", "Electrical services")
            )
        )

        val result = repository.getCategories()
        assertTrue(result is ResultState.Success)
        val categories = (result as ResultState.Success).data
        assertEquals(2, categories.size)
        assertEquals("Plumbing", categories[0].name)
    }

    @Test
    fun getProviders_withQuery_returnsMatchingProviders() = runTest {
        mockApiService.providersResponse = Response.success(
            listOf(
                ServiceProvider(
                    id = "tech_1",
                    name = "Thabo Mokoena",
                    avatarName = "avatar_thabo",
                    categoryId = "plumbing",
                    categoryName = "Plumbing",
                    rating = 4.8,
                    reviewsCount = 126,
                    yearsExperience = 8,
                    location = "Polokwane",
                    startingPriceDisplay = "R350",
                    jobsCompleted = 342,
                    responseTime = "1hr",
                    bio = "Plumber bio"
                )
            )
        )

        val result = repository.getProviders(query = "Thabo")
        assertTrue(result is ResultState.Success)
        val providers = (result as ResultState.Success).data
        assertEquals(1, providers.size)
        assertEquals("Thabo Mokoena", providers[0].name)
    }

    @Test
    fun createServiceRequest_withBlankDescription_returnsError() = runTest {
        val request = ServiceRequestCreate(
            userId = "usr_1",
            userName = "User",
            providerId = "tech_1",
            providerName = "Thabo",
            providerCategory = "Plumbing",
            serviceName = "Tap Fix",
            description = "",
            address = "12 Main St",
            date = "2025-05-10",
            time = "10:00",
            estimatedPrice = "R350"
        )

        val result = repository.createServiceRequest(request)
        assertTrue(result is ResultState.Error)
        assertEquals("Please describe the problem or service required.", (result as ResultState.Error).message)
    }

    @Test
    fun createServiceRequest_withValidData_returnsSuccess() = runTest {
        val requestPayload = ServiceRequestCreate(
            userId = "usr_1",
            userName = "User",
            providerId = "tech_1",
            providerName = "Thabo",
            providerCategory = "Plumbing",
            serviceName = "Tap Fix",
            description = "Pipe burst in kitchen",
            address = "12 Main St, Polokwane",
            date = "2025-05-10",
            time = "10:00",
            estimatedPrice = "R350"
        )

        mockApiService.createRequestResponse = Response.success(
            ServiceRequest(
                id = "REQ-101",
                userId = "usr_1",
                userName = "User",
                providerId = "tech_1",
                providerName = "Thabo",
                providerCategory = "Plumbing",
                serviceName = "Tap Fix",
                description = "Pipe burst in kitchen",
                address = "12 Main St, Polokwane",
                date = "2025-05-10",
                time = "10:00",
                estimatedPrice = "R350",
                status = ServiceRequest.STATUS_PENDING,
                createdAt = "Just now"
            )
        )

        val result = repository.createServiceRequest(requestPayload)
        assertTrue(result is ResultState.Success)
        val created = (result as ResultState.Success).data
        assertEquals("REQ-101", created.id)
        assertEquals(ServiceRequest.STATUS_PENDING, created.status)
    }

    class FakeServiceApiService : FixFinderApiService {
        var categoriesResponse: Response<List<ServiceCategory>>? = null
        var providersResponse: Response<List<ServiceProvider>>? = null
        var createRequestResponse: Response<ServiceRequest>? = null

        override suspend fun login(request: LoginRequest): Response<LoginResponse> = Response.error(500, "".toResponseBody())
        override suspend fun register(request: RegisterRequest): Response<LoginResponse> = Response.error(500, "".toResponseBody())
        override suspend fun getCategories(): Response<List<ServiceCategory>> = categoriesResponse ?: Response.error(500, "".toResponseBody())
        override suspend fun getProviders(query: String?, category: String?, location: String?): Response<List<ServiceProvider>> = providersResponse ?: Response.error(500, "".toResponseBody())
        override suspend fun getProviderById(id: String): Response<ServiceProvider> = Response.error(404, "".toResponseBody())
        override suspend fun createServiceRequest(request: ServiceRequestCreate): Response<ServiceRequest> = createRequestResponse ?: Response.error(500, "".toResponseBody())
        override suspend fun getUserRequests(userId: String): Response<List<ServiceRequest>> = Response.success(emptyList())
        override suspend fun updateSettings(settings: UserSettings): Response<UserSettings> = Response.success(settings)
    }
}
