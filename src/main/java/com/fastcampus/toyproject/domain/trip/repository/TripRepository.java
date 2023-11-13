package com.fastcampus.toyproject.domain.trip.repository;

import com.fastcampus.toyproject.domain.trip.entity.Trip;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByBaseTimeEntity_DeletedAtIsFalseOrderByTripId();

    @Query("SELECT DISTINCT t FROM Trip t LEFT outer join FETCH t.itineraryList i WHERE i.baseTimeEntity.deletedAt != null AND t.tripId = :tripId")
    Optional<Trip> findByTripIdAndItineraryDeletedIsFalse(@Param("tripId") Long tripId);

    Optional<List<Trip>> findByTripNameContains(String keyword);
}
