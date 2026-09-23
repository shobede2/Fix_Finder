package com.example.fix_finder.data.api

import com.example.fix_finder.data.model.*
import com.example.fix_finder.data.repository.AuthRepository
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.util.UUID

class MockInterceptor : Interceptor {

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // In-memory data store for requests and users
    private val registeredUsersMap = mutableMapOf<String, User>()

    private val mockCategories = listOf(
        ServiceCategory("plumbing", "Plumbing", "ic_plumbing", "Pipe repairs, leak fixes, drain cleaning & installation"),
        ServiceCategory("electrical", "Electrical", "ic_electrical", "Wiring, light fixtures, DB board repairs & fault finding"),
        ServiceCategory("appliances", "Appliances", "ic_appliances", "Washing machine, fridge, stove & microwave repairs"),
        ServiceCategory("ac", "Air Conditioning", "ic_ac", "AC installation, gas refills, servicing & HVAC repair"),
        ServiceCategory("electronics", "Electronics", "ic_electronics", "TV, audio systems & smart home equipment repair"),
        ServiceCategory("automotive", "Automotive", "ic_automotive", "Car battery replacement, minor mechanics & detailing"),
        ServiceCategory("handyman", "Handyman", "ic_handyman", "General home repairs, furniture assembly & mounting"),
        ServiceCategory("other", "Other", "ic_other", "Custom maintenance & specialized repair services")
    )

    private val mockProviders = mutableListOf(
        ServiceProvider(
            id = "tech_1",
            name = "Thabo Mokoena",
            avatarName = "avatar_thabo",
            categoryId = "plumbing",
            categoryName = "Plumbing",
            rating = 4.8,
            reviewsCount = 126,
            yearsExperience = 8,
            location = "Polokwane Central, Limpopo",
            startingPriceDisplay = "R350",
            jobsCompleted = 342,
            responseTime = "~1 hour",
            bio = "Master plumber specializing in emergency repairs, tap replacement, leak detection, and geyser installations. Dedicated to providing fast, clean, and reliable service across Polokwane.",
            isVerified = true,
            services = listOf(
                ServiceOffer("srv_101", "Tap Replacement", "Standard kitchen or bathroom tap replacement", 350.0, "R350"),
                ServiceOffer("srv_102", "Leaking Pipe Fix", "Diagnostic and pipe patch/replacement", 500.0, "R500"),
                ServiceOffer("srv_103", "Geyser Repair & Service", "Thermostat, element replacement & valve check", 850.0, "R850")
            ),
            reviews = listOf(
                ProviderReview("rev_1", "Kagiso N.", 5.0, "Thabo arrived on time and fixed my leaking kitchen sink in less than an hour. Great work!", "2 days ago"),
                ProviderReview("rev_2", "Sarah V.", 4.5, "Very professional and fair pricing. Highly recommended for plumbing issues.", "1 week ago")
            )
        ),
        ServiceProvider(
            id = "tech_2",
            name = "Sipho Khumalo",
            avatarName = "avatar_sipho",
            categoryId = "plumbing",
            categoryName = "Plumbing",
            rating = 4.9,
            reviewsCount = 98,
            yearsExperience = 6,
            location = "Polokwane West, Limpopo",
            startingPriceDisplay = "R400",
            jobsCompleted = 120,
            responseTime = "~30 mins",
            bio = "Licensed plumber providing 24/7 emergency response for residential and commercial properties. Specializing in drain unblocking, pipe burst repairs, and full bathroom installations.",
            isVerified = true,
            services = listOf(
                ServiceOffer("srv_201", "Unblock Drain", "High-pressure drain jetting and clearage", 400.0, "R400"),
                ServiceOffer("srv_202", "Emergency Burst Pipe", "Immediate arrival and rapid repair", 750.0, "R750")
            ),
            reviews = listOf(
                ProviderReview("rev_3", "David M.", 5.0, "Saved our home during a midnight pipe burst! Excellent service.", "3 days ago")
            )
        ),
        ServiceProvider(
            id = "tech_3",
            name = "Lerato Ndlovu",
            avatarName = "avatar_lerato",
            categoryId = "electrical",
            categoryName = "Electrical",
            rating = 4.9,
            reviewsCount = 142,
            yearsExperience = 9,
            location = "Bendor, Polokwane",
            startingPriceDisplay = "R380",
            jobsCompleted = 280,
            responseTime = "~45 mins",
            bio = "Certified electrician with over 9 years experience in residential fault finding, solar power setup, backup generator integration, and DB board installations.",
            isVerified = true,
            services = listOf(
                ServiceOffer("srv_301", "DB Board Inspection", "Comprehensive safety check and breaker replacement", 380.0, "R380"),
                ServiceOffer("srv_302", "Lighting Installation", "Indoor & outdoor light fixture and LED wiring", 450.0, "R450"),
                ServiceOffer("srv_303", "Inverter & Solar Prep", "Inverter wiring and surge protection installation", 1200.0, "R1200")
            ),
            reviews = listOf(
                ProviderReview("rev_4", "Michael S.", 5.0, "Lerato fixed our entire power triplication issue quickly. Solved what 2 others couldn't!", "4 days ago")
            )
        ),
        ServiceProvider(
            id = "tech_4",
            name = "Jacob van der Merwe",
            avatarName = "avatar_thabo",
            categoryId = "ac",
            categoryName = "Air Conditioning",
            rating = 4.7,
            reviewsCount = 84,
            yearsExperience = 7,
            location = "Flora Park, Polokwane",
            startingPriceDisplay = "R450",
            jobsCompleted = 195,
            responseTime = "~2 hours",
            bio = "HVAC technician specializing in split-unit aircon installations, gas refilling, filter deep cleaning, and heating/cooling maintenance.",
            isVerified = true,
            services = listOf(
                ServiceOffer("srv_401", "AC Service & Gas Top-up", "Complete cleaning and refrigerant gas fill", 450.0, "R450"),
                ServiceOffer("srv_402", "Split-Unit AC Install", "Wall mount and external unit wiring/piping", 1500.0, "R1500")
            ),
            reviews = listOf(
                ProviderReview("rev_5", "Annemarie B.", 4.5, "Cooling works like brand new now. Clean and efficient work.", "2 weeks ago")
            )
        ),
        ServiceProvider(
            id = "tech_5",
            name = "Bongani Cele",
            avatarName = "avatar_sipho",
            categoryId = "appliances",
            categoryName = "Appliances",
            rating = 4.6,
            reviewsCount = 65,
            yearsExperience = 5,
            location = "Sterpark, Polokwane",
            startingPriceDisplay = "R300",
            jobsCompleted = 150,
            responseTime = "~1 hour",
            bio = "Appliance repair specialist for all major brands (Samsung, LG, Whirlpool, Defy). Fixes fridges, washing machines, tumble dryers, and ovens.",
            isVerified = true,
            services = listOf(
                ServiceOffer("srv_501", "Washing Machine Repair", "Drum, pump, or electrical fix", 350.0, "R350"),
                ServiceOffer("srv_502", "Fridge Cooling Repair", "Compressor check & gas refill", 500.0, "R500")
            ),
            reviews = listOf(
                ProviderReview("rev_6", "Grace K.", 4.8, "Fixed my washing machine on the same day. Very happy!", "5 days ago")
            )
        )
    )

    private val mockRequests = mutableListOf<ServiceRequest>(
        ServiceRequest(
            id = "REQ-1001",
            userId = "usr_demo",
            userName = "Demo User",
            providerId = "tech_1",
            providerName = "Thabo Mokoena",
            providerCategory = "Plumbing",
            serviceName = "Tap Replacement",
            description = "Kitchen sink tap is leaking severely around the spout joint.",
            address = "12 Market Street, Polokwane",
            date = "2025-05-10",
            time = "10:00 AM",
            estimatedPrice = "R350",
            status = ServiceRequest.STATUS_ACCEPTED,
            createdAt = "2025-05-08"
        ),
        ServiceRequest(
            id = "REQ-1002",
            userId = "usr_demo",
            userName = "Demo User",
            providerId = "tech_3",
            providerName = "Lerato Ndlovu",
            providerCategory = "Electrical",
            serviceName = "Lighting Installation",
            description = "Installing 4 new outdoor security lights on patio.",
            address = "12 Market Street, Polokwane",
            date = "2025-05-12",
            time = "02:00 PM",
            estimatedPrice = "R450",
            status = ServiceRequest.STATUS_PENDING,
            createdAt = "2025-05-09"
        )
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        return when {
            // AUTH LOGIN
            path.endsWith("/api/auth/login") && method == "POST" -> {
                val bodyString = request.body?.readToString() ?: ""
                val loginReq = try { gson.fromJson(bodyString, LoginRequest::class.java) } catch (e: Exception) { null }

                val pwdErr = AuthRepository.validatePassword(loginReq?.password.orEmpty())
                if (loginReq == null || loginReq.email.isBlank() || loginReq.password.isBlank()) {
                    createJsonResponse(request, 400, gson.toJson(LoginResponse(false, "Email and password are required.")))
                } else if (pwdErr != null) {
                    createJsonResponse(request, 400, gson.toJson(LoginResponse(false, pwdErr)))
                } else {
                    val cleanEmail = loginReq.email.trim().lowercase()
                    val existingUser = registeredUsersMap[cleanEmail]
                    val isTech = cleanEmail.contains("tech") || cleanEmail.contains("thabo")

                    val derivedName = if (isTech) {
                        "Thabo Mokoena"
                    } else {
                        val username = cleanEmail.substringBefore("@").replace(".", " ").replace("_", " ").replace("-", " ")
                        username.split(" ").filter { it.isNotBlank() }.joinToString(" ") { word ->
                            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }.ifBlank { "User" }
                    }

                    val user = existingUser ?: User(
                        id = if (isTech) "tech_1" else "usr_" + UUID.randomUUID().toString().take(8),
                        name = derivedName,
                        email = loginReq.email,
                        role = if (isTech) User.ROLE_TECHNICIAN else User.ROLE_CUSTOMER,
                        phone = "082 555 1234",
                        location = "Polokwane, Limpopo"
                    )

                    val response = LoginResponse(
                        success = true,
                        message = "Sign-in successful",
                        token = "token_" + UUID.randomUUID().toString().take(12),
                        user = user
                    )
                    createJsonResponse(request, 200, gson.toJson(response))
                }
            }

            // AUTH REGISTER
            path.endsWith("/api/auth/register") && method == "POST" -> {
                val bodyString = request.body?.readToString() ?: ""
                val regReq = try { gson.fromJson(bodyString, RegisterRequest::class.java) } catch (e: Exception) { null }

                val pwdErr = AuthRepository.validatePassword(regReq?.password.orEmpty())
                if (regReq == null || regReq.email.isBlank() || regReq.name.isBlank() || regReq.password.isBlank()) {
                    createJsonResponse(request, 400, gson.toJson(LoginResponse(false, "Please fill in all required fields.")))
                } else if (!regReq.email.contains("@")) {
                    createJsonResponse(request, 400, gson.toJson(LoginResponse(false, "Please enter a valid email address.")))
                } else if (pwdErr != null) {
                    createJsonResponse(request, 400, gson.toJson(LoginResponse(false, pwdErr)))
                } else {
                    val newUser = User(
                        id = "usr_" + UUID.randomUUID().toString().take(8),
                        name = regReq.name.trim(),
                        email = regReq.email.trim(),
                        role = regReq.role,
                        phone = regReq.phone.ifBlank { "082 000 0000" },
                        location = regReq.location.ifBlank { "Polokwane, Limpopo" }
                    )
                    registeredUsersMap[regReq.email.trim().lowercase()] = newUser

                    val response = LoginResponse(
                        success = true,
                        message = "Account created successfully",
                        token = "token_" + UUID.randomUUID().toString().take(12),
                        user = newUser
                    )
                    createJsonResponse(request, 200, gson.toJson(response))
                }
            }

            // GET CATEGORIES
            path.endsWith("/api/categories") && method == "GET" -> {
                createJsonResponse(request, 200, gson.toJson(mockCategories))
            }

            // GET PROVIDERS LIST
            path.endsWith("/api/providers") && method == "GET" -> {
                val query = request.url.queryParameter("query")?.trim()?.lowercase()
                val category = request.url.queryParameter("category")?.trim()?.lowercase()
                val location = request.url.queryParameter("location")?.trim()?.lowercase()

                var filtered = mockProviders.toList()

                if (!category.isNullOrBlank() && category != "all") {
                    filtered = filtered.filter { 
                        it.categoryId.lowercase() == category || it.categoryName.lowercase() == category 
                    }
                }

                if (!query.isNullOrBlank()) {
                    filtered = filtered.filter {
                        it.name.lowercase().contains(query) ||
                        it.categoryName.lowercase().contains(query) ||
                        it.bio.lowercase().contains(query) ||
                        it.services.any { s -> s.name.lowercase().contains(query) }
                    }
                }

                if (!location.isNullOrBlank()) {
                    filtered = filtered.filter {
                        it.location.lowercase().contains(location)
                    }
                }

                createJsonResponse(request, 200, gson.toJson(filtered))
            }

            // GET PROVIDER BY ID
            path.contains("/api/providers/") && method == "GET" -> {
                val id = path.substringAfterLast("/api/providers/").trim()
                val provider = mockProviders.find { it.id == id }
                if (provider != null) {
                    createJsonResponse(request, 200, gson.toJson(provider))
                } else {
                    createJsonResponse(request, 404, "{\"error\": \"Provider not found\"}")
                }
            }

            // CREATE SERVICE REQUEST
            path.endsWith("/api/requests") && method == "POST" -> {
                val bodyString = request.body?.readToString() ?: ""
                val reqData = try { gson.fromJson(bodyString, ServiceRequestCreate::class.java) } catch (e: Exception) { null }

                if (reqData == null || reqData.description.isBlank() || reqData.address.isBlank()) {
                    createJsonResponse(request, 400, "{\"error\": \"Description and location address are required.\"}")
                } else {
                    val newRequest = ServiceRequest(
                        id = "REQ-" + (1000 + mockRequests.size + 1),
                        userId = reqData.userId,
                        userName = reqData.userName,
                        providerId = reqData.providerId,
                        providerName = reqData.providerName,
                        providerCategory = reqData.providerCategory,
                        serviceName = reqData.serviceName,
                        description = reqData.description,
                        address = reqData.address,
                        date = reqData.date,
                        time = reqData.time,
                        estimatedPrice = reqData.estimatedPrice,
                        status = ServiceRequest.STATUS_PENDING,
                        createdAt = "Just now"
                    )
                    mockRequests.add(0, newRequest)
                    createJsonResponse(request, 200, gson.toJson(newRequest))
                }
            }

            // GET USER SERVICE REQUESTS
            path.contains("/api/requests/user/") && method == "GET" -> {
                val userId = path.substringAfterLast("/api/requests/user/").trim()
                val userReqs = mockRequests.filter { it.userId == userId || userId == "usr_demo" }
                createJsonResponse(request, 200, gson.toJson(userReqs))
            }

            // UPDATE SETTINGS
            path.endsWith("/api/user/settings") && method == "PUT" -> {
                val bodyString = request.body?.readToString() ?: ""
                val settings = try { gson.fromJson(bodyString, UserSettings::class.java) } catch (e: Exception) { null }
                if (settings != null) {
                    createJsonResponse(request, 200, gson.toJson(settings))
                } else {
                    createJsonResponse(request, 400, "{\"error\": \"Invalid settings payload\"}")
                }
            }

            else -> {
                chain.proceed(request)
            }
        }
    }

    private fun createJsonResponse(request: Request, statusCode: Int, jsonBody: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(statusCode)
            .message(if (statusCode == 200) "OK" else "Error")
            .body(jsonBody.toResponseBody(jsonMediaType))
            .build()
    }

    private fun RequestBody.readToString(): String {
        val buffer = Buffer()
        this.writeTo(buffer)
        return buffer.readUtf8()
    }
}
