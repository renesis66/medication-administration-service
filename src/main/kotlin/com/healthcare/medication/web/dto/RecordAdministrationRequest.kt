package com.healthcare.medication.web.dto

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.Instant

@Introspected
data class RecordAdministrationRequest(
    @field:NotNull
    val actualTime: Instant,
    
    @field:NotBlank
    val administeredBy: String,
    
    @field:Positive
    val dosageGiven: Double?,
    
    val notes: String?
)