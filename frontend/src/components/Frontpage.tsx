import React from "react";
import ReactMarkdown from 'react-markdown'
import { useState, useEffect } from 'react';


const Frontpage = () => {
    const [md, setMd] = useState("")

    useEffect(() => {
            fetch("https://raw.githubusercontent.com/jaflaten/SikkerKommunikasjon/main/README.md")
                .then((res) => (res.text()))
                .then((res) => setMd(res))}, [])
    
    return (
        <div>
            <ReactMarkdown>{md}</ReactMarkdown>
            <button type="button" onClick={() => window.location.assign('/form')}>
                Send et skjema
            </button>
        </div>
    );
}

export default Frontpage
