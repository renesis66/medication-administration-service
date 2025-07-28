package com.healthcare.medication.domain

enum class AdministrationStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    MISSED,
    CANCELLED,
    DELAYED
}