package com.healthcare.medication.infrastructure

import com.healthcare.medication.domain.*
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Requires(env = ["test"])
class InMemoryAdministrationRepository : AdministrationRepository {
    
    private val administrations = ConcurrentHashMap<AdministrationId, MedicationAdministration>()
    
    override suspend fun save(administration: MedicationAdministration): MedicationAdministration {
        administrations[administration.administrationId] = administration
        return administration
    }
    
    override suspend fun findById(id: AdministrationId): MedicationAdministration? {
        return administrations[id]
    }
    
    override suspend fun findByPatientId(patientId: PatientId): List<MedicationAdministration> {
        return administrations.values.filter { it.patientId == patientId }
    }
    
    override suspend fun findByPrescriptionId(prescriptionId: PrescriptionId): List<MedicationAdministration> {
        return administrations.values.filter { it.prescriptionId == prescriptionId }
    }
    
    override suspend fun findByDate(date: LocalDate): List<MedicationAdministration> {
        return administrations.values.filter { administration ->
            val administrationDate = administration.scheduledTime.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            administrationDate == date
        }
    }
    
    override suspend fun update(administration: MedicationAdministration): MedicationAdministration {
        administrations[administration.administrationId] = administration
        return administration
    }
    
    fun clear() {
        administrations.clear()
    }
}