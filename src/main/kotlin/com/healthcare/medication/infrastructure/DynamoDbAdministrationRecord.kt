package com.healthcare.medication.infrastructure

import com.healthcare.medication.domain.*
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@DynamoDbBean
data class DynamoDbAdministrationRecord(
    @get:DynamoDbPartitionKey
    var pk: String = "",
    
    @get:DynamoDbSortKey
    var sk: String = "",
    
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["GSI1"])
    var gsi1pk: String = "",
    
    @get:DynamoDbSecondarySortKey(indexNames = ["GSI1"])
    var gsi1sk: String = "",
    
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["GSI2"])
    var gsi2pk: String = "",
    
    @get:DynamoDbSecondarySortKey(indexNames = ["GSI2"])
    var gsi2sk: String = "",
    
    var administrationId: String = "",
    var patientId: String = "",
    var prescriptionId: String = "",
    var scheduledTime: String = "",
    var actualTime: String? = null,
    var dosageGiven: Double? = null,
    var unit: String = "",
    var administeredBy: String? = null,
    var status: String = "",
    var notes: String? = null,
    var createdAt: String = ""
) {
    companion object {
        fun from(administration: MedicationAdministration): DynamoDbAdministrationRecord {
            val scheduledTime = administration.scheduledTime
            val date = LocalDate.ofInstant(scheduledTime, java.time.ZoneOffset.UTC)
            
            return DynamoDbAdministrationRecord(
                pk = "PATIENT#${administration.patientId}",
                sk = "ADMIN#${administration.scheduledTime}",
                gsi1pk = "PRESCRIPTION#${administration.prescriptionId}",
                gsi1sk = "ADMIN#${administration.scheduledTime}",
                gsi2pk = "DATE#${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                gsi2sk = "ADMIN#${administration.scheduledTime}",
                administrationId = administration.administrationId.toString(),
                patientId = administration.patientId.toString(),
                prescriptionId = administration.prescriptionId.toString(),
                scheduledTime = administration.scheduledTime.toString(),
                actualTime = administration.actualTime?.toString(),
                dosageGiven = administration.dosageGiven,
                unit = administration.unit,
                administeredBy = administration.administeredBy,
                status = administration.status.name,
                notes = administration.notes,
                createdAt = administration.createdAt.toString()
            )
        }
    }
    
    fun toDomain(): MedicationAdministration {
        return MedicationAdministration(
            administrationId = AdministrationId.from(administrationId),
            patientId = PatientId.from(patientId),
            prescriptionId = PrescriptionId.from(prescriptionId),
            scheduledTime = Instant.parse(scheduledTime),
            actualTime = actualTime?.let { Instant.parse(it) },
            dosageGiven = dosageGiven,
            unit = unit,
            administeredBy = administeredBy,
            status = AdministrationStatus.valueOf(status),
            notes = notes,
            createdAt = Instant.parse(createdAt)
        )
    }
}