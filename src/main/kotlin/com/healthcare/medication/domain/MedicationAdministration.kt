package com.healthcare.medication.domain

import java.time.Instant

data class MedicationAdministration(
    val administrationId: AdministrationId,
    val patientId: PatientId,
    val prescriptionId: PrescriptionId,
    val scheduledTime: Instant,
    val actualTime: Instant?,
    val dosageGiven: Double?,
    val unit: String,
    val administeredBy: String?,
    val status: AdministrationStatus,
    val notes: String?,
    val createdAt: Instant
) {
    companion object {
        fun create(
            patientId: PatientId,
            prescriptionId: PrescriptionId,
            scheduledTime: Instant,
            dosageGiven: Double?,
            unit: String,
            administeredBy: String?,
            notes: String?
        ): MedicationAdministration {
            return MedicationAdministration(
                administrationId = AdministrationId.generate(),
                patientId = patientId,
                prescriptionId = prescriptionId,
                scheduledTime = scheduledTime,
                actualTime = null,
                dosageGiven = dosageGiven,
                unit = unit,
                administeredBy = administeredBy,
                status = AdministrationStatus.SCHEDULED,
                notes = notes,
                createdAt = Instant.now()
            )
        }
    }
    
    fun markAsCompleted(actualTime: Instant, administeredBy: String): MedicationAdministration {
        return copy(
            actualTime = actualTime,
            administeredBy = administeredBy,
            status = AdministrationStatus.COMPLETED
        )
    }
    
    fun markAsMissed(): MedicationAdministration {
        return copy(status = AdministrationStatus.MISSED)
    }
    
    fun updateStatus(newStatus: AdministrationStatus): MedicationAdministration {
        return copy(status = newStatus)
    }
}