package com.healthcare.medication.web.dto

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.Instant

@Introspected
data class CreateAdministrationRequest(
    @field:NotBlank
    val patientId: String,
    
    @field:NotBlank
    val prescriptionId: String,
    
    @field:NotNull
    val scheduledTime: Instant,
    
    @field:Positive
    val dosageGiven: Double?,
    
    @field:NotBlank
    val unit: String,
    
    val notes: String?
)