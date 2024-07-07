import { createContext, useContext, useReducer } from "react";
import React from "react";
import { api } from "./Api";

const reducer = (state, action) => {
    if (action.type === "FETCH_BOTS") {
        const newBots = action.payload.bots
        return {
            ...state,
            chatbots: newBots
        }
    }
    if (action.type === "SET_SELECTED_BOT") {
        const newSelectedBot = action.payload.bot
        localStorage.setItem("selectedBot", JSON.stringify(newSelectedBot))
        return {
            ...state,
            selectedBot: newSelectedBot
        }
    }
    if (action.type === "FETCH_EMBEDDINGS") {
        const newEmbeddings = action.payload.embeddings
        return {
            ...state,
            embeddings: newEmbeddings
        }
    }
}

const localSelectedBot = JSON.parse(localStorage.getItem("selectedBot"))

const initialState = {
    chatbots: [],
    selectedBot: localSelectedBot,
    embeddings: []
}

const AppStateContext = createContext({
    ...initialState
})

export default function AppStateContextProvider({
    children
}) {

    const [state, dispatch] = useReducer(reducer, initialState);

    const fetchBots = async () => {
        const response = await api.get("/faqassistant/v1")
        dispatch({
            type: "FETCH_BOTS",
            payload: {
                bots: response.data
            }
        })
    }

    const fetchEmbeddings = async () => {
        const response = await api.get(`/embedding/v1/${state.selectedBot.id}`)
        dispatch({
            type: "FETCH_EMBEDDINGS",
            payload: {
                embeddings: response.data
            }
        })
    }

    const createEmbedding = async (payload) => {
        const formData = new FormData()
        formData.append("file", payload.file)
        formData.append("name", payload.name)
        formData.append("assistantId", state.selectedBot.id)
        formData.append("async", true)
        await api.post("/embedding/v1", formData)
        await fetchEmbeddings()
    }

    const deleteEmbedding = async (embeddingId) => {
        const response = await api.delete(`/embedding/v1/${embeddingId}`)
        if (response.status ===  200) {
            await fetchEmbeddings()
        }
    }

    const createBot = async (payload) => {
        const response = await api.post("/faqassistant/v1", payload)
        if (response.status === 200) {
            await fetchBots()
        }
    }

    const deleteBot = async (botId) => {
        const response = await api.delete(`/faqassistant/v1/${botId}`)
        if (response.status ===  200) {
            await fetchBots()
        }
    }

    const setSelectedBot = (bot) => {
        dispatch({
            type: "SET_SELECTED_BOT",
            payload: {
                bot
            }
        })
    }

    const contextValue = {
        ...state,
        fetchBots,
        createBot,
        deleteBot,
        setSelectedBot,
        fetchEmbeddings,
        createEmbedding,
        deleteEmbedding,
    }

    return (
        <AppStateContext.Provider value={contextValue}>{children}</AppStateContext.Provider>
    )

}

export const useAppStateContext = () => useContext(AppStateContext)