package com.fasoo.prac.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.validation.Valid;
import me.whiteship.demoinfleanrestapi.accounts.Account;
import me.whiteship.demoinfleanrestapi.accounts.CurrentUser;
import me.whiteship.demoinfleanrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	
	private final EventRepository eventRepository;
	
	private final ModelMapper modelMapper;
	
	private final EventValidator eventValidator;
	
	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		
		eventValidator.validate(eventDto, errors);
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		
		Event event = modelMapper.map(eventDto, Event.class);
		
		Event newEvent = this.eventRepository.save(event);
		
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createdUri).body(event);
	}
	

    @GetMapping
    public ResponseEntity queryEvents(final Pageable pageable, final PagedResourcesAssembler<Event> assembler) {
        final Page<Event> events = this.eventRepository.findAll(pageable);
        final PagedModel<EntityModel<Event>> model = assembler.toModel(events, e -> EntityModel.of(e, linkTo(EventController.class).slash(e.getId()).withSelfRel()));
        model.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(model);
    }
    
    @GetMapping("/base-price")
    public ResponseEntity queryEventByBasePrice(final Pageable pageable, final PagedResourcesAssembler<Event> assembler, @RequestParam int startBasePrice, @RequestParam int endBasePrice)    
    {
        final Page<Event> events = this.eventRepository.findByBasePriceBetween(startBasePrice, endBasePrice, pageable);
        final PagedModel<EntityModel<Event>> model = assembler.toModel(events, e -> EntityModel.of(e, linkTo(EventController.class).slash(e.getId()).withSelfRel()));
    	
    	return ResponseEntity.ok(model);
    }
}
