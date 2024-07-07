import { Navigate, createBrowserRouter } from "react-router-dom";
import AppLayout from "./layout/AppLayout";
import { lazy } from "react";
import React from "react";

const ChatBotView = lazy(() => import("./chat-bots/ChatBotView"))
const ChatBotEmbeddingView = lazy(() => import("./chat-bots/ChatBotEmbeddingView"))

const router = createBrowserRouter([
    {
        path: "",
        element: <AppLayout />,
        children: [
            {
                index: true,
                element: <Navigate to="/chat-bots" replace />
            },
            {
                path: "/chat-bots",
                element: <ChatBotView />,
            },
            {
                path: "/chat-bots/embedding",
                element: <ChatBotEmbeddingView />
            }
        ]
    }
], {
    basename: "/app"
});

export {
    router
}

