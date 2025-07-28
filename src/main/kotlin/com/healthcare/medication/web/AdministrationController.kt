package com.healthcare.medication.web

import com.healthcare.medication.application.AdministrationService
import com.healthcare.medication.application.AdministrationNotFoundException
import com.healthcare.medication.domain.AdministrationId
import com.healthcare.medication.domain.PatientId
import com.healthcare.medication.domain.PrescriptionId
import com.healthcare.medication.web.dto.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import jakarta.validation.Valid
import java.time.LocalDate

@Controller("/medication")
@Validated
class AdministrationController(
    private val administrationService: AdministrationService
) {
    
    @Get("/patients/{patientId}/administrations")
    suspend fun getPatientAdministrations(@PathVariable patientId: String): List<AdministrationResponse> {
        val administrations = administrationService.getPatientAdministrations(PatientId.from(patientId))
        return administrations.map { AdministrationResponse.from(it) }
    }
    
    @Post("/administrations")
    suspend fun createAdministration(@Body @Valid request: CreateAdministrationRequest): HttpResponse<AdministrationResponse> {
        val administration = administrationService.createAdministration(
            patientId = PatientId.from(request.patientId),
            prescriptionId = PrescriptionId.from(request.prescriptionId),
            scheduledTime = request.scheduledTime,
            dosageGiven = request.dosageGiven,
            unit = request.unit,
            notes = request.notes
        )
        
        return HttpResponse.created(AdministrationResponse.from(administration))
    }
    
    @Get("/administrations/{id}")
    suspend fun getAdministration(@PathVariable id: String): HttpResponse<AdministrationResponse> {
        return try {
            val administration = administrationService.getAdministration(AdministrationId.from(id))
            HttpResponse.ok(AdministrationResponse.from(administration))
        } catch (e: AdministrationNotFoundException) {
            HttpResponse.notFound()
        }
    }
    
    @Put("/administrations/{id}/status")
    suspend fun updateAdministrationStatus(
        @PathVariable id: String,
        @Body @Valid request: UpdateStatusRequest
    ): HttpResponse<AdministrationResponse> {
        return try {
            val administration = administrationService.updateAdministrationStatus(
                AdministrationId.from(id),
                request.status
            )
            HttpResponse.ok(AdministrationResponse.from(administration))
        } catch (e: AdministrationNotFoundException) {
            HttpResponse.notFound()
        }
    }
    
    @Post("/administrations/{id}/record")
    suspend fun recordAdministration(
        @PathVariable id: String,
        @Body @Valid request: RecordAdministrationRequest
    ): HttpResponse<AdministrationResponse> {
        return try {
            val administration = administrationService.recordAdministration(
                administrationId = AdministrationId.from(id),
                actualTime = request.actualTime,
                administeredBy = request.administeredBy,
                dosageGiven = request.dosageGiven,
                notes = request.notes
            )
            HttpResponse.ok(AdministrationResponse.from(administration))
        } catch (e: AdministrationNotFoundException) {
            HttpResponse.notFound()
        }
    }
    
    @Get("/administrations/daily")
    suspend fun getDailyAdministrations(@QueryValue date: LocalDate): List<AdministrationResponse> {
        val administrations = administrationService.getDailyAdministrations(date)
        return administrations.map { AdministrationResponse.from(it) }
    }
    
    @Get("/prescriptions/{prescriptionId}/administrations")
    suspend fun getPrescriptionAdministrations(@PathVariable prescriptionId: String): List<AdministrationResponse> {
        val administrations = administrationService.getPrescriptionAdministrations(PrescriptionId.from(prescriptionId))
        return administrations.map { AdministrationResponse.from(it) }
    }
}