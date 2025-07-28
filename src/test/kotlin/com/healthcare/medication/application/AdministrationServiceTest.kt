package com.healthcare.medication.application

import com.healthcare.medication.domain.*
import com.healthcare.medication.infrastructure.InMemoryAdministrationRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

@MicronautTest(environments = ["test"])
class AdministrationServiceTest {
    
    @Inject
    lateinit var administrationService: AdministrationService
    
    @Inject
    lateinit var repository: InMemoryAdministrationRepository
    
    @BeforeEach
    fun setup() {
        repository.clear()
    }
    
    @Test
    fun `should create new administration`() = runBlocking {
        val patientId = PatientId.generate()
        val prescriptionId = PrescriptionId.generate()
        val scheduledTime = Instant.now()
        
        val administration = administrationService.createAdministration(
            patientId = patientId,
            prescriptionId = prescriptionId,
            scheduledTime = scheduledTime,
            dosageGiven = 500.0,
            unit = "mg",
            notes = "Test administration"
        )
        
        assertEquals(patientId, administration.patientId)
        assertEquals(prescriptionId, administration.prescriptionId)
        assertEquals(AdministrationStatus.SCHEDULED, administration.status)
        assertEquals(500.0, administration.dosageGiven)
    }
    
    @Test
    fun `should record administration completion`() = runBlocking {
        val administration = createTestAdministration()
        val actualTime = Instant.now()
        val administeredBy = "Nurse Johnson"
        
        val recordedAdministration = administrationService.recordAdministration(
            administrationId = administration.administrationId,
            actualTime = actualTime,
            administeredBy = administeredBy,
            dosageGiven = 500.0,
            notes = "Patient tolerated well"
        )
        
        assertEquals(AdministrationStatus.COMPLETED, recordedAdministration.status)
        assertEquals(actualTime, recordedAdministration.actualTime)
        assertEquals(administeredBy, recordedAdministration.administeredBy)
        assertEquals("Patient tolerated well", recordedAdministration.notes)
    }
    
    @Test
    fun `should update administration status`() = runBlocking {
        val administration = createTestAdministration()
        
        val updatedAdministration = administrationService.updateAdministrationStatus(
            administration.administrationId,
            AdministrationStatus.DELAYED
        )
        
        assertEquals(AdministrationStatus.DELAYED, updatedAdministration.status)
    }
    
    @Test
    fun `should get administration by id`() = runBlocking {
        val administration = createTestAdministration()
        
        val retrievedAdministration = administrationService.getAdministration(administration.administrationId)
        
        assertEquals(administration.administrationId, retrievedAdministration.administrationId)
        assertEquals(administration.patientId, retrievedAdministration.patientId)
    }
    
    @Test
    fun `should throw exception when administration not found`() = runBlocking {
        val nonExistentId = AdministrationId.generate()
        
        assertThrows(AdministrationNotFoundException::class.java) {
            runBlocking {
                administrationService.getAdministration(nonExistentId)
            }
        }
    }
    
    @Test
    fun `should get patient administrations`() = runBlocking {
        val patientId = PatientId.generate()
        val administration1 = createTestAdministration(patientId)
        val administration2 = createTestAdministration(patientId)
        createTestAdministration() // Different patient
        
        val patientAdministrations = administrationService.getPatientAdministrations(patientId)
        
        assertEquals(2, patientAdministrations.size)
        assertTrue(patientAdministrations.any { it.administrationId == administration1.administrationId })
        assertTrue(patientAdministrations.any { it.administrationId == administration2.administrationId })
    }
    
    @Test
    fun `should get prescription administrations`() = runBlocking {
        val prescriptionId = PrescriptionId.generate()
        val administration1 = createTestAdministration(prescriptionId = prescriptionId)
        val administration2 = createTestAdministration(prescriptionId = prescriptionId)
        createTestAdministration() // Different prescription
        
        val prescriptionAdministrations = administrationService.getPrescriptionAdministrations(prescriptionId)
        
        assertEquals(2, prescriptionAdministrations.size)
        assertTrue(prescriptionAdministrations.any { it.administrationId == administration1.administrationId })
        assertTrue(prescriptionAdministrations.any { it.administrationId == administration2.administrationId })
    }
    
    private suspend fun createTestAdministration(
        patientId: PatientId = PatientId.generate(),
        prescriptionId: PrescriptionId = PrescriptionId.generate()
    ): MedicationAdministration {
        return administrationService.createAdministration(
            patientId = patientId,
            prescriptionId = prescriptionId,
            scheduledTime = Instant.now(),
            dosageGiven = 500.0,
            unit = "mg",
            notes = "Test administration"
        )
    }
}