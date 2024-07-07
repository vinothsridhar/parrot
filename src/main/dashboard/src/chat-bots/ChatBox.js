import Chatbot, { createCustomMessage } from "react-chatbot-kit"
import { api } from "../Api";
import 'react-chatbot-kit/build/main.css'
import './Chatbox.css'
import { useAppStateContext } from "../AppState";
import React from "react";
import { Button, Flex, Typography } from "antd";
import { CloseCircleFilled } from "@ant-design/icons";

export default function ChatBox({
    onClose
}) {

    const { selectedBot } = useAppStateContext()

    const config = {
        initialMessages: [createCustomMessage(`I'm an assistant to the the given embedded content`, 'system')],
        botName: selectedBot.name,
        customMessages: {
            system: (props) => <ChatbotCustomSystemMessage {...props} message={props.state.messages.find(msg => msg.payload === props.payload)} />,
            loading: (props) => <ChatbotCustomLoadingMessage {...props} />
        },
        customComponents: {
            userChatMessage: (props) => <ChatbotCustomUserMessage {...props} />,
            userAvatar: (props) => null,
            header: () => <ChatbotCustomHeader onClose={onClose} name={selectedBot.name} />
        }
    };

    return (
        <Chatbot config={config} messageParser={MessageParser} actionProvider={ActionProvider} />
    )

}

const MessageParser = ({ children, actions }) => {
    const parse = (message) => {
        if (message.trim()) {
            actions.handleMessage(message)
        }
    }

    return (
        <div>
            {React.Children.map(children, (child) => {
                return React.cloneElement(child, {
                    parse: parse,
                    actions,
                });
            })}
        </div>
    )
}

const ActionProvider = ({ setState, children }) => {

    const handleMessage = async (message) => {
        const request = {
            query: message,
            assistantId: 1
        }
        setState((prev) => ({
            ...prev,
            messages: [...prev.messages, createCustomMessage('', 'loading')]
        }))
        const response = await api.post("chat/v1", request)
        const id = crypto.randomUUID()
        const replyMessage = createCustomMessage(response.data, 'system', { payload:  { id } })
        setState((prev) => ({
            ...prev,
            messages: [...prev.messages.slice(0, -1), replyMessage]
        }))
    }

    return (
        <div>
            {React.Children.map(children, (child) => {
                return React.cloneElement(child, {
                    actions: {
                        handleMessage,
                    },
                });
            })}
        </div>
    );
}

function ChatbotCustomHeader({
    name,
    onClose
}) {

    const containerStyle = {
        backgroundColor: "#1677ff", 
        padding: "5px 5px 5px 10px", 
        borderRadius: "5px 5px 0px 0px", 
        marginBottom: "5px"
    }

    const closeButtonStyle = {
        color: "white"
    }

    const headerTextStyle = {
        margin: "0", 
        color: "white"
    }

    return (
        <Flex justify={"space-between"} style={containerStyle}>
            <Typography.Title level={4} style={headerTextStyle}>{name}</Typography.Title>
            <Button type="text" size="medium" onClick={onClose} style={closeButtonStyle}><CloseCircleFilled /></Button>
        </Flex>
    )
}

function ChatbotCustomSystemMessage({ message }) {
    return (
        <span><b>System: </b> 
            <pre style={{ whiteSpace: "pre-wrap" }}>
                {message.message}
            </pre>
        </span>
    )
}

function ChatbotCustomLoadingMessage({ state }) {
    return (
        <span>...</span>
    )
}

function ChatbotCustomUserMessage({ message }) {
    return (
        <span><b>You:</b> 
            <pre>{message}</pre>
        </span>
    )
}