import React from 'react';
import { useState } from "react";

const Form = () => {
    const [formData, setFormData] = useState({
        ssn: "",
        name: "",
        email: "",
        receiver: "",
        title: "",
        message: "",
        isSensitive: false,
        selectedFile: null
      })

    const handleSubmit = () => {
        //implement handleSubmit
    }

    return (
        <div>
            <form>
                <div>
                    <h2>Hvem Sender Inn?</h2>
                    <label>
                        Personnummer
                        <input 
                            required 
                            type="text"  
                            name="ssn" 
                            value={formData.ssn}
                            onChange={(e) => setFormData({...formData, ssn: e.target.value})}
                        />
                    </label>
                    <label>
                        Navn
                        <input 
                            required
                            type="text"  
                            name="name" 
                            value={formData.name}
                            onChange={(e) => setFormData({...formData, name: e.target.value})}
                        />
                    </label>
                    <label>
                        Epost
                        <input 
                            required
                            type="email"  
                            name="email" 
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
                        />
                    </label>
                </div>

                <div>
                    <h2>Hvem er Mottaker?</h2>
                    <label>
                        Mottaker
                        <input 
                            required
                            type="text"  
                            name="receiver" 
                            value={formData.receiver}
                            onChange={(e) => setFormData({...formData, receiver: e.target.value})}
                        />
                    </label>
                </div>

                <div>
                    <h2>Hva skal sendes?</h2>
                    <label>
                        Tittel
                        <input 
                            required
                            type="text"  
                            name="title" 
                            value={formData.title}
                            onChange={(e) => setFormData({...formData, title: e.target.value})}
                        />
                    </label>
                    <label>
                        Kommentar
                        <textarea 
                            required
                            name="message" 
                            value={formData.message}
                            onChange={(e) => setFormData({...formData, message: e.target.value})}
                        />
                    </label>
                </div>

                <div>
                    <h2>Inneholder henvendelsen sensitive eller følsomme opplysninger?</h2>
                    <label>
                        Ja:
                        <input 
                            checked={formData.isSensitive}
                            type="radio"
                            name="isSensitive"
                            onChange={(e) => setFormData({...formData, isSensitive: e.target.checked})}
                        />
                    </label>
                    <label>
                        Nei:
                        <input 
                            checked={!formData.isSensitive}
                            type="radio"
                            name="isSensitive"
                            onChange={(e) => setFormData({...formData, isSensitive: !(e.target.checked)})}
                        />
                    </label>
                </div>

                <div>
                    <label>
                        <input
                            type="file"
                            onChange={(e) => setFormData({...formData, selectedFile: e.target.files[0]})} //only accepts 1 fileupload
                        />
                    </label>
                </div>

                <button type="button" onClick={handleSubmit}>Send Sikker Melding</button>
            </form>
        </div>
    )
}

export default Form