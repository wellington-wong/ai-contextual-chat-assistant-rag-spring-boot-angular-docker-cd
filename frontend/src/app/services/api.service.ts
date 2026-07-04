import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';



import { Observable } from 'rxjs';
import { ChatSession, ChatMessage, DocumentInfo } from '../models/models';

const BASE = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class ApiService {



    constructor(private http: HttpClient) {}

    // -- Sessions ----------------------------------------

    getSessions(): Observable<ChatSession[]> {
        return this.http.get<ChatSession[]>(`${BASE}/sessions`);
    }



    createSession(title: string): Observable<ChatSession> {
        return this.http.post<ChatSession>(`${BASE}/sessions`, { title });
    }

    deleteSession(id: number): Observable<void> {
        return this.http.delete<void>(`${BASE}/sessions/${id}`);
    }



    // -- Messages --------------------------------------

    getMessages(sessionId: number): Observable<ChatMessage[]> {
        return this.http.get<ChatMessage[]>(`${BASE}/sessions/${sessionId}/messages`);
    }

    sendMessage(sessionId: number, message: string): Observable<ChatMessage> {
        return this.http.post<ChatMessage>(`${BASE}/sessions/${sessionId}/chat`, {message});

    }

    // -- Documents -------------------------------------

    getDocuments(): Observable<DocumentInfo[]> {
        return this.http.get<DocumentInfo[]>(`${BASE}/documents`);
    }

    uploadDocument(file: File): Observable<DocumentInfo> {


        const form = new FormData();
        form.append('file', file);
        return this.http.post<DocumentInfo>(`${BASE}/documents/upload`, form);
    }

    deleteDocument(id: number): Observable<void> {
        return this.http.delete<void>(`${BASE}/documents/${id}`);
    }
}