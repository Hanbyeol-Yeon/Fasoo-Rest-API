package com.fasoo.prac.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.reject("wrongPrices", "Values fo prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }

        // TODO BeginEventDateTime
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        if (beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
        		beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        		beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
        	errors.rejectValue("beginEventDateTime", "wrongValue", "beginEventDateTime is wrong");
        }
        
        // TODO CloseEnrollmentDateTime
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if (closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
        		closeEnrollmentDateTime.isAfter(eventDto.getBeginEventDateTime()) ||
        		closeEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime())) {
        	errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong");
        }
    }

}