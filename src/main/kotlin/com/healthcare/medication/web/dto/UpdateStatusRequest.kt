package com.healthcare.medication.web.dto

import com.healthcare.medication.domain.AdministrationStatus
import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotNull

@Introspected
data class UpdateStatusRequest(
    @field:NotNull
    val status: AdministrationStatus
)