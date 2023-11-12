package com.fastcampus.toyproject.domain.trip.controller;

import com.fastcampus.toyproject.common.dto.ResponseDTO;
import com.fastcampus.toyproject.common.util.DateUtil;
import com.fastcampus.toyproject.domain.trip.dto.TripDetailResponse;
import com.fastcampus.toyproject.domain.trip.dto.TripRequest;
import com.fastcampus.toyproject.domain.trip.dto.TripResponse;
import com.fastcampus.toyproject.domain.trip.service.TripService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 여행과 관련된 Trip Rest Controller trip 삽입, 수정, 삭제, 전체 조회, 상세 조회 기능
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/{userId}/trip")
public class TripController {

    private final TripService tripService;

    @GetMapping("/all")
    public ResponseDTO<List<TripResponse>> getAllTrips(
    ) {
        return ResponseDTO.ok("모든 여행 조회 완료",
            tripService.getAllTrips()
        );
    }


    @GetMapping("/{tripId}")
    public ResponseDTO<TripDetailResponse> getTripDetail(
        @PathVariable Long tripId
    ) {
        return ResponseDTO.ok("상세 여행 조회 완료",
            tripService.getTripDetail(tripId)
        );
    }

    @GetMapping()
    public ResponseDTO<List<TripResponse>> searchTripListByKeyword(
            @RequestParam("keyword") String keyword
    ) {
        System.out.println("keyword : " + keyword);
        Optional<List<TripResponse>> optional = tripService.getTripByKeyword(keyword);
        if (optional.get().size() == 0) {
            return ResponseDTO.ok(
                "검색된 여행이 없습니다.", null
            );
        }
        return ResponseDTO.ok("여행 검색 완료", optional.get());
    }

    @PostMapping()
    public ResponseDTO<TripResponse> insertTrip(
        @PathVariable final Long userId,
        @Valid @RequestBody final TripRequest tripRequest
    ) {
        DateUtil.isStartDateEarlierThanEndDate(
            tripRequest.getStartDate(),
            tripRequest.getEndDate()
        );
        return ResponseDTO.ok("여행 삽입 완료",
            tripService.insertTrip(userId, tripRequest)
        );
    }

    @PutMapping("/{tripId}")
    public ResponseDTO<TripResponse> updateTrip(
        @PathVariable final Long memberId,
        @PathVariable final Long tripId,
        @Valid @RequestBody final TripRequest tripRequest
    ) {
        DateUtil.isStartDateEarlierThanEndDate(
            tripRequest.getStartDate(),
            tripRequest.getEndDate()
        );
        return ResponseDTO.ok("여행 수정 완료",
            tripService.updateTrip(memberId, tripId, tripRequest)
        );
    }

    @DeleteMapping("/{tripId}")
    public ResponseDTO<TripResponse> deleteTrip(
        @PathVariable final Long tripId
    ) {
        return ResponseDTO.ok("여행 삭제 완료",
            tripService.deleteTrip(tripId)
        );
    }
}
