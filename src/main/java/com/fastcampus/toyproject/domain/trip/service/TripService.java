package com.fastcampus.toyproject.domain.trip.service;


import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.NOT_MATCH_BETWEEN_USER_AND_TRIP;
import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.NO_SUCH_TRIP;
import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.TRIP_ALREADY_DELETED;
import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.TRIP_SAVE_FAILED;

import com.fastcampus.toyproject.common.BaseTimeEntity;
import com.fastcampus.toyproject.domain.itinerary.entity.Itinerary;
import com.fastcampus.toyproject.domain.trip.dto.TripDetailResponse;
import com.fastcampus.toyproject.domain.trip.dto.TripRequest;
import com.fastcampus.toyproject.domain.trip.dto.TripResponse;
import com.fastcampus.toyproject.domain.trip.entity.Trip;
import com.fastcampus.toyproject.domain.trip.exception.TripException;
import com.fastcampus.toyproject.domain.trip.repository.TripRepository;
import com.fastcampus.toyproject.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    /**
     * trip 아이디를 통한 trip 객체 반환하는 메소드
     *
     * @param tripId
     * @return trip
     */
    public Trip getTripByTripId(Long tripId) {

        Trip trip = tripRepository
            .findById(tripId)
            .orElseThrow(() -> new TripException(NO_SUCH_TRIP));

        if (trip.getBaseTimeEntity().getDeletedAt() != null) {
            throw new TripException(TRIP_ALREADY_DELETED);
        }

        List<Itinerary> list = trip.getItineraryList();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDeleted()) {
                list.remove(i);
                i--;
            }
        }
        Collections.sort(list, Comparator.comparingInt(Itinerary::getItineraryOrder));
        return trip;
    }

    /**
     * user 아이디를 통한 trip 객체 리스트 반환하는 메소드
     *
     * @param userId
     * @return List<TripResponseDTO>
     */
    public Optional<List<TripResponse>> getTripByUserId(Long userId) {
        Optional<List<Trip>> optionalTrips = tripRepository
            .findAllByUser(userId);

        List<TripResponse> tripResponseList = new ArrayList<>();
        for (Trip trip : optionalTrips.get()) {
            if (trip.getBaseTimeEntity().getDeletedAt() != null) continue;
            tripResponseList.add(TripResponse.fromEntity(trip));
        }
        return Optional.ofNullable(tripResponseList);
    }

    /**
     * trip 아이디와 user 아이디를 통해, 해당하는 trip과 그 여정을 반환하는 메소드
     *
     * @param tripId
     * @param userId
     * @return tripDetail
     */
    public TripDetailResponse findByTripIdAndUserId(Long tripId, Long userId) {
        return tripRepository.findByTripIdAndUserId(tripId, userId)
            .map(trip -> TripDetailResponse.fromEntity(trip))
            .orElseThrow(() -> new TripException(NOT_MATCH_BETWEEN_USER_AND_TRIP));
    }

    /**
     * trip 과 userId가 맞는지 검증
     *
     * @param userId
     * @param trip
     */
    private static void isMatchUserAndTrip(Long userId, Trip trip) {
        if (trip.getUser().getUserId() != userId) {
            throw new TripException(NOT_MATCH_BETWEEN_USER_AND_TRIP);
        }
    }

    /**
     * 삭제 되지 않은 trip 전부를 반환하는 메소드
     *
     * @return List<TripResponse>
     */
    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll()
            .stream().filter(trip ->
                trip.getBaseTimeEntity().getDeletedAt() == null
            )
            .map(TripResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * trip과 연관된 itinerary 리스트 반환 (여행 상세 조회)
     *
     * @param tripId
     * @return tripDetail
     */
    @Transactional(readOnly = true)
    public TripDetailResponse getTripDetail(Long tripId) {
        Trip trip = getTripByTripId(tripId);
        return TripDetailResponse.fromEntity(trip);
    }

    /**
     * trip 1개 삽입하는 메소드
     *
     * @param userId
     * @param tripRequest
     * @return tripResponse
     */
    @Transactional
    public TripResponse insertTrip(Long userId, TripRequest tripRequest) {
        Trip trip = Trip.builder()
            .user(userRepository.getReferenceById(userId))
            .tripName(tripRequest.getTripName())
            .startDate(tripRequest.getStartDate())
            .endDate(tripRequest.getEndDate())
            .isDomestic(tripRequest.getIsDomestic())
            .baseTimeEntity(new BaseTimeEntity())
            .build();

        Trip saveTrip = tripRepository.save(trip);
        if (saveTrip == null) {
            throw new TripException(TRIP_SAVE_FAILED);
        }
        return TripResponse.fromEntity(trip);
    }

    /**
     * trip 수정하는 메소드
     *
     * @param userId
     * @param tripId
     * @param tripRequest
     * @return tripResponseDTO
     */
    public TripResponse updateTrip(Long userId, Long tripId, TripRequest tripRequest) {
        Trip existTrip = getTripByTripId(tripId);
        isMatchUserAndTrip(userId, existTrip);

        existTrip.updateFromDTO(tripRequest);

        Trip saveTrip = tripRepository.save(existTrip);
        if (saveTrip == null) {
            throw new TripException(TRIP_SAVE_FAILED);
        }
        return TripResponse.fromEntity(saveTrip);
    }

    /**
     * trip 삭제 및 연관된 itinerary 삭제하는 메소드
     *
     * @param tripId
     */
    public TripResponse deleteTrip(Long userId, Long tripId) {
        Trip existTrip = getTripByTripId(tripId);
        isMatchUserAndTrip(userId, existTrip);

        existTrip.delete();

        Trip saveTrip = tripRepository.save(existTrip);
        if (saveTrip == null) {
            throw new TripException(TRIP_SAVE_FAILED);
        }
        return TripResponse.fromEntity(saveTrip);
    }

    /**
     * keyword 검색을 통한 여행 이름 리스트 출력
     *
     * @param keyword
     * @return List<TripResponse>
     */
    @Transactional(readOnly = true)
    public Optional<List<TripResponse>> getTripByKeyword(String keyword) {
        Optional<List<Trip>> optionalTrips = tripRepository
            .findByTripNameContains(keyword);

        List<TripResponse> tripResponseList = new ArrayList<>();
        for (Trip trip : optionalTrips.get()) {
            if (trip.getBaseTimeEntity().getDeletedAt() != null) {
                continue;
            }
            tripResponseList.add(TripResponse.fromEntity(trip));
        }
        return Optional.ofNullable(tripResponseList);
    }

    @Transactional
    public void updateLikesCount(Long tripId, boolean increase) {
        Trip trip = getTripByTripId(tripId);
        int currentLikes = trip.getLikesCount() != null ? trip.getLikesCount() : 0;

        if (increase) {
            trip.setLikesCount(currentLikes + 1);
        } else if (currentLikes > 0) {
            trip.setLikesCount(currentLikes - 1);
        }

    }



}
