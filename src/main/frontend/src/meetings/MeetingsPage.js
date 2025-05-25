import {useEffect, useState} from "react";
import NewMeetingForm from "./NewMeetingForm";
import MeetingsList from "./MeetingsList";

export default function MeetingsPage({username}) {
    const [meetings, setMeetings] = useState([]);
    const [addingNewMeeting, setAddingNewMeeting] = useState(false);

    async function handleNewMeeting(meeting) {
        const response = await fetch('/api/meetings', {
            method: 'POST',
            body: JSON.stringify(meeting),
            headers: { 'Content-Type': 'application/json' }
        });
        if (response.ok) {
            const newMeeting = await response.json();
            const nextMeetings = [...meetings, newMeeting];
            setMeetings(nextMeetings);
            setAddingNewMeeting(false);
        }
    }

    async function handleDeleteMeeting(meeting) {
        const response = await fetch(`/api/meetings/${meeting.id}`, {
            method: 'DELETE',
        });
        if (response.ok) {
            const nextMeetings = meetings.filter(m => m !== meeting);
            setMeetings(nextMeetings);
        }
    }

    async function handleSignToMeeting(meeting) {
        const response = await fetch(`api/meetings/${meeting.id}/participants`, {
            method: 'POST',
            body: username,
            headers: {'Content-Type': 'application/json'}
        });
        if (response.ok) {
            getMeetings();
        }
    }

    async function handleUnsignForMeeting(meeting) {
        const response = await fetch(`api/meetings/${meeting.id}/participants/${username}`, {
            method: 'DELETE',
        })
        if (response.ok) {
            getMeetings();
        }
    }

    async function editMeeting(meeting) {
        newMeeting;
        const response = await fetch(`api/meetings/${meeting.id}`, newMeeting, {
            method: 'PUT',
            body: JSON.stringify(newMeeting),
            headers: { 'Content-Type': 'application/json' }
        })
        if (response.ok) {
            //     podmiana tylko tego jednego meetingu a nie wolanie wszystkich na nowo
        }

    }

    useEffect(() => {
        const fetchMeetings = async () => {
            getMeetings();
        };
        fetchMeetings();
    }, []);

    async function getMeetings() {
        const response = await fetch(`/api/meetings`);
        if (response.ok) {
            const meetings = await response.json();
            setMeetings(meetings);
        }
    }

    return (
        <div>
            <h2>Zajęcia ({meetings.length})</h2>
            {
                addingNewMeeting
                    ? <NewMeetingForm onSubmit={(meeting) => handleNewMeeting(meeting)}/>
                    : <button onClick={() => setAddingNewMeeting(true)}>Dodaj nowe spotkanie</button>
            }
            {meetings.length > 0 &&
                <MeetingsList meetings={meetings} username={username}
                              onDelete={handleDeleteMeeting} signToMeeting={handleSignToMeeting}
                              unsignFromMeeting={handleUnsignForMeeting}/>}
        </div>
    )
}
