package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> findMeetings(@RequestParam(value = "title", defaultValue = "") String title,
                                          @RequestParam(value = "description", defaultValue = "") String description,
                                          @RequestParam(value = "sort", defaultValue = "") String sortMode,
                                          @RequestParam(value = "participantLogin", defaultValue = "") String participantLogin) {

        Participant foundParticipant = null;
        if (!participantLogin.isEmpty()) {
            foundParticipant = participantService.findByLogin(participantLogin);
        }
        Collection<Meeting> meetings = meetingService.findMeetings(title, description, foundParticipant, sortMode);
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        if (meetingService.alreadyExist(meeting)) {
            return new ResponseEntity<String>("Unable to add. A meeting with title " + meeting.getTitle() + " and date "
                    + meeting.getDate() + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        Meeting currentMeeting = meetingService.findById(id);
        if (currentMeeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.setId(currentMeeting.getId());
        meetingService.update(meeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> setParticipantInMeeting(@PathVariable("id") long id, @RequestBody String login) {
        Meeting meeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);
        if (participant == null && !login.isEmpty()) {
            Participant newParticipant = new Participant();
            newParticipant.setLogin(login);
            participant = newParticipant;
            participantService.add(participant);
        }
        if (meeting == null && participant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        System.out.println(participant.toString() +  login.toString());
        meeting.addParticipant(participant);
        meetingService.update(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meetingService.findById(meeting.getId()).getParticipants();
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipantFromMeeting(@PathVariable("id") long id, String login) {
        Meeting meeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);
        if (meeting == null && participant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.removeParticipant(participant);
        meetingService.update(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

}