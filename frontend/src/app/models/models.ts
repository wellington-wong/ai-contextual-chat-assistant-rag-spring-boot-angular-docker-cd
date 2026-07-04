export interface ChatSession {
    id: number;
    title: string;

    createdAt: string;
    updatedAt: string;
}

export interface ChatMessage {
    id: number;
    role: 'user' | 'assistant';




    content: string;
    createdAt: string;
}

export interface DocumentInfo {
    id: number;
    name: string;



    fileType: string;
    createdAt: string;
}