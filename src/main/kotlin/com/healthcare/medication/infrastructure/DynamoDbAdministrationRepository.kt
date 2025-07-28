package com.healthcare.medication.infrastructure

import com.healthcare.medication.domain.*
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Singleton
@Requires(notEnv = ["test"])
class DynamoDbAdministrationRepository(
    private val dynamoDbClient: DynamoDbEnhancedClient
) : AdministrationRepository {
    
    private val table: DynamoDbTable<DynamoDbAdministrationRecord> = 
        dynamoDbClient.table("administrations", TableSchema.fromBean(DynamoDbAdministrationRecord::class.java))
    
    override suspend fun save(administration: MedicationAdministration): MedicationAdministration {
        val record = DynamoDbAdministrationRecord.from(administration)
        table.putItem(record)
        return administration
    }
    
    override suspend fun findById(id: AdministrationId): MedicationAdministration? {
        // For findById, we need to scan since we don't have the patientId
        // In a real implementation, you might want to add another GSI for this
        val results = table.scan().items()
        return results.find { it.administrationId == id.toString() }?.toDomain()
    }
    
    override suspend fun findByPatientId(patientId: PatientId): List<MedicationAdministration> {
        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue("PATIENT#$patientId")
                .build()
        )
        
        return table.query(queryConditional)
            .flatMap { it.items() }
            .map { it.toDomain() }
    }
    
    override suspend fun findByPrescriptionId(prescriptionId: PrescriptionId): List<MedicationAdministration> {
        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue("PRESCRIPTION#$prescriptionId")
                .build()
        )
        
        return table.index("GSI1")
            .query(queryConditional)
            .flatMap { it.items() }
            .map { it.toDomain() }
    }
    
    override suspend fun findByDate(date: LocalDate): List<MedicationAdministration> {
        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue("DATE#${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
                .build()
        )
        
        return table.index("GSI2")
            .query(queryConditional)
            .flatMap { it.items() }
            .map { it.toDomain() }
    }
    
    override suspend fun update(administration: MedicationAdministration): MedicationAdministration {
        val record = DynamoDbAdministrationRecord.from(administration)
        table.putItem(record)
        return administration
    }
}