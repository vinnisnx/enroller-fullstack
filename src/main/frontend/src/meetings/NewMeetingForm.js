import {useState} from "react";
import {Loader} from '../loader';

export default function NewMeetingForm({onSubmit}) {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [date, setDate] = useState('');
    const [loading, setLoading] = useState(false);

    async function submit(event) {

        event.preventDefault();
        setLoading(true);
        try {
            await onSubmit({title, description, date, participants: []});
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
        {loading ? (
            <Loader />
            ) : (
        <form onSubmit={submit}>
            <h3>Dodaj nowe spotkanie</h3>
            <label>Nazwa</label>
            <input type="text" value={title}
                   onChange={(e) => setTitle(e.target.value)}/>
            <label>Opis</label>
            <textarea value={description}
                      onChange={(e) => setDescription(e.target.value)}></textarea>
            <label>Data</label>
            <textarea value={date}
                      onChange={(e) => setDate(e.target.value)}></textarea>
            <button>Dodaj</button>
        </form>
        )}
        </div>
    );
}