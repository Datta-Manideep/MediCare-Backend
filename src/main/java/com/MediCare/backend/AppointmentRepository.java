package com.MediCare.backend;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUserIdOrderByIdDesc(Long userId);

    @Modifying
    @Query("UPDATE Appointment a SET a.userId = :userId WHERE a.userId IS NULL")
    int assignUnownedAppointments(@Param("userId") Long userId);
}
