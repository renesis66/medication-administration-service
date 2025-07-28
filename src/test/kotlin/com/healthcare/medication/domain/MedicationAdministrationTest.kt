package com.healthcare.medication.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class MedicationAdministrationTest {
    
    @Test
    fun `should create medication administration with scheduled status`() {
        val patientId = PatientId.generate()
        val prescriptionId = PrescriptionId.generate()
        val scheduledTime = Instant.now()
        
        val administration = MedicationAdministration.create(
            patientId = patientId,
            prescriptionId = prescriptionId,
            scheduledTime = scheduledTime,
            dosageGiven = 500.0,
            unit = "mg",
            administeredBy = null,
            notes = "Test notes"
        )
        
        assertEquals(patientId, administration.patientId)
        assertEquals(prescriptionId, administration.prescriptionId)
        assertEquals(scheduledTime, administration.scheduledTime)
        assertEquals(500.0, administration.dosageGiven)
        assertEquals("mg", administration.unit)
        assertEquals(AdministrationStatus.SCHEDULED, administration.status)
        assertEquals("Test notes", administration.notes)
        assertNull(administration.actualTime)
        assertNull(administration.administeredBy)
    }
    
    @Test
    fun `should mark administration as completed`() {
        val administration = createTestAdministration()
        val actualTime = Instant.now()
        val administeredBy = "Nurse Johnson"
        
        val completedAdministration = administration.markAsCompleted(actualTime, administeredBy)
        
        assertEquals(AdministrationStatus.COMPLETED, completedAdministration.status)
        assertEquals(actualTime, completedAdministration.actualTime)
        assertEquals(administeredBy, completedAdministration.administeredBy)
    }
    
    @Test
    fun `should mark administration as missed`() {
        val administration = createTestAdministration()
        
        val missedAdministration = administration.markAsMissed()
        
        assertEquals(AdministrationStatus.MISSED, missedAdministration.status)
    }
    
    @Test
    fun `should update administration status`() {
        val administration = createTestAdministration()
        
        val delayedAdministration = administration.updateStatus(AdministrationStatus.DELAYED)
        
        assertEquals(AdministrationStatus.DELAYED, delayedAdministration.status)
    }
    
    private fun createTestAdministration(): MedicationAdministration {
        return MedicationAdministration.create(
            patientId = PatientId.generate(),
            prescriptionId = PrescriptionId.generate(),
            scheduledTime = Instant.now(),
            dosageGiven = 500.0,
            unit = "mg",
            administeredBy = null,
            notes = null
        )
    }
}