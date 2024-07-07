import { Button, Divider, Form, Input, Modal, Row, Space, Table, Tooltip, Typography } from "antd";
import { useAppStateContext } from "../AppState";
import { ArrowLeftOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import React from "react";
import ChatBox from "./ChatBox";

export default function ChatBotEmbeddingView() {

    const tableColumns = [
        {
            title: "Name",
            dataIndex: "name",
            key: "name"
        },
        {
            title: "Status",
            dataIndex: "status",
            key: "status"
        },
        {
            title: "Action",
            key: "action",
            render: (_, record) => (
                <Space size="middle">
                    <Button onClick={() => handleDeleteClick(record)} type="link" size="small">Delete</Button>
                </Space>
            )
        }
    ]

    const { embeddings, selectedBot, fetchEmbeddings, createEmbedding, deleteEmbedding } = useAppStateContext()
    const [showCreate, setShowCreate] = useState(false);
    const [ showBot, setShowBot ] = useState(false)
    const [formData, setFormData] = useState({})
    const navigate = useNavigate()

    const handleDeleteClick = async (embedding) => {
        await deleteEmbedding(embedding.id)
    }

    const handleFormSubmit = async (values) => {
        await createEmbedding(formData)
        setShowCreate(false)
        setFormData({})
    }

    const handleCreateEmbeddingClick = (e) => {
        setShowCreate(true)
    }

    const handleSelectFileChange = (e) => {
        setFormData({...formData, file: e.target.files[0]})
    }

    const handleNameChange = (e) => {
        setFormData({ ...formData, name: e.target.value })
    }

    useEffect(() => {
        fetchEmbeddings()
    }, [])

    const handleOpenBotClick = (e) => {
        setShowBot(true);
    }

    const chatBotStyle = {
        position: "absolute", 
        right: 0, 
        bottom: 0, 
        border: "2px solid rgba(0, 0, 0, 0.25)",
        boxShadow: "0px 0px 5px 5px rgba(0, 0, 0, 0.25)",
        borderRadius: "6px"
    }

    return (
        <div>
            <Row justify="space-between">
                <Space>
                    <Button type="link" onClick={() => navigate("/chat-bots")}><ArrowLeftOutlined style={{ fontSize: "25px", marginBottom: "10px" }} /></Button>
                    <Typography.Title level={2} >{`${selectedBot.name}`}</Typography.Title>
                </Space>
                <Space>
                    <Button onClick={handleOpenBotClick} size="large" type="primary">Open Bot</Button>
                    <Button onClick={handleCreateEmbeddingClick} size="large" type="primary">Create Embedding</Button>
                </Space>
            </Row>

            <Table columns={tableColumns} dataSource={embeddings} />

            <Modal open={showCreate} closable={false} footer title="Create Embedding">
                <div>
                    <Divider />
                    <Form autoComplete="off" onFinish={handleFormSubmit}>
                        <Form.Item label="Name" name="name">
                            <Input onChange={handleNameChange} />
                        </Form.Item>
                        <Form.Item label="File" name="file" required rules={[{required: true, message: "Please select a file"}]}>
                            <Space>
                                <input type="file" name="file" onChange={handleSelectFileChange} />
                                <Tooltip title=".pdf, .doc, .docx files can be uploaded">
                                    <Typography.Link>Supported Files</Typography.Link>
                                </Tooltip>
                            </Space>
                        </Form.Item>
                        <Form.Item wrapperCol={{ offset: 3}}>
                            <Button type="primary" htmlType="submit" size="medium">Create</Button>
                            <Button type="link" onClick={() => setShowCreate(false)} htmlType="reset" size="medium">Close</Button>
                        </Form.Item>
                    </Form>
                </div>
            </Modal>
            <Row justify={"end"} style={chatBotStyle}>
                {
                    showBot && <ChatBox onClose={() => setShowBot(false)} />
                }
            </Row>
        </div>
    )

}