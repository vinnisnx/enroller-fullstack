export default function MeetingsList({meetings, onDelete, signToMeeting}) {

    return (
        <table>
            <thead>
            <tr>
                <th>Nazwa spotkania</th>
                <th>Opis</th>
                <th>Uczestnicy</th>
                <th>Akcje</th>
            </tr>
            </thead>
            <tbody>
            {
                meetings.map((meeting, index) => <tr key={index}>
                    <td>{meeting.title}</td>
                    <td>{meeting.description}</td>
                    {/*{index === employees.length - 1}*/}
                    <td>{meeting.participants.map((participant, index) => <span key={index}>{participant.login}<span>{(index === meeting.participants.length - 1) ? "" : ", "}</span></span>)}</td>
                    <td>
                        <button type="button" className="button-outline delete-button" onClick={() => onDelete(meeting)}>Usun</button>
                        <button type="button" className="button-outline" onClick={() => signToMeeting(meeting)}>Zapisz sie</button>
                    </td>
                </tr>)
            }
            </tbody>
        </table>
    );
}
