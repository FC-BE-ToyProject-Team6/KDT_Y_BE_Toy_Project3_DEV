package com.fastcampus.toyproject.domain.itinerary.service;

import static com.fastcampus.toyproject.domain.itinerary.exception.ItineraryExceptionCode.DUPLICATE_ITINERARY_ORDER;
import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.NOT_MATCH_BETWEEN_USER_AND_TRIP;
import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.NO_SUCH_TRIP;
import static com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode.TRIP_ALREADY_DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fastcampus.toyproject.common.BaseTimeEntity;
import com.fastcampus.toyproject.common.util.LocationUtil;
import com.fastcampus.toyproject.domain.itinerary.dto.ItineraryRequest;
import com.fastcampus.toyproject.domain.itinerary.dto.ItineraryResponse;
import com.fastcampus.toyproject.domain.itinerary.dto.ItineraryResponseFactory;
import com.fastcampus.toyproject.domain.itinerary.entity.Itinerary;
import com.fastcampus.toyproject.domain.itinerary.entity.ItineraryFactory;
import com.fastcampus.toyproject.domain.itinerary.exception.ItineraryException;
import com.fastcampus.toyproject.domain.itinerary.exception.ItineraryExceptionCode;
import com.fastcampus.toyproject.domain.itinerary.repository.ItineraryRepository;
import com.fastcampus.toyproject.domain.itinerary.type.ItineraryType;
import com.fastcampus.toyproject.domain.trip.dto.TripRequest;
import com.fastcampus.toyproject.domain.trip.dto.TripResponse;
import com.fastcampus.toyproject.domain.trip.entity.Trip;
import com.fastcampus.toyproject.domain.trip.exception.TripException;
import com.fastcampus.toyproject.domain.trip.exception.TripExceptionCode;
import com.fastcampus.toyproject.domain.trip.repository.TripRepository;
import com.fastcampus.toyproject.domain.trip.service.TripService;
import com.fastcampus.toyproject.domain.user.entity.User;
import com.fastcampus.toyproject.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("여정 서비스 테스트")
class ItineraryServiceTest {
    //@InjectMocks
    private ItineraryService itineraryService;
    @Mock
    private TripService tripService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItineraryRepository itineraryRepository;

    private User user;
    private Trip trip;
    private Trip trip_deleted;
    private Trip trip_details;
    private List<ItineraryRequest> itineraryRequestList;
    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    private void setup(){

        user = User.builder()
                .userId(1L)
                .email("test@mail.com")
                .password("1234")
                .build();
        trip = Trip.builder()
                .tripId(1L)
                .tripName("일본여행")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .baseTimeEntity(new BaseTimeEntity())
                .isDomestic(true)
                .itineraryList(List.of())
                .user(user)
                .build();

        trip_deleted = Trip.builder()
                .tripId(1L)
                .tripName("일본여행")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .baseTimeEntity(BaseTimeEntity.builder().deletedAt(now).build())
                .user(user)
                .build();


        ItineraryRequest ir1 = ItineraryRequest.builder()
                .name("비행기")
                .type(ItineraryType.MOVEMENT)
                .startDate(now).endDate(now)
                .departurePlace("인천 공항").arrivalPlace("도쿄 공항")
                .order(1)
                .build();

        ItineraryRequest ir2 = ItineraryRequest.builder()
                .name("도쿄 디즈니 월드")
                .type(ItineraryType.STAY)
                .startDate(now).endDate(now)
                .order(2)
                .build();

        ItineraryRequest ir3 = ItineraryRequest.builder()
                .name("신주쿠 워싱턴 호텔")
                .type(ItineraryType.LODGEMENT)
                .startDate(now).endDate(now)
                .order(3)
                .build();

        itineraryRequestList = new ArrayList<>();
        itineraryRequestList.add(ir1);
        itineraryRequestList.add(ir2);
        itineraryRequestList.add(ir3);

        tripService = new TripService(tripRepository, userRepository);

    }

    @Nested
    @DisplayName("여정 리스트 추가,삭제,수정")
    class Itinerary_CUD{

        TripRequest tripRequest;
        List<Itinerary> itineraryList;
        List<ItineraryResponse> itineraryResponseList = new ArrayList<>();
        TripResponse tripResponse;

        @BeforeEach
        void itinerary_cud_setup(){
            itineraryList = new ArrayList<>();
            for (ItineraryRequest ir : itineraryRequestList) {
                Itinerary itinerary = ItineraryFactory.getItineraryEntity(trip, ir);
                itineraryList.add(itinerary);
                itineraryResponseList.add(ItineraryResponseFactory.getItineraryResponse(itinerary));
            }
        }

        @Test
        @DisplayName("여정 추가 성공")
        void insertItineraries_success(){

            //given
            //when
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(tripRepository.save(any(Trip.class))).thenReturn(trip);
            when(tripRepository.findById(anyLong())).thenReturn(Optional.ofNullable(trip));
            when(tripService.getTripByTripId(anyLong())).thenReturn(trip);
            when(itineraryRepository.saveAll(anyList())).thenReturn(itineraryList);

            List<ItineraryResponse> result = itineraryService.insertItineraries(trip.getTripId(),
                    user.getUserId(), itineraryRequestList);
            //then
            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(itineraryResponseList);
        }

        @Test
        @DisplayName("여정리스트_추가_실패_순서중복")
        void insertItineraries_fail() {
            //givan
            ItineraryRequest ir4 = ItineraryRequest.builder()
                    .name("도톤보리").type(ItineraryType.STAY)
                    .startDate(now).endDate(now).order(3) //ir3과 중복.
                    .build();
            itineraryRequestList.add(ir4);

            //then
            //when
            assertThatExceptionOfType(ItineraryException.class)
                    .isThrownBy(() -> itineraryService.insertItineraries(trip.getTripId(),
                            user.getUserId(), itineraryRequestList))
                    .extracting("errorCode")
                    .isEqualTo(DUPLICATE_ITINERARY_ORDER);
        }

        @Test
        @DisplayName("여정 수정 성공")
        void updateTrip_success(){
            //given
            //when
            when(tripRepository.findById(anyLong())).thenReturn(Optional.of(trip));
            when(tripRepository.save(any(Trip.class))).thenReturn(trip);
            when(itineraryRepository.save(any(Itinerary.class))).thenReturn((Itinerary)itineraryList);
            TripResponse result = tripService.updateTrip(
                    user.getUserId(),
                    trip.getTripId(),
                    tripRequest);
            //then
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(tripResponse);
        }

        @Test
        @DisplayName("여행 수정 실패 해당 여행 없음")
        void updateTrip_no_such_trip(){
            //given
            //when
            when(tripRepository.findById(anyLong())).thenThrow(new TripException(NO_SUCH_TRIP));
            //then
            assertThatExceptionOfType(TripException.class)
                    .isThrownBy(() -> tripService.updateTrip(1L, 1L, tripRequest))
                    .withMessage("해당하는 여행 정보가 없습니다.")
                    .extracting("errorCode")
                    .isEqualTo(NO_SUCH_TRIP);
        }

        @Test
        @DisplayName("여정 삭제 성공")
        void deleteTrip_success(){
            //given
            //when
            when(tripRepository.findById(anyLong())).thenReturn(Optional.of(trip));
            when(tripRepository.save(any(Trip.class))).thenReturn(trip);
            when(itineraryRepository.save(any(Itinerary.class))).thenReturn(
                    (Itinerary) itineraryList);
            List<Long> longs =new ArrayList<>();
            longs.add(1L);

            List<ItineraryResponse> itineraryResponses =
                    itineraryService.deleteItineraries(1L,1L, longs);

            //then
            assertThat(itineraryResponses.get(0).getId()).usingRecursiveComparison()
                    .isEqualTo(1L);
        }

    }

//    @Test
//    void 여정리스트_삽입_성공() {
//        Trip trip1 = tripRepository.save(trip);
//
//        List<ItineraryResponse> itineraryResponseList = itineraryService.insertItineraries(
//                trip1.getTripId(), itineraryRequestList
//        );
//
//        for (ItineraryResponse ir : itineraryResponseList) {
//            System.out.println(ir.getItineraryType() + ": " + ir.getItineraryName() + " " + ir.getItineraryOrder());
//        }
//    }
//
//    @Test
//    void 여정리스트_삽입_실패_tripId_없는경우() {
//        try {
//            List<ItineraryResponse> itineraryResponseList = itineraryService.insertItineraries(
//                    99L, itineraryRequestList
//            );
//        } catch (TripException e) {
//            Assertions.assertEquals(e.getErrorCode(), TripExceptionCode.NO_SUCH_TRIP);
//        }
//    }
//
//    @Test
//    void 여정리스트_삽입_실패_순서중복() {
//        ItineraryRequest ir4 = ItineraryRequest.builder()
//                .name("도톤보리").type(ItineraryType.STAY)
//                .startDate(now).endDate(now).order(3) //ir3과 중복.
//                .build();
//        itineraryRequestList.add(ir4);
//
//        Trip trip1 = tripRepository.save(trip);
//
//        try {
//            List<ItineraryResponse> itineraryResponseList = itineraryService.insertItineraries(
//                    trip1.getTripId(), itineraryRequestList
//            );
//        } catch (ItineraryException e) {
//            Assertions.assertEquals(e.getErrorCode(), ItineraryExceptionCode.DUPLICATE_ITINERARY_ORDER);
//        }
//    }



}