import { useState, useEffect } from 'react';
import {LoaderSmall} from '../loader';

export default function MeetingList({ meetings, username, onAddUser, onRemoveUser, onDelete }) {
    return (
        <table>
            <thead>
            <tr>
                <th>Nazwa spotkania</th>
                <th>Opis</th>
                <th>Data</th>
                <th>Uczestnicy</th>
            </tr>
            </thead>
            <tbody>
            {meetings.map((meeting) => (
                <MeetingRow
                    key={meeting.id}
                    meeting={meeting}
                    username={username}
                    onAddUser={onAddUser}
                    onRemoveUser={onRemoveUser}
                    onDelete={onDelete}
                />
            ))}
            </tbody>
        </table>
    );
}

function MeetingRow({ meeting, username, onAddUser, onRemoveUser, onDelete }) {
    const [isInMeeting, setIsInMeeting] = useState(false);
    const [loadingUser, setLoadingUser] = useState(false);
    const [loadingMeeting, setLoadingMeeting] = useState(false);

    useEffect(() => {
        const isParticipant = meeting.participants.some(user => user.login === username);
        setIsInMeeting(isParticipant);
    }, [meeting.participants, username]);

    const handleEnroll = async () => {
        setLoadingUser(true);
        await onAddUser(meeting, username);
        setIsInMeeting(true);
        setLoadingUser(false);
    };

    const handleUnenroll = async () => {
        setLoadingUser(true);
        await onRemoveUser(meeting, username);
        setIsInMeeting(false);
        setLoadingUser(false);
    };

    const handleDeletion = async () => {
        setLoadingMeeting(true);
        await onDelete(meeting);
        setLoadingMeeting(false);
    };

    return (
        <tr>
            <td>{meeting.title}</td>
            <td>{meeting.description}</td>
            <td>{meeting.date}</td>
            <td>
                {meeting.participants.map((user, idx) => (
                    <div key={idx}>{user.login}</div>
                ))}
            </td>
            <td>
                {loadingUser ? (
                        <LoaderSmall />
                ) : isInMeeting ? (
                    <a  className="button button-outline" onClick={handleUnenroll}>
                        Usuń się
                    </a >
                ) : (
                    <a  className="button" onClick={handleEnroll}>
                        Zapisz się
                    </a >
                )}
                {(meeting.participants.length === 0) ? (
                    loadingMeeting ? (
                        <LoaderSmall />
                        ) : (
                            <a type="button" className="button button-clear" onClick={handleDeletion}>
                                Usuń
                            </a>
                        )

                ) : (
                    <a type="button" className="button button-clear hidden" >
                        Usuń
                    </a>
                    )}
            </td>
        </tr>
    );
}
