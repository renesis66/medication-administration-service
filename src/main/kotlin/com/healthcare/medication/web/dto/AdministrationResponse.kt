package com.healthcare.medication.web.dto

import com.healthcare.medication.domain.AdministrationStatus
import com.healthcare.medication.domain.MedicationAdministration
import io.micronaut.core.annotation.Introspected
import java.time.Instant

@Introspected
data class AdministrationResponse(
    val administrationId: String,
    val patientId: String,
    val prescriptionId: String,
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
        fun from(administration: MedicationAdministration): AdministrationResponse {
            return AdministrationResponse(
                administrationId = administration.administrationId.toString(),
                patientId = administration.patientId.toString(),
                prescriptionId = administration.prescriptionId.toString(),
                scheduledTime = administration.scheduledTime,
                actualTime = administration.actualTime,
                dosageGiven = administration.dosageGiven,
                unit = administration.unit,
                administeredBy = administration.administeredBy,
                status = administration.status,
                notes = administration.notes,
                createdAt = administration.createdAt
            )
        }
    }
}