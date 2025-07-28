package com.healthcare.medication.application

import com.healthcare.medication.domain.*
import jakarta.inject.Singleton
import java.time.Instant
import java.time.LocalDate

@Singleton
class AdministrationService(
    private val administrationRepository: AdministrationRepository
) {
    
    suspend fun createAdministration(
        patientId: PatientId,
        prescriptionId: PrescriptionId,
        scheduledTime: Instant,
        dosageGiven: Double?,
        unit: String,
        notes: String?
    ): MedicationAdministration {
        val administration = MedicationAdministration.create(
            patientId = patientId,
            prescriptionId = prescriptionId,
            scheduledTime = scheduledTime,
            dosageGiven = dosageGiven,
            unit = unit,
            administeredBy = null,
            notes = notes
        )
        
        return administrationRepository.save(administration)
    }
    
    suspend fun recordAdministration(
        administrationId: AdministrationId,
        actualTime: Instant,
        administeredBy: String,
        dosageGiven: Double?,
        notes: String?
    ): MedicationAdministration {
        val administration = administrationRepository.findById(administrationId)
            ?: throw AdministrationNotFoundException("Administration not found: $administrationId")
        
        val updatedAdministration = administration
            .markAsCompleted(actualTime, administeredBy)
            .copy(
                dosageGiven = dosageGiven ?: administration.dosageGiven,
                notes = notes ?: administration.notes
            )
        
        return administrationRepository.update(updatedAdministration)
    }
    
    suspend fun updateAdministrationStatus(
        administrationId: AdministrationId,
        status: AdministrationStatus
    ): MedicationAdministration {
        val administration = administrationRepository.findById(administrationId)
            ?: throw AdministrationNotFoundException("Administration not found: $administrationId")
        
        val updatedAdministration = administration.updateStatus(status)
        return administrationRepository.update(updatedAdministration)
    }
    
    suspend fun getAdministration(administrationId: AdministrationId): MedicationAdministration {
        return administrationRepository.findById(administrationId)
            ?: throw AdministrationNotFoundException("Administration not found: $administrationId")
    }
    
    suspend fun getPatientAdministrations(patientId: PatientId): List<MedicationAdministration> {
        return administrationRepository.findByPatientId(patientId)
    }
    
    suspend fun getPrescriptionAdministrations(prescriptionId: PrescriptionId): List<MedicationAdministration> {
        return administrationRepository.findByPrescriptionId(prescriptionId)
    }
    
    suspend fun getDailyAdministrations(date: LocalDate): List<MedicationAdministration> {
        return administrationRepository.findByDate(date)
    }
}

class AdministrationNotFoundException(message: String) : RuntimeException(message)