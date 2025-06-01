import "milligram";
import './App.css';
import {useState} from "react";
import LoginForm from "./LoginForm";
import UserPanel from "./UserPanel";

function App() {
    const [loggedIn, setLoggedIn] = useState(() => localStorage.getItem('username') || '');

    function login(email) {
        if (email) {
            localStorage.setItem('username', email);
            setLoggedIn(email);
        }
    }

    function logout() {
        localStorage.removeItem('username');
        setLoggedIn('');
    }

    return (
        <div>
            <h1>System do zapisów na zajęcia</h1>
            {loggedIn ? <UserPanel username={loggedIn} onLogout={logout}/> : <LoginForm onLogin={login}/>}
        </div>
    );
}

export default App;
