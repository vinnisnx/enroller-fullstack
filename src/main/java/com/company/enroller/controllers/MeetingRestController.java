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

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantsOfMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting with ID " + id + " doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId,
                                                     @RequestBody Map<String, String> body) {
        String login = body.get("login");
        Meeting meeting = meetingService.findById(meetingId);
        Participant participant = participantService.findByLogin(login);

        if (meeting == null) {
            return new ResponseEntity<>("Meeting with ID " + meetingId + " doesn't exist.", HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("A participant with login " + login + " doesn't exist.", HttpStatus.NOT_FOUND);
        }

        if (meeting.getParticipants().contains(participant)) {
            return new ResponseEntity<>("Participant already in the meeting", HttpStatus.CONFLICT);
        }

        meeting.getParticipants().add(participant);
        meetingService.update(meeting);
        return new ResponseEntity<>(participant, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantOfMeeting(@PathVariable("id") long meetingId,
                                                     @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(meetingId);
        Participant participant = participantService.findByLogin(login);

        if (meeting == null) {
            return new ResponseEntity<>("Meeting with ID " + meetingId + " doesn't exist.", HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("A participant with login " + login + " doesn't exist.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("A participant " + login + " is assigned to the meeting", HttpStatus.OK);
    }
    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long meetingId,
                                                          @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(meetingId);
        Participant participant = participantService.findByLogin(login);

        if (meeting == null) {
            return new ResponseEntity<>("Meeting with ID " + meetingId + " doesn't exist.", HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("A participant with login " + login + " doesn't exist.", HttpStatus.NOT_FOUND);
        }

        if (!meeting.getParticipants().contains(participant)) {
            return new ResponseEntity<>("A participant with login " + login + " doesn't register fot this meeting", HttpStatus.CONFLICT);
        }

        meeting.getParticipants().remove(participant);
        meetingService.update(meeting);
        return new ResponseEntity<>("A participant with login " + login + " has been removed from the meeting", HttpStatus.OK);
    }
}