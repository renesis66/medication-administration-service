package com.healthcare.medication.web

import com.healthcare.medication.domain.*
import com.healthcare.medication.infrastructure.InMemoryAdministrationRepository
import com.healthcare.medication.web.dto.*
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

@MicronautTest(environments = ["test"])
class AdministrationControllerTest {
    
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient
    
    @Inject
    lateinit var repository: InMemoryAdministrationRepository
    
    @BeforeEach
    fun setup() {
        repository.clear()
    }
    
    @Test
    fun `should create new administration`() = runBlocking {
        val request = CreateAdministrationRequest(
            patientId = PatientId.generate().toString(),
            prescriptionId = PrescriptionId.generate().toString(),
            scheduledTime = Instant.now(),
            dosageGiven = 500.0,
            unit = "mg",
            notes = "Test administration"
        )
        
        val response = client.toBlocking().exchange(
            HttpRequest.POST("/medication/administrations", request),
            AdministrationResponse::class.java
        )
        
        assertEquals(HttpStatus.CREATED, response.status)
        assertNotNull(response.body())
        
        val administration = response.body()!!
        assertEquals(request.patientId, administration.patientId)
        assertEquals(request.prescriptionId, administration.prescriptionId)
        assertEquals(AdministrationStatus.SCHEDULED, administration.status)
    }
    
    @Test
    fun `should get administration by id`() = runBlocking {
        val savedAdministration = createTestAdministration()
        
        val response = client.toBlocking().exchange(
            HttpRequest.GET<Any>("/medication/administrations/${savedAdministration.administrationId}"),
            AdministrationResponse::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        
        val administration = response.body()!!
        assertEquals(savedAdministration.administrationId.toString(), administration.administrationId)
    }
    
    @Test
    fun `should return 404 for non-existent administration`() = runBlocking {
        val nonExistentId = AdministrationId.generate()
        
        try {
            client.toBlocking().exchange(
                HttpRequest.GET<Any>("/medication/administrations/$nonExistentId"),
                AdministrationResponse::class.java
            )
            fail("Expected HttpClientResponseException")
        } catch (e: io.micronaut.http.client.exceptions.HttpClientResponseException) {
            assertEquals(HttpStatus.NOT_FOUND, e.status)
        }
    }
    
    @Test
    fun `should get patient administrations`() = runBlocking {
        val patientId = PatientId.generate()
        createTestAdministration(patientId)
        createTestAdministration(patientId)
        createTestAdministration() // Different patient
        
        val response = client.toBlocking().exchange(
            HttpRequest.GET<Any>("/medication/patients/$patientId/administrations"),
            Array<AdministrationResponse>::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        
        val administrations = response.body()!!
        assertEquals(2, administrations.size)
        assertTrue(administrations.all { it.patientId == patientId.toString() })
    }
    
    @Test
    fun `should update administration status`() = runBlocking {
        val savedAdministration = createTestAdministration()
        val request = UpdateStatusRequest(AdministrationStatus.DELAYED)
        
        val response = client.toBlocking().exchange(
            HttpRequest.PUT("/medication/administrations/${savedAdministration.administrationId}/status", request),
            AdministrationResponse::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        
        val administration = response.body()!!
        assertEquals(AdministrationStatus.DELAYED, administration.status)
    }
    
    @Test
    fun `should record administration`() = runBlocking {
        val savedAdministration = createTestAdministration()
        val actualTime = Instant.now()
        val request = RecordAdministrationRequest(
            actualTime = actualTime,
            administeredBy = "Nurse Johnson",
            dosageGiven = 500.0,
            notes = "Patient tolerated well"
        )
        
        val response = client.toBlocking().exchange(
            HttpRequest.POST("/medication/administrations/${savedAdministration.administrationId}/record", request),
            AdministrationResponse::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        
        val administration = response.body()!!
        assertEquals(AdministrationStatus.COMPLETED, administration.status)
        assertEquals(actualTime, administration.actualTime)
        assertEquals("Nurse Johnson", administration.administeredBy)
    }
    
    @Test
    fun `should validate required fields in create request`() = runBlocking {
        val request = CreateAdministrationRequest(
            patientId = "", // Invalid - blank
            prescriptionId = PrescriptionId.generate().toString(),
            scheduledTime = Instant.now(),
            dosageGiven = 500.0,
            unit = "mg",
            notes = null
        )
        
        try {
            client.toBlocking().exchange(
                HttpRequest.POST("/medication/administrations", request),
                String::class.java
            )
            fail("Expected HttpClientResponseException for validation error")
        } catch (e: io.micronaut.http.client.exceptions.HttpClientResponseException) {
            assertEquals(HttpStatus.BAD_REQUEST, e.status)
        }
    }
    
    @Test
    fun `should get daily administrations`() = runBlocking {
        val today = LocalDate.now()
        createTestAdministration()
        createTestAdministration()
        
        val response = client.toBlocking().exchange(
            HttpRequest.GET<Any>("/medication/administrations/daily?date=$today"),
            Array<AdministrationResponse>::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        
        val administrations = response.body()!!
        assertEquals(2, administrations.size)
    }
    
    private suspend fun createTestAdministration(
        patientId: PatientId = PatientId.generate()
    ): MedicationAdministration {
        val administration = MedicationAdministration.create(
            patientId = patientId,
            prescriptionId = PrescriptionId.generate(),
            scheduledTime = Instant.now(),
            dosageGiven = 500.0,
            unit = "mg",
            administeredBy = null,
            notes = "Test administration"
        )
        return repository.save(administration)
    }
}