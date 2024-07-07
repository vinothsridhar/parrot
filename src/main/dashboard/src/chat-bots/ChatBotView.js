import { Button, Row, Typography, Modal, Form, Input, Divider, Table, Space } from "antd";
import { useCallback, useEffect, useState } from "react";
import { useAppStateContext } from "../AppState";
import { useNavigate } from "react-router-dom";
import React from "react";

export default function ChatBotView() {

    const tableColumns = [
        {
            title: "Name",
            dataIndex: "name",
            key: "name"
        },
        {
            title: "Action",
            key: "action",
            render: (_, record) => (
                <Space size="middle">
                    <Button onClick={() => handleEmbedClick(record)} type="link" size="small">Embed</Button>
                    <Button onClick={() => handleDeleteClick(record)} type="link" size="small">Delete</Button>
                </Space>
            )
        }
    ]

    const { chatbots, fetchBots, createBot, deleteBot, setSelectedBot } = useAppStateContext()
    const [showCreate, setShowCreate] = useState(false);
    const navigate = useNavigate()

    const handleShowCreateClick = (e) => {
        setShowCreate(!showCreate);
    }

    const handleFormSubmit = async (values) => {
        await createBot(values);
        setShowCreate(false)
    }

    const handleDeleteClick = async (bot) => {
        await deleteBot(bot.id)
    }

    const handleEmbedClick = (bot) => {
        setSelectedBot(bot)
        navigate(`embedding`)
    }

    useEffect(() => { fetchBots() }, [])

    return (
        <div>
            <Row justify={"space-between"}>
                <Typography.Title level={2} >Chat Bots</Typography.Title>
                <Button onClick={handleShowCreateClick} size="large" type="primary">Create New</Button>
            </Row>

            <Table columns={tableColumns} dataSource={chatbots} />

            <Modal open={showCreate} closable={false} footer title="Create Chat Bot">
                <div>
                    <Divider />
                    <Form autoComplete="off" onFinish={handleFormSubmit}>
                        <Form.Item label="Name" name="name" required rules={[{required: true, message: "Please enter chat bot name"}]}>
                            <Input />
                        </Form.Item>
                        <Form.Item wrapperCol={{ offset: 3}}>
                            <Button type="primary" htmlType="submit" size="medium">Create</Button>
                            <Button type="link" onClick={() => setShowCreate(false)} htmlType="reset" size="medium">Close</Button>
                        </Form.Item>
                    </Form>
                </div>
            </Modal>
        </div>
    )
}
