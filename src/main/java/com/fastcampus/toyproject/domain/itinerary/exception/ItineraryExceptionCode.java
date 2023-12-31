package com.fastcampus.toyproject.domain.itinerary.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.fastcampus.toyproject.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ItineraryExceptionCode implements ExceptionCode {

    ITINERARY_NOT_MATCH_TRIP(BAD_REQUEST, "ITINERARY_NOT_MATCH_TRIP", "여정과 여행이 일치하지 않습니다."),
    INCORRECT_ITINERARY_ORDER(BAD_REQUEST, "INCORRECT_ITINERARY_ORDER", "잘못된 여정 순서입니다."),
    DUPLICATE_ITINERARY_ORDER(BAD_REQUEST, "DUPLICATE_ITINERARY_ORDER","여정 순서가 중복됩니다."),
    EMPTY_ITINERARY(BAD_REQUEST, "EMPTY_ITINERARY","수정 할 여정 정보가 없습니다."),
    EMPTY_DEPARTURE_PLACE(BAD_REQUEST, "EMPTY_DEPARTURE_PLACE","출발지를 입력하지 않았습니다."),
    EMPTY_ARRIVAL_PLACE(BAD_REQUEST, "EMPTY_ARRIVAL_PLACE","도착지를 입력하지 않았습니다."),
    EMPTY_TRANSPORTATION(BAD_REQUEST, "EMPTY_TRANSPORTATION","교통수단을 입력하지 않았습니다."),
    ILLEGAL_ITINERARY_TYPE(BAD_REQUEST, "ILLEGAL_ITINERARY_TYPE","잘 못 된 여정 타입입니다."),
    ITINERARY_ALREADY_DELETED(BAD_REQUEST, "ITINERARY_ALREADY_DELETED","이미 삭제된 여정입니다."),
    ITINERARY_SAVE_FAILED(INTERNAL_SERVER_ERROR, "ITINERARY_SAVE_FAILED", "여정 저장에 실패하였습니다."),
    NO_ITINERARY(NOT_FOUND, "NO_ITINERARY", "해당되는 여정이 없습니다."),
    NOT_MATCH_BETWEEN_USER_AND_ITINERARY(FORBIDDEN, "NOT_MATCH_BETWEEN_USER_AND_ITINERARY", "로그인 유저와 여정 정보의 유저와 일치하지 않습니다.")

    ;

    private final HttpStatus status;
    private final String code;
    private final String msg;

}
