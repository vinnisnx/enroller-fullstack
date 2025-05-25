export default function MeetingsList({meetings, onDelete, onAddUser, username}) {
    function checkUserOnMeeting(meeting) {
        return fetch(`/api/${meeting.id}/meetings/${username}`, {
            method: 'GET',
        })
    }

    let content;

    function checker(response, meeting) {
        if (response.ok) {
            content = <button type="button" className="button" onClick={() => onAddUser(meeting, username)}>Usuń się</button>
        } else {
            content = <button type="button" className="button" onClick={() => onAddUser(meeting, username)}>Zapisz się</button>
        }
    }


    return (
        <table>
            <thead>
            <tr>
                <th>Nazwa spotkania</th>
                <th>Opis</th>
                <th>Uczęstniki</th>
                <th>Akcje</th>
            </tr>
            </thead>
            <tbody>
            {
                meetings.map((meeting, index) => <tr key={index}>
                    <td>{meeting.title}</td>
                    <td>{meeting.description}</td>
                    <td>{meeting.participants.map((user, index) =>
                        (<tr key={index}>
                            {user.login}
                        </tr>))}
                    </td>
                    <td>

                        {/*{checkUserOnMeeting(meeting)}*/}
                        {/*{checker(checkUserOnMeeting(meeting), meeting)}*/}
                        {/*{content}*/}
                        <button type="button" className="button" onClick={() => onAddUser(meeting, username)}>Zapisz się</button>
                        <button type="button" className="button-outline" onClick={() => onDelete(meeting)}>Usun</button>
                    </td>
                </tr>)
            }
            </tbody>
        </table>
    );
}
