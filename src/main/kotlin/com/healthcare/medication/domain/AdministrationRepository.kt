package com.healthcare.medication.domain

import java.time.LocalDate

interface AdministrationRepository {
    suspend fun save(administration: MedicationAdministration): MedicationAdministration
    suspend fun findById(id: AdministrationId): MedicationAdministration?
    suspend fun findByPatientId(patientId: PatientId): List<MedicationAdministration>
    suspend fun findByPrescriptionId(prescriptionId: PrescriptionId): List<MedicationAdministration>
    suspend fun findByDate(date: LocalDate): List<MedicationAdministration>
    suspend fun update(administration: MedicationAdministration): MedicationAdministration
}