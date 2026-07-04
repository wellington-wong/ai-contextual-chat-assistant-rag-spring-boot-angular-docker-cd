import {
    Component, OnInit, AfterViewChecked,
    ViewChild, ElementRef, ChangeDetectorRef


} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ApiService } from '../../services/api.service';
import { ChatSession, ChatMessage, DocumentInfo } from '../../models/models';

@Component({


    selector: 'app-chat',
    standalone: true,
    imports: [CommonModule, FormsModule],

    templateUrl: './chat.component.html',
    styleUrls: ['./chat.component.css']
})



export class ChatComponent implements OnInit, AfterViewChecked {
    @ViewChild('messageContainer') messageContainer!: ElementRef;
    @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

    sessions: ChatSession[] = [];
    activeSession: ChatSession | null = null;
    messages: ChatMessage[] = [];



    documents: DocumentInfo[] = [];
    inputText = '';
    isSending = false;

    isUploading = false;
    showDocPanel = false;
    uploadError = '';



    chatError = '';

    constructor(private api: ApiService, private cdr: ChangeDetectorRef) {}

    ngOnInit(): void {
        this.loadSessions();
        this.loadDocuments();
    }


    ngAfterViewChecked(): void {
        this.scrollToBottom();
    }

    // -- Sessions ------------------------------------------------------

    loadSessions(): void {
        this.api.getSessions().subscribe({


            next: (s) => {
                this.sessions = s;
                if (s.length > 0 && !this.activeSession) {

                    this.selectSession(s[0]);
                }
            }
        });

    }

    selectSession(session: ChatSession): void {
        this.activeSession = session;
        this.messages = [];

        this.chatError = '';
        this.api.getMessages(session.id).subscribe({
            next: (m) => (this.messages = m),

        });
    }

    newSession(): void {
        this.api.createSession('New Chat').subscribe({
            next: (s) => {

                this.sessions.unshift(s);
                this.activeSession = s;
                this.messages = [];
            }
        });
    }

    deleteSession(session: ChatSession, event: Event): void {
        event.stopPropagation();
        this.api.deleteSession(session.id).subscribe({

            next: () => {

                this.sessions = this.sessions.filter((s) => s.id !== session.id);
                if (this.activeSession?.id === session.id) {
                    this.activeSession = this.sessions[0] ?? null;

                    this.messages = [];
                    if (this.activeSession) {
                        this.selectSession(this.activeSession);
                    }
                }
            }

        })
    }

    // -- Chat ----------------------------------------------------------

    sendMessage(): void {
        if (!this.inputText.trim() || this.isSending || !this.activeSession) return;
        const text = this.inputText.trim();


        this.inputText = '';
        this.chatError = '';

        // Optimistic user message
        this.messages.push({
            id: Date.now(),

            role: 'user',


            content: text,
            createdAt: new Date().toISOString(),
        });


        this.isSending = true;
        this.api.sendMessage(this.activeSession.id, text).subscribe({



            next: (reply) => {
                this.messages.push(reply);
                // Refresh session list for title update

                this.loadSessions();
                this.isSending = false;
            },
            error: (err) => {


                this.chatError = err.error?.message ?? 'Failed to get a response. Please try again.'
                this.isSending = false;
            },
        });
    }

    onKeyDown(event: KeyboardEvent): void {
        if (event.key == 'Enter' && !event.shiftKey) {

            event.preventDefault();
            this.sendMessage();
        }

    }

    // -- Documents -------------------------------------------------------------




    loadDocuments(): void {
        this.api.getDocuments().subscribe({ next: (d) => (this.documents = d) });
    }

    triggerFileUpload(): void {
        this.fileInput.nativeElement.click();
    }



    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];

        if (!file) return;
        this.uploadError = '';
        this.isUploading = true;



        this.api.uploadDocument(file).subscribe({
            next: (doc) => {
                this.documents.unshift(doc);

                this.isUploading = false;
                input.value = '';
            },
            error: (err) => {


                this.uploadError = err.error?.message ?? 'Upload failed.';
                this.isUploading = false;
                input.value = '';
            },
        });
    }

    deleteDocument(doc: DocumentInfo, event: Event): void {


        event.stopPropagation();
        this.api.deleteDocument(doc.id).subscribe({
            next: () => (this.documents = this.documents.filter((d) => d.id !== doc.id)),
        });
    }

    // -- Utils ------------------------------------------------------



    private scrollToBottom(): void {
        try {
            const el = this.messageContainer.nativeElement;
            el.scrollTop = el.scrollHeight;

        } catch {}
    }



    trackById(_: number, item: { id: number }) {
        return item.id;
    }

}